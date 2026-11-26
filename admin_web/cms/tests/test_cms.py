import io
import tempfile
from pathlib import Path
from django.contrib.auth import get_user_model
from django.core.exceptions import PermissionDenied, ValidationError
from django.core.files.uploadedfile import SimpleUploadedFile
from django.test import TestCase
from PIL import Image

from cms.media_pipeline import process_uploaded_image
from cms.models import (
    AdminProfile,
    ContentAuditLog,
    CropEntry,
    HelplineContactEntry,
    LivestockEntry,
)
from cms.workflow import transition_entry_status

User = get_user_model()


class CmsWorkflowAndSecurityTests(TestCase):
    def setUp(self):
        self.editor = User.objects.create_user(
            username="editor1", password="StrongEditorPass!2026"
        )
        AdminProfile.objects.create(
            user=self.editor, role="content_editor", full_name="Field Editor"
        )

        self.reviewer = User.objects.create_user(
            username="reviewer1", password="StrongReviewerPass!2026"
        )
        AdminProfile.objects.create(
            user=self.reviewer, role="technical_reviewer", full_name="Agronomist Reviewer"
        )

        self.super_admin = User.objects.create_user(
            username="admin1",
            password="StrongAdminPass!2026",
            is_superuser=True,
            is_staff=True,
        )
        AdminProfile.objects.create(
            user=self.super_admin, role="super_admin", full_name="Chief Admin"
        )

        self.outsider = User.objects.create_user(
            username="outsider", password="StrongOutsiderPass!2026"
        )

    def test_unauthenticated_cms_redirects_and_outsider_forbidden(self):
        res = self.client.get("/cms/")
        self.assertEqual(res.status_code, 302)
        self.assertIn("/cms/login/", res.url)

        self.client.force_login(self.outsider)
        res_forbidden = self.client.get("/cms/")
        self.assertEqual(res_forbidden.status_code, 403)

    def test_english_only_cms_dashboard_loads_for_authorized_editor(self):
        self.client.force_login(self.editor)
        res = self.client.get("/cms/")
        self.assertEqual(res.status_code, 200)
        body = res.content.decode("utf-8")
        self.assertIn("Editorial Overview", body)
        self.assertIn("Bakaiya Krishi Saathi Admin", body)

    def test_workflow_prevents_publishing_without_source_and_enforces_rbac(self):
        crop = CropEntry.objects.create(
            id="crop-test-barley",
            category_id="cat-cereal",
            name_ne="जौ",
            name_en="Barley",
            description_ne="परीक्षण बाली",
            source_reference="",
            created_by=self.editor,
        )

        with self.assertRaises(ValidationError):
            transition_entry_status(crop, "in_review", self.editor, "crop")

        crop.source_reference = "NARC Agronomy Division"
        crop.save()

        transition_entry_status(crop, "in_review", self.editor, "crop")
        self.assertEqual(crop.review_status, "in_review")
        self.assertFalse(crop.is_published)

        with self.assertRaises(PermissionDenied):
            transition_entry_status(crop, "approved", self.editor, "crop")

        self_crop = CropEntry.objects.create(
            id="crop-self-created",
            category_id="cat-cereal",
            name_ne="कोदो",
            name_en="Finger Millet",
            description_ne="कोदो बाली",
            source_reference="NARC",
            review_status="in_review",
            created_by=self.reviewer,
        )
        with self.assertRaises(PermissionDenied):
            transition_entry_status(self_crop, "approved", self.reviewer, "crop")

        transition_entry_status(crop, "approved", self.reviewer, "crop")
        transition_entry_status(crop, "published", self.reviewer, "crop")
        self.assertTrue(crop.is_published)
        self.assertEqual(
            ContentAuditLog.objects.filter(target_id="crop-test-barley").count(), 3
        )

    def test_public_api_hides_draft_and_in_review_content(self):
        CropEntry.objects.create(
            id="crop-pub",
            name_ne="धान",
            name_en="Paddy",
            description_ne="प्रकाशित धान",
            source_reference="NARC",
            review_status="published",
            is_published=True,
        )
        CropEntry.objects.create(
            id="crop-draft",
            name_ne="परीक्षण",
            name_en="DraftCrop",
            description_ne="मस्यौदा",
            review_status="draft",
            is_published=False,
        )

        LivestockEntry.objects.create(
            id="animal-goat",
            species_code="goat",
            name_ne="बाख्रा",
            name_en="Goat",
            summary_ne="प्रकाशित बाख्रा",
            source_reference="Vet Office",
            review_status="published",
            is_published=True,
        )
        LivestockEntry.objects.create(
            id="animal-sheep",
            species_code="sheep",
            name_ne="भेडा",
            name_en="Sheep",
            summary_ne="समीक्षामा",
            review_status="in_review",
            is_published=False,
        )
        HelplineContactEntry.objects.create(
            id="help-1",
            name_ne="बकैया कृषि सेवा",
            designation_ne="कृषि प्राविधिक",
            department_en="Bakaiya Agriculture Desk",
            ward_ne="बकैया",
            mobile_number="9868445081",
            source_reference="bakaiyakrishi.gov.np",
            is_verified=True,
            is_published=True,
        )

        crops_json = self.client.get("/api/v1/public/crops/").json()
        self.assertEqual(crops_json["count"], 1)
        self.assertEqual(crops_json["results"][0]["id"], "crop-pub")

        livestock_json = self.client.get("/api/v1/public/livestock/").json()
        self.assertEqual(livestock_json["count"], 1)
        self.assertEqual(livestock_json["results"][0]["species_code"], "goat")

        helpline_json = self.client.get("/api/v1/public/helpline/").json()
        self.assertEqual(helpline_json["count"], 1)
        self.assertEqual(helpline_json["results"][0]["mobile_number"], "9868445081")

    def test_media_pipeline_rejects_svg_script_and_generates_webp_derivatives(self):
        with tempfile.TemporaryDirectory() as tmp_dir:
            tmp_path = Path(tmp_dir)
            fake_svg = SimpleUploadedFile(
                "evil.svg", b"<svg onload='alert(1)'></svg>", content_type="image/svg+xml"
            )
            with self.assertRaises(ValidationError):
                process_uploaded_image(fake_svg, tmp_path, "asset-bad")

            buf = io.BytesIO()
            Image.new("RGB", (1200, 900), color=(30, 120, 60)).save(buf, format="PNG")
            valid_png = SimpleUploadedFile(
                "goat.png", buf.getvalue(), content_type="image/png"
            )

            meta = process_uploaded_image(valid_png, tmp_path, "asset-good")
            self.assertEqual(meta["mime_type"], "image/png")
            self.assertEqual(meta["width_px"], 1200)
            self.assertTrue((tmp_path / meta["thumb_path"]).exists())
            self.assertTrue((tmp_path / meta["medium_path"]).exists())
            self.assertTrue((tmp_path / meta["original_path"]).exists())
