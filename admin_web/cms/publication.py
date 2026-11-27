"""Mirror published CMS rows into the farmer-facing Supabase tables.

cms_cropentry and cms_livestockentry stay the editorial records.
public.crops and public.animals are what Android reads. Unpublished edits
clear the farmer-facing publication flag and do not delete the shared row.
"""

import logging

from django.db import DatabaseError, connection

logger = logging.getLogger(__name__)


def mirror_published_entry(entry, target_type: str) -> None:
    try:
        if target_type == "crop":
            _mirror_crop(entry)
        elif target_type == "livestock":
            _mirror_animal(entry)
    except DatabaseError:
        logger.warning("Farmer catalog mirror skipped for %s %s", target_type, getattr(entry, "pk", ""))


def mirror_helpline(contact) -> None:
    published = bool(contact.is_published and contact.is_verified)
    ward_number = _ward_number(contact.ward_ne)
    try:
        with connection.cursor() as cursor:
            cursor.execute(
                """
                INSERT INTO public.helpline_contacts (
                    id, name_ne, designation_ne, ward_ne, ward_number,
                    mobile_number, email, is_verified, is_published
                )
                VALUES (%s, %s, %s, %s, %s, %s, %s, %s, %s)
                ON CONFLICT (id) DO UPDATE SET
                    name_ne = EXCLUDED.name_ne,
                    designation_ne = EXCLUDED.designation_ne,
                    ward_ne = EXCLUDED.ward_ne,
                    ward_number = EXCLUDED.ward_number,
                    mobile_number = EXCLUDED.mobile_number,
                    email = EXCLUDED.email,
                    is_verified = EXCLUDED.is_verified,
                    is_published = EXCLUDED.is_published
                """,
                [
                    contact.id,
                    contact.name_ne,
                    contact.designation_ne,
                    contact.ward_ne,
                    ward_number,
                    contact.mobile_number,
                    contact.email or None,
                    published,
                    published,
                ],
            )
    except DatabaseError:
        logger.warning("Helpline mirror skipped for %s", contact.pk)


def _mirror_crop(entry) -> None:
    published = bool(entry.is_published and entry.review_status == "published")
    with connection.cursor() as cursor:
        cursor.execute(
            """
            INSERT INTO public.crops (
                id, category_id, name_ne, name_en, scientific_name, description_ne,
                varieties, planting_season, suitable_altitude, soil_types,
                source_reference, thumbnail_url, is_published, is_verified_content, updated_at
            )
            VALUES (
                %s,
                (SELECT id FROM public.crop_categories WHERE id = %s),
                %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, TRUE, NOW()
            )
            ON CONFLICT (id) DO UPDATE SET
                category_id = EXCLUDED.category_id,
                name_ne = EXCLUDED.name_ne,
                name_en = EXCLUDED.name_en,
                scientific_name = EXCLUDED.scientific_name,
                description_ne = EXCLUDED.description_ne,
                varieties = EXCLUDED.varieties,
                planting_season = EXCLUDED.planting_season,
                suitable_altitude = EXCLUDED.suitable_altitude,
                soil_types = EXCLUDED.soil_types,
                source_reference = EXCLUDED.source_reference,
                thumbnail_url = EXCLUDED.thumbnail_url,
                is_published = EXCLUDED.is_published,
                updated_at = NOW()
            """,
            [
                entry.id,
                entry.category_id,
                entry.name_ne,
                entry.name_en,
                entry.scientific_name or entry.name_en,
                entry.description_ne,
                entry.varieties or None,
                entry.planting_season_ne or None,
                entry.altitude_summary_ne or None,
                entry.soil_types_ne or None,
                entry.source_reference or "",
                entry.thumbnail_url or None,
                published,
            ],
        )


def _mirror_animal(entry) -> None:
    published = bool(entry.is_published and entry.review_status == "published")
    with connection.cursor() as cursor:
        cursor.execute(
            """
            INSERT INTO public.animal_categories (id, name_ne, name_en, is_published)
            VALUES (%s, %s, %s, TRUE)
            ON CONFLICT (id) DO NOTHING
            """,
            [entry.category_id, entry.category_id, entry.category_id],
        )
        cursor.execute(
            """
            INSERT INTO public.animals (
                id, category_id, name_ne, name_en, scientific_name, species_code,
                summary_ne, purpose_tags_ne, bakaiya_relevance_ne, climate_suitability_ne,
                altitude_range_ne, recommended_breeds_summary_ne, safety_notice_ne,
                source_reference, thumbnail_url, review_status, is_published, is_verified_content,
                updated_at
            )
            VALUES (
                %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, NOW()
            )
            ON CONFLICT (id) DO UPDATE SET
                name_ne = EXCLUDED.name_ne,
                name_en = EXCLUDED.name_en,
                scientific_name = EXCLUDED.scientific_name,
                species_code = EXCLUDED.species_code,
                summary_ne = EXCLUDED.summary_ne,
                source_reference = EXCLUDED.source_reference,
                thumbnail_url = EXCLUDED.thumbnail_url,
                review_status = EXCLUDED.review_status,
                is_published = EXCLUDED.is_published,
                updated_at = NOW()
            """,
            [
                entry.id,
                entry.category_id,
                entry.name_ne,
                entry.name_en,
                entry.scientific_name or None,
                entry.species_code,
                entry.summary_ne,
                entry.purpose_tags_ne or None,
                entry.bakaiya_relevance_ne or None,
                entry.climate_suitability_ne or None,
                entry.altitude_range_ne or None,
                entry.recommended_breeds_summary_ne or None,
                entry.safety_notice_ne,
                entry.source_reference or "",
                entry.thumbnail_url or None,
                entry.review_status,
                published,
                published,
            ],
        )


def _ward_number(ward_text: str) -> int:
    digits = "".join(ch for ch in (ward_text or "") if ch.isdigit())
    if not digits:
        return 0
    return int(digits[:2])
