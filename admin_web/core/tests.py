"""Comprehensive test suite for Bakaiya Krishi Saathi Core application."""

import errno
import io
import json
import os
import socket
import subprocess
import sys
import tempfile
import unittest
from pathlib import Path
from unittest.mock import MagicMock, patch

from django.contrib.auth import get_user_model
from django.core.cache import cache
from django.core.exceptions import PermissionDenied, ValidationError
from django.core.files.uploadedfile import SimpleUploadedFile
from django.test import TestCase, override_settings
from django.urls import reverse
from PIL import Image

import config.settings as project_settings
from core.db_diagnostics import (
    classify_psycopg2_error,
    classify_socket_error,
    run_db_diagnostics,
    sanitize_error_text,
)
from core.models import (
    AdminProfile,
    ContentAuditLog,
    CropEntry,
    HelplineContactEntry,
    LivestockEntry,
    LivestockSectionContent,
    MediaAsset,
)
from core.services import process_uploaded_image, transition_entry_status

User = get_user_model()
ADMIN_WEB_DIR = Path(__file__).resolve().parents[1]


class CmsWorkflowAndSecurityTests(TestCase):
    def setUp(self):
        cache.clear()
        # Use new role system: "staff" (replaces content_editor)
        self.editor = User.objects.create_user(
            username="editor1", password="StrongEditorPass!2026"
        )
        AdminProfile.objects.create(
            user=self.editor, role="staff", full_name="Field Editor"
        )

        # Use new role system: "staff_manager" (replaces technical_reviewer)
        self.reviewer = User.objects.create_user(
            username="reviewer1", password="StrongReviewerPass!2026"
        )
        AdminProfile.objects.create(
            user=self.reviewer, role="staff_manager", full_name="Agronomist Reviewer"
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
        res = self.client.get(reverse("core:dashboard"))
        self.assertEqual(res.status_code, 200)
        body = res.content.decode("utf-8")
        self.assertIn("Editorial Overview", body)
        self.assertIn("Bakaiya Krishi Saathi Admin", body)
        self.assertIn("core/css/app.css", body)
        self.assertIn("core/js/app.js", body)

        farmers_res = self.client.get(reverse("core:farmers_list"))
        self.assertEqual(farmers_res.status_code, 200)
        self.assertIn("Registered Farmers", farmers_res.content.decode("utf-8"))

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

        crops_json = self.client.get(reverse("core:public_crops_api")).json()
        self.assertEqual(crops_json["count"], 1)
        self.assertEqual(crops_json["results"][0]["id"], "crop-pub")

        livestock_json = self.client.get(reverse("core:public_livestock_api")).json()
        self.assertEqual(livestock_json["count"], 1)
        self.assertEqual(livestock_json["results"][0]["species_code"], "goat")

        helpline_json = self.client.get(reverse("core:public_helpline_api")).json()
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

    def test_render_host_header_and_csrf_origin_are_allowed(self):
        from django.conf import settings

        self.assertIn(".onrender.com", settings.ALLOWED_HOSTS)
        self.assertIn("https://*.onrender.com", settings.CSRF_TRUSTED_ORIGINS)

        res_root = self.client.get("/", HTTP_HOST="bakaiya-krishi-saathi-cms.onrender.com")
        self.assertEqual(res_root.status_code, 302)
        self.assertIn("/cms/", res_root.url)

        res_login = self.client.get(
            "/cms/login/", HTTP_HOST="bakaiya-krishi-saathi-cms.onrender.com"
        )
        self.assertEqual(res_login.status_code, 200)


class CoreAllPagesEndToEndTests(TestCase):
    def setUp(self):
        cache.clear()
        # Use new role system: "staff" (replaces content_editor)
        self.editor = User.objects.create_user(
            username="qa_editor", password="StrongEditorPass!2026"
        )
        AdminProfile.objects.create(
            user=self.editor, role="staff", full_name="QA Editor"
        )

        # Use new role system: "staff_manager" (replaces technical_reviewer)
        self.reviewer = User.objects.create_user(
            username="qa_reviewer", password="StrongReviewerPass!2026"
        )
        AdminProfile.objects.create(
            user=self.reviewer, role="staff_manager", full_name="QA Reviewer"
        )

        self.super_admin = User.objects.create_user(
            username="qa_admin",
            password="StrongAdminPass!2026",
            is_superuser=True,
            is_staff=True,
        )
        AdminProfile.objects.create(
            user=self.super_admin, role="super_admin", full_name="QA Super Admin"
        )

    def test_login_logout_and_rate_limiting(self):
        res = self.client.get("/cms/login/")
        self.assertEqual(res.status_code, 200)
        self.assertContains(res, "Bakaiya Krishi Saathi Admin")

        bad = self.client.post(
            "/cms/login/",
            {"username": "qa_editor", "password": "wrong-password"},
        )
        self.assertEqual(bad.status_code, 200)
        self.assertContains(bad, "Invalid credentials")

        ok = self.client.post(
            "/cms/login/",
            {"username": "qa_editor", "password": "StrongEditorPass!2026"},
        )
        self.assertRedirects(ok, "/cms/")

        out = self.client.post("/cms/logout/")
        self.assertRedirects(out, "/cms/login/")

        cache.clear()
        for _ in range(8):
            self.client.post(
                "/cms/login/",
                {"username": "qa_editor", "password": "wrong-password"},
            )
        throttled = self.client.post(
            "/cms/login/",
            {"username": "qa_editor", "password": "wrong-password"},
        )
        self.assertEqual(throttled.status_code, 429)

    def test_crop_create_edit_preserves_metadata_and_allows_rereview(self):
        self.client.force_login(self.editor)
        self.assertEqual(self.client.get("/cms/crops/create/").status_code, 200)

        bad = self.client.post(
            "/cms/crops/create/",
            {"name_en": "   ", "name_ne": "", "description_ne": ""},
        )
        self.assertEqual(bad.status_code, 400)
        self.assertEqual(CropEntry.objects.count(), 0)

        res = self.client.post(
            "/cms/crops/create/",
            {
                "name_en": "Maize",
                "name_ne": "मकै",
                "scientific_name": "Zea mays",
                "category_id": "cat-cereal",
                "description_ne": "पहाडी तथा भित्री मधेशको प्रमुख अन्नबाली",
                "varieties": "Manakamana-3",
                "planting_season_ne": "चैत–वैशाख",
                "altitude_summary_ne": "३००–२००० मिटर",
                "soil_types_ne": "दोमट माटो",
                "source_reference": "NARC Maize Program",
                "thumbnail_url": "https://example.com/maize.jpg",
            },
        )
        self.assertRedirects(res, "/cms/crops/")
        crop = CropEntry.objects.get(name_en="Maize")

        self.client.post(f"/cms/crops/{crop.id}/transition/", {"target_status": "in_review"})
        self.client.force_login(self.reviewer)
        self.client.post(f"/cms/crops/{crop.id}/transition/", {"target_status": "approved"})
        self.client.post(f"/cms/crops/{crop.id}/transition/", {"target_status": "published"})

        self.client.force_login(self.editor)
        edit_page = self.client.get(f"/cms/crops/{crop.id}/edit/")
        self.assertEqual(edit_page.status_code, 200)
        self.assertContains(edit_page, "planting_season_ne")
        self.assertContains(edit_page, "thumbnail_url")

        self.client.post(
            f"/cms/crops/{crop.id}/edit/",
            {
                "name_en": "Maize Updated",
                "name_ne": "मकै",
                "description_ne": "अद्यावधिक विवरण",
                "source_reference": "NARC Maize Program",
            },
        )
        crop.refresh_from_db()
        self.assertEqual(crop.planting_season_ne, "चैत–वैशाख")
        self.assertEqual(crop.altitude_summary_ne, "३००–२००० मिटर")
        self.assertEqual(crop.soil_types_ne, "दोमट माटो")
        self.assertEqual(crop.thumbnail_url, "https://example.com/maize.jpg")
        self.assertEqual(crop.review_status, "needs_rereview")
        self.assertFalse(crop.is_published)
        self.assertTrue(ContentAuditLog.objects.filter(action="edit_crop", target_id=crop.id).exists())

        list_page = self.client.get("/cms/crops/?status=needs_rereview")
        self.assertEqual(list_page.status_code, 200)
        self.assertContains(list_page, "Submit for Review")

        self.client.post(f"/cms/crops/{crop.id}/transition/", {"target_status": "in_review"})
        crop.refresh_from_db()
        self.assertEqual(crop.review_status, "in_review")

    def test_livestock_list_detail_sections_and_publish_sync(self):
        animal = LivestockEntry.objects.create(
            id="animal-buffalo",
            species_code="buffalo",
            name_ne="भैंसी",
            name_en="Buffalo",
            summary_ne="दुधालु भैंसी पालन",
            source_reference="DLS Nepal",
            review_status="draft",
            created_by=self.editor,
        )
        self.client.force_login(self.editor)

        list_res = self.client.get("/cms/livestock/")
        self.assertEqual(list_res.status_code, 200)
        self.assertContains(list_res, "Submit for Review")

        detail_res = self.client.get(f"/cms/livestock/{animal.id}/")
        self.assertEqual(detail_res.status_code, 200)
        self.assertContains(detail_res, "1. Species Overview")

        bad_sec = self.client.post(
            f"/cms/livestock/{animal.id}/",
            {"section_number": "not-an-int"},
        )
        self.assertRedirects(bad_sec, f"/cms/livestock/{animal.id}/")

        self.client.post(
            f"/cms/livestock/{animal.id}/",
            {
                "section_number": "1",
                "body_ne": "बकैया गाउँपालिकामा भैंसी पालन उपयुक्त छ।",
                "body_en": "Buffalo farming overview.",
                "source_reference": "DLS Nepal",
                "is_applicable": "on",
            },
        )
        sec = LivestockSectionContent.objects.get(animal=animal, section_number=1)
        self.assertEqual(sec.review_status, "in_review")

        self.client.post(f"/cms/livestock/{animal.id}/transition/", {"target_status": "in_review"})
        self.client.force_login(self.reviewer)
        self.client.post(f"/cms/livestock/{animal.id}/transition/", {"target_status": "approved"})
        self.client.post(f"/cms/livestock/{animal.id}/transition/", {"target_status": "published"})

        sec.refresh_from_db()
        self.assertTrue(sec.is_published)
        self.assertEqual(sec.review_status, "published")

        api_res = self.client.get("/api/v1/public/livestock/").json()
        self.assertEqual(api_res["count"], 1)
        self.assertEqual(len(api_res["results"][0]["sections"]), 1)

    def test_media_library_and_helpline_validation(self):
        with tempfile.TemporaryDirectory() as tmp_dir:
            with override_settings(MEDIA_ROOT=Path(tmp_dir)):
                self.client.force_login(self.editor)
                self.assertEqual(self.client.get("/cms/media/").status_code, 200)

                buf = io.BytesIO()
                Image.new("RGB", (400, 300), color=(40, 110, 50)).save(buf, format="PNG")
                img_file = SimpleUploadedFile("paddy.png", buf.getvalue(), content_type="image/png")

                upload = self.client.post(
                    "/cms/media/",
                    {
                        "title_en": "Paddy Field",
                        "caption_ne": "धान खेत",
                        "target_kind": "crop",
                        "creator_name": "Bakaiya Agro Desk",
                        "license_type": "CC BY 4.0",
                        "source_url": "https://example.com/paddy",
                        "image_file": img_file,
                    },
                )
                self.assertRedirects(upload, "/cms/media/")
                self.assertEqual(MediaAsset.objects.count(), 1)

        self.client.force_login(self.editor)
        helpline_editor = self.client.get("/cms/helpline/")
        self.assertEqual(helpline_editor.status_code, 200)
        self.assertNotContains(helpline_editor, "Add Verified Contact")

        self.client.force_login(self.super_admin)
        self.client.post("/cms/helpline/", {"name_ne": "", "mobile_number": ""})
        self.assertEqual(HelplineContactEntry.objects.count(), 0)

        self.client.post(
            "/cms/helpline/",
            {
                "name_ne": "कृषि शाखा",
                "designation_ne": "कृषि अधिकृत",
                "department_en": "Bakaiya Agriculture Unit",
                "ward_ne": "वडा १",
                "mobile_number": "9800000001",
                "email": "agro@bakaiya.gov.np",
                "source_reference": "bakaiyamun.gov.np",
            },
        )
        self.assertEqual(HelplineContactEntry.objects.count(), 1)

    def test_healthz_django_admin_and_farmers_error_safety(self):
        ok_health = self.client.get("/healthz")
        self.assertEqual(ok_health.status_code, 200)
        self.assertEqual(ok_health.json(), {"status": "ok"})

        self.client.force_login(self.super_admin)
        self.assertEqual(self.client.get("/django-admin/").status_code, 200)

        self.client.force_login(self.editor)
        with patch("core.models.FarmerProfile.objects.all", side_effect=RuntimeError("postgresql://u:SecretPass@host/db")):
            res = self.client.get("/cms/farmers/")
        self.assertEqual(res.status_code, 200)
        self.assertNotContains(res, "SecretPass")
        self.assertContains(res, "Farmer directory is currently unavailable")

    def test_legacy_routes_require_auth_and_reject_get_mutations(self):
        for path in ("/dashboard/", "/crops/", "/guides/", "/accounts/farmers/"):
            self.assertEqual(self.client.get(path).status_code, 302)

        login_page = self.client.get("/accounts/login/")
        self.assertEqual(login_page.status_code, 200)
        self.assertContains(login_page, "core/css/app.css")

        self.client.force_login(self.super_admin)
        self.assertEqual(self.client.get("/crops/abc/delete/").status_code, 405)
        self.assertEqual(self.client.get("/crops/abc/toggle-publish/").status_code, 405)
        self.assertEqual(self.client.get("/guides/abc/toggle-publish/").status_code, 405)


class DatabaseEnvironmentConfigTests(unittest.TestCase):
    def test_discrete_session_pooler_config(self):
        env = {
            "DB_ENGINE": "django.db.backends.postgresql",
            "DB_NAME": "postgres",
            "DB_USER": "postgres.projref123",
            "DB_PASSWORD": "super-secret-value",
            "DB_HOST": "example.pooler.supabase.com",
            "DB_PORT": "5432",
            "DB_SSLMODE": "require",
            "DB_CONN_MAX_AGE": "",
            "DB_CONNECT_TIMEOUT": "",
        }
        with (
            patch.dict(os.environ, env, clear=True),
            patch.object(project_settings, "IS_VERCEL", False),
        ):
            cfg = project_settings.build_database_config(running_tests=False)
        self.assertEqual(cfg["ENGINE"], "django.db.backends.postgresql")
        self.assertEqual(cfg["HOST"], "example.pooler.supabase.com")
        self.assertEqual(cfg["USER"], "postgres.projref123")
        self.assertEqual(cfg["PORT"], "5432")
        self.assertEqual(cfg["OPTIONS"]["sslmode"], "require")
        self.assertEqual(cfg["CONN_MAX_AGE"], 60)
        self.assertTrue(cfg["CONN_HEALTH_CHECKS"])
        self.assertNotIn("DISABLE_SERVER_SIDE_CURSORS", cfg)

    def test_vercel_transaction_pooler_config_and_security_settings(self):
        env = {
            "PATH": os.environ.get("PATH", ""),
            "VERCEL": "1",
            "VERCEL_ENV": "production",
            "VERCEL_URL": "krishisaathi-cms.vercel.app",
            "DJANGO_DEBUG": "true",
            "SECRET_KEY": "test-prod-secret-key-value-long-enough-for-checks",
            "DB_ENGINE": "django.db.backends.postgresql",
            "DB_NAME": "postgres",
            "DB_USER": "postgres.projref123",
            "DB_PASSWORD": "super-secret-value",
            "DB_HOST": "example.pooler.supabase.com",
            "DB_PORT": "6543",
            "DB_SSLMODE": "disable",
            "ALLOWED_HOSTS": "*,cms.example.com",
        }
        script = (
            "import json, config.settings as s; "
            "print(json.dumps({"
            "'apps': s.INSTALLED_APPS, "
            "'debug': s.DEBUG, "
            "'db': s.DATABASES['default'], "
            "'allowed': s.ALLOWED_HOSTS, "
            "'csrf': s.CSRF_TRUSTED_ORIGINS, "
            "'ssl_redirect': getattr(s, 'SECURE_SSL_REDIRECT', False)"
            "}))"
        )
        proc = subprocess.run(
            [sys.executable, "-c", script],
            cwd=str(ADMIN_WEB_DIR),
            env=env,
            capture_output=True,
            text=True,
            check=True,
        )
        data = json.loads(proc.stdout)
        cfg = data["db"]
        self.assertIn("core", data["apps"])
        self.assertFalse(data["debug"])
        self.assertTrue(data["ssl_redirect"])
        self.assertEqual(cfg["CONN_MAX_AGE"], 0)
        self.assertTrue(cfg["DISABLE_SERVER_SIDE_CURSORS"])
        self.assertEqual(cfg["OPTIONS"]["sslmode"], "require")
        self.assertNotIn("*", data["allowed"])
        self.assertIn("cms.example.com", data["allowed"])
        self.assertIn("krishisaathi-cms.vercel.app", data["allowed"])
        self.assertIn("https://krishisaathi-cms.vercel.app", data["csrf"])

    def test_database_url_fallback_with_encoded_password(self):
        env = {
            "DATABASE_URL": "postgresql://postgres.ref:p%40ss%23word@example.pooler.supabase.com:6543/postgres",
        }
        with (
            patch.dict(os.environ, env, clear=True),
            patch.object(project_settings, "IS_VERCEL", False),
        ):
            cfg = project_settings.build_database_config(running_tests=False)
        self.assertEqual(cfg["USER"], "postgres.ref")
        self.assertEqual(cfg["PASSWORD"], "p@ss#word")
        self.assertEqual(cfg["PORT"], "6543")
        self.assertEqual(cfg["CONN_MAX_AGE"], 0)
        self.assertTrue(cfg["DISABLE_SERVER_SIDE_CURSORS"])
        self.assertEqual(cfg["OPTIONS"]["sslmode"], "require")


class DatabaseDiagnosticsTests(unittest.TestCase):
    def test_sanitize_error_text_redacts_secrets(self):
        raw = "FATAL: password failed for postgresql://user:MySecret123@host:5432/postgres (MySecret123)"
        cleaned = sanitize_error_text(raw, password="MySecret123")
        self.assertNotIn("MySecret123", cleaned)
        self.assertIn("<REDACTED>", cleaned)

    def test_error_classification_distinguishes_ipv6_ipv4_dns_timeout_and_auth(self):
        self.assertEqual(
            classify_socket_error(OSError(errno.ENETUNREACH, "Network is unreachable"), socket.AF_INET6)[0],
            "ipv6_unreachable",
        )
        self.assertEqual(
            classify_socket_error(OSError(errno.ENETUNREACH, "Network is unreachable"), socket.AF_INET)[0],
            "ipv4_unreachable",
        )
        self.assertEqual(
            classify_socket_error(socket.gaierror(socket.EAI_NONAME, "Name or service not known"), socket.AF_UNSPEC)[0],
            "dns_failure",
        )
        self.assertEqual(
            classify_socket_error(TimeoutError("timed out"), socket.AF_INET)[0],
            "connection_timeout",
        )
        self.assertEqual(
            classify_socket_error(ConnectionRefusedError(errno.ECONNREFUSED, "Connection refused"), socket.AF_INET)[0],
            "project_unavailable",
        )
        self.assertEqual(
            classify_psycopg2_error(Exception('FATAL: password authentication failed for user "postgres"'))[0],
            "auth_failed",
        )
        self.assertEqual(
            classify_psycopg2_error(
                Exception('connection to server at "db.example.supabase.co" (2a05:d014:18fe::1), port 5432 failed: Network is unreachable')
            )[0],
            "ipv6_unreachable",
        )

    def test_run_db_diagnostics_ipv6_failure_is_not_auth_error(self):
        fake_db = {
            "default": {
                "ENGINE": "django.db.backends.postgresql",
                "NAME": "postgres",
                "USER": "postgres",
                "PASSWORD": "secret",
                "HOST": "db.aheylojvwimspfuivpii.supabase.co",
                "PORT": "5432",
                "OPTIONS": {"sslmode": "require"},
                "CONN_MAX_AGE": 60,
            }
        }
        fake_sock = MagicMock()
        fake_sock.connect.side_effect = OSError(errno.ENETUNREACH, "Network is unreachable")
        with (
            patch("core.db_diagnostics.settings.DATABASES", fake_db),
            patch(
                "core.db_diagnostics.socket.getaddrinfo",
                return_value=[(socket.AF_INET6, socket.SOCK_STREAM, 6, "", ("2406:da14::1", 5432, 0, 0))],
            ),
            patch("core.db_diagnostics.socket.socket", return_value=fake_sock),
        ):
            report = run_db_diagnostics(timeout=1)

        self.assertFalse(report.passed)
        self.assertEqual(report.steps[-1].category, "ipv6_unreachable")

    def test_run_db_diagnostics_all_six_steps_pass_when_reachable(self):
        fake_db = {
            "default": {
                "ENGINE": "django.db.backends.postgresql",
                "NAME": "postgres",
                "USER": "postgres.projref",
                "PASSWORD": "secret",
                "HOST": "example.pooler.supabase.com",
                "PORT": "5432",
                "OPTIONS": {"sslmode": "require"},
                "CONN_MAX_AGE": 60,
            }
        }
        fake_sock = MagicMock()
        fake_pg_conn = MagicMock()
        fake_cursor = MagicMock()
        fake_cursor.fetchone.return_value = (1, "postgres", "postgres.projref")
        fake_cursor.__enter__.return_value = fake_cursor
        fake_recorder = MagicMock()
        fake_recorder.has_table.return_value = True
        fake_loader = MagicMock()
        fake_loader.applied_migrations = {("core", "0001_initial")}
        fake_loader.graph.nodes = {("core", "0001_initial"): None}

        with (
            patch("core.db_diagnostics.settings.DATABASES", fake_db),
            patch(
                "core.db_diagnostics.socket.getaddrinfo",
                return_value=[(socket.AF_INET, socket.SOCK_STREAM, 6, "", ("54.1.2.3", 5432))],
            ),
            patch("core.db_diagnostics.socket.socket", return_value=fake_sock),
            patch("core.db_diagnostics.psycopg2.connect", return_value=fake_pg_conn),
            patch("core.db_diagnostics.connection.cursor", return_value=fake_cursor),
            patch("core.db_diagnostics.MigrationRecorder", return_value=fake_recorder),
            patch("core.db_diagnostics.MigrationLoader", return_value=fake_loader),
        ):
            report = run_db_diagnostics(timeout=1)

        self.assertTrue(report.passed)
        self.assertEqual(len(report.steps), 6)
