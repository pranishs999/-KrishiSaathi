"""Domain constants, roles, and workflow definitions for Bakaiya Krishi Saathi."""

WORKFLOW_STATUSES = [
    ("draft", "Draft"),
    ("needs_source", "Needs Source"),
    ("in_review", "In Review"),
    ("approved", "Approved"),
    ("published", "Published"),
    ("needs_rereview", "Needs Re-review"),
    ("archived", "Archived"),
]

ALLOWED_TRANSITIONS = {
    "draft": {"needs_source", "in_review", "archived"},
    "needs_source": {"draft", "in_review", "archived"},
    "in_review": {"needs_source", "approved", "draft", "archived"},
    "approved": {"published", "needs_rereview", "archived"},
    "published": {"needs_rereview", "archived"},
    "needs_rereview": {"in_review", "archived"},
    "archived": {"draft"},
}

# New 3-level hierarchy roles
ADMIN_ROLES = [
    ("super_admin", "Super Admin"),
    ("staff_manager", "Staff Manager"),
    ("staff", "Staff"),
]

# Permissions for the new 3-level hierarchy
ROLE_PERMISSIONS = {
    "super_admin": {
        # User management
        "create_staff_manager",
        "edit_staff_manager",
        "suspend_staff_manager",
        "reactivate_staff_manager",
        "archive_staff_manager",
        "view_all_staff_managers",
        "view_all_staff",
        "manage_all_users",
        # Content management - full access
        "create_draft",
        "edit_content",
        "submit_review",
        "approve_content",
        "publish_content",
        "archive_content",
        "delete_content",
        "manage_media",
        "manage_helpline",
        "view_all_content",
        # Agricultural content
        "manage_crops",
        "manage_livestock",
        "manage_fodder",
        "manage_guides",
        "manage_farmers",
        # System
        "view_audit_logs",
        "manage_settings",
        "view_system_stats",
    },
    "staff_manager": {
        # Team management
        "create_staff",
        "edit_staff",
        "suspend_staff",
        "reactivate_staff",
        "deactivate_staff",
        "view_own_staff",
        "view_own_team_activity",
        # Content management - team scope
        "create_draft",
        "edit_content",
        "submit_review",
        "approve_content",
        "publish_content",
        "delete_own_team_content",
        "manage_media",
        "view_team_content",
        # Agricultural content - team scope
        "manage_crops",
        "manage_livestock",
        "manage_fodder",
        "manage_guides",
        # Reports
        "view_team_stats",
    },
    "staff": {
        # Personal content
        "create_draft",
        "edit_content",  # Allow staff to edit content for list views
        "edit_own_content",
        "submit_review",
        "manage_media",
        "view_own_content",
        # Agricultural content - personal scope
        "manage_crops",
        "manage_own_crops",
        "manage_livestock",
        "manage_own_livestock",
        "manage_fodder",
        "manage_own_fodder",
        "manage_guides",
        "manage_own_guides",
    },
}

# Permission categories for UI grouping
PERMISSION_CATEGORIES = {
    "user_management": [
        "create_staff_manager",
        "edit_staff_manager",
        "suspend_staff_manager",
        "reactivate_staff_manager",
        "archive_staff_manager",
        "view_all_staff_managers",
        "view_all_staff",
        "manage_all_users",
        "create_staff",
        "edit_staff",
        "suspend_staff",
        "reactivate_staff",
        "deactivate_staff",
        "view_own_staff",
        "view_own_team_activity",
    ],
    "content_management": [
        "create_draft",
        "edit_content",
        "edit_own_content",
        "submit_review",
        "approve_content",
        "publish_content",
        "archive_content",
        "delete_content",
        "delete_own_team_content",
        "manage_media",
        "manage_helpline",
        "view_all_content",
        "view_team_content",
        "view_own_content",
    ],
    "agricultural_content": [
        "manage_crops",
        "manage_livestock",
        "manage_fodder",
        "manage_guides",
        "manage_farmers",
        "manage_own_crops",
        "manage_own_livestock",
        "manage_own_fodder",
        "manage_own_guides",
    ],
    "system": [
        "view_audit_logs",
        "manage_settings",
        "view_system_stats",
        "view_team_stats",
    ],
}

