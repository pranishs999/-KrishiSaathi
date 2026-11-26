"""Image validation, EXIF stripping, and derivative generation pipeline using Pillow."""

import io
from pathlib import Path
from django.core.exceptions import ValidationError
from PIL import Image, ImageOps

ALLOWED_MIME_TYPES = {"image/jpeg", "image/png", "image/webp"}
MAX_IMAGE_BYTES = 5 * 1024 * 1024  # 5 MB hard cap

MAGIC_HEADERS = {
    b"\xff\xd8\xff": "image/jpeg",
    b"\x89PNG\r\n\x1a\n": "image/png",
}


def detect_image_mime(raw_bytes: bytes) -> str:
    """Inspect magic header bytes instead of trusting user filename or HTTP Content-Type."""
    for sig, mime in MAGIC_HEADERS.items():
        if raw_bytes.startswith(sig):
            return mime
    if raw_bytes[:4] == b"RIFF" and raw_bytes[8:12] == b"WEBP":
        return "image/webp"
    raise ValidationError("Unsupported or unsafe file signature. Only JPEG, PNG, and WebP images are accepted.")


def process_uploaded_image(uploaded_file, storage_root: Path, asset_id: str) -> dict:
    """
    Validates raw image upload, strips EXIF/metadata, and generates:
    - original/clean WebP (`{asset_id}_orig.webp`)
    - medium 800px WebP (`{asset_id}_med.webp`)
    - thumb 320px WebP (`{asset_id}_thumb.webp`)
    """
    if uploaded_file.size > MAX_IMAGE_BYTES:
        raise ValidationError("Image exceeds maximum 5 MB upload size.")

    raw_bytes = uploaded_file.read()
    mime_type = detect_image_mime(raw_bytes)

    try:
        with Image.open(io.BytesIO(raw_bytes)) as img:
            img.verify()
        with Image.open(io.BytesIO(raw_bytes)) as img:
            normalized = ImageOps.exif_transpose(img).convert("RGB")
            orig_w, orig_h = normalized.size

            storage_root.mkdir(parents=True, exist_ok=True)
            orig_file = storage_root / f"{asset_id}_orig.webp"
            med_file = storage_root / f"{asset_id}_med.webp"
            thumb_file = storage_root / f"{asset_id}_thumb.webp"

            normalized.save(orig_file, format="WEBP", quality=88, method=6)

            med_copy = normalized.copy()
            med_copy.thumbnail((800, 800))
            med_copy.save(med_file, format="WEBP", quality=82, method=6)

            thumb_copy = normalized.copy()
            thumb_copy.thumbnail((320, 320))
            thumb_copy.save(thumb_file, format="WEBP", quality=78, method=6)

            return {
                "mime_type": mime_type,
                "width_px": orig_w,
                "height_px": orig_h,
                "file_size_bytes": len(raw_bytes),
                "original_path": str(orig_file.name),
                "medium_path": str(med_file.name),
                "thumb_path": str(thumb_file.name),
            }
    except ValidationError:
        raise
    except Exception as exc:
        raise ValidationError(f"Corrupted or invalid image payload: {exc}") from exc