LIVESTOCK_SECTIONS = [
    (1, "overview", "1. Species Overview & Bakaiya Suitability"),
    (2, "climate_altitude", "2. Climate & Altitude Suitability"),
    (3, "breeds", "3. Recommended Breeds & Selection"),
    (4, "housing_design", "4. Shed / Housing Structural Design"),
    (5, "housing_sanitation", "5. Ventilation, Bedding & Sanitation"),
    (6, "feeding_fodder", "6. Green Fodder & Roughage"),
    (7, "feeding_concentrate", "7. Concentrate Feed, Minerals & Water"),
    (8, "feeding_caution", "8. Ration Quantity & Bloat Cautions"),
    (9, "breeding_selection", "9. Breeding Age & Estrus Identification"),
    (10, "breeding_ai", "10. Mating & Artificial Insemination Guidance"),
    (11, "pregnancy_care", "11. Pregnancy / Incubation Care"),
    (12, "birthing_care", "12. Birthing / Hatching Management"),
    (13, "newborn_care", "13. Newborn / Colostrum / Chick Brooding Care"),
    (14, "weaning_management", "14. Weaning & Growth Transition"),
    (15, "daily_hygiene", "15. Daily Hygiene & Manure Management"),
    (16, "biosecurity", "16. Farm Biosecurity & Quarantine"),
    (17, "vaccination_awareness", "17. Vaccination Awareness (Vet Consultation)"),
    (18, "parasite_control", "18. Deworming & Parasite Control"),
    (19, "warning_signs", "19. Early Disease Warning Signs"),
    (20, "major_diseases", "20. Major Species Diseases & Immediate Action"),
    (21, "vet_referral", "21. Veterinary Referral & Treatment Caution"),
    (22, "clean_production", "22. Clean Milk / Egg / Meat Production Practices"),
    (23, "post_harvest", "23. Post-Harvest Handling & Storage"),
    (24, "waste_compost", "24. Biogas & Organic Manure Utilization"),
    (25, "market_sources", "25. Local Cooperative Marketing & Verified Sources"),
]

CROP_SECTIONS = [
    (1, "overview", "1. Crop Overview & Bakaiya Relevance"),
    (2, "varieties", "2. Recommended NARC Varieties"),
    (3, "altitude_zones", "3. Altitude & Nepal Ecological Zones"),
    (4, "climate", "4. Temperature, Rainfall & Sunlight"),
    (5, "soil_ph", "5. Soil Type, Texture, pH & Drainage"),
    (6, "season_timing", "6. Planting & Harvesting Calendar"),
    (7, "land_prep", "7. Land Preparation"),
    (8, "seed_selection", "8. Seed Selection & Nursery Raising"),
    (9, "spacing", "9. Row & Plant Spacing"),
    (10, "fertilizer", "10. Organic & Balanced Nutrient Management"),
    (11, "irrigation", "11. Critical Irrigation Stages"),
    (12, "interculture", "12. Weeding, Earthing Up & Staking"),
    (13, "pest_ipm", "13. Integrated Pest Management"),
    (14, "disease_management", "14. Major Crop Diseases"),
    (15, "safety_pesticide", "15. Safe Agrochemical Waiting Period Caution"),
    (16, "maturity_indices", "16. Maturity Signs"),
    (17, "harvesting", "17. Harvesting Method"),
    (18, "drying_curing", "18. Threshing, Drying & Curing"),
    (19, "storage", "19. Safe Storage Practices"),
    (20, "intercropping", "20. Intercropping & Crop Rotation"),
    (21, "media_gallery", "21. Verified Visual References"),
    (22, "references", "22. Official Sources & Verification Notes"),
]
