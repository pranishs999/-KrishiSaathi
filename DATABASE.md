# Krishi Saathi: Database & Storage Schema Specification

---

## 1. Schema Design Philosophy

Krishi Saathi maintains a **dual-schema architecture**:
1. **Remote Cloud Storage:** **PostgreSQL 15+** managed via **Supabase**, secured by Row Level Security (RLS) policies, with JSONB support and audit timestamps.
2. **Local Client Storage:** **Android Room (SQLite)**, acting as an offline-first replica and single source of truth for the Android application.

All primary keys use UUID strings (or stable semantic text IDs for pre-packaged seed data) to allow conflict-free offline generation and seamless synchronization.

---

## 2. PostgreSQL / Supabase Schema (DDL)

```sql
-- Enable UUID extension
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- ============================================================================
-- 1. PROFILES (Farmer & Admin User Accounts)
-- ============================================================================
CREATE TABLE public.profiles (
    id UUID PRIMARY KEY REFERENCES auth.users(id) ON DELETE CASCADE,
    phone TEXT NOT NULL UNIQUE,
    full_name TEXT NOT NULL,
    role TEXT NOT NULL DEFAULT 'farmer' CHECK (role IN ('farmer', 'admin')),
    district TEXT NOT NULL DEFAULT 'Makwanpur',
    municipality TEXT NOT NULL, -- e.g. 'Hetauda', 'Thaha', 'Manahari', 'Bakaiya'
    ward_number INT NOT NULL CHECK (ward_number > 0 AND ward_number <= 35),
    primary_crops TEXT[] NOT NULL DEFAULT '{}', -- array of crop IDs
    land_unit_preference TEXT NOT NULL DEFAULT 'ropani' CHECK (land_unit_preference IN ('ropani', 'bigha')),
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- ============================================================================
-- 2. CROPS (Master Crop Catalog)
-- ============================================================================
CREATE TABLE public.crops (
    id TEXT PRIMARY KEY, -- semantic slug e.g. 'crop_paddy', 'crop_maize'
    name_en TEXT NOT NULL,
    name_ne TEXT NOT NULL,
    scientific_name TEXT NOT NULL,
    category TEXT NOT NULL CHECK (category IN ('cereal', 'vegetable', 'cash_crop', 'spice', 'pulse')),
    growth_duration_days_min INT NOT NULL,
    growth_duration_days_max INT NOT NULL,
    optimal_temp_celsius TEXT NOT NULL, -- e.g. '20-30'
    rainfall_mm TEXT NOT NULL,           -- e.g. '1000-1500'
    soil_type TEXT NOT NULL,             -- e.g. 'Sandy loam to clay loam'
    thumbnail_asset_path TEXT NOT NULL,  -- local asset or CDN path
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- ============================================================================
-- 3. CROP_GUIDES (Agronomic Growth Stages)
-- ============================================================================
CREATE TABLE public.crop_guides (
    id TEXT PRIMARY KEY, -- e.g. 'guide_paddy_stage_1'
    crop_id TEXT NOT NULL REFERENCES public.crops(id) ON DELETE CASCADE,
    stage_order INT NOT NULL, -- 1, 2, 3...
    stage_name_en TEXT NOT NULL,
    stage_name_ne TEXT NOT NULL,
    duration_days_text TEXT NOT NULL, -- e.g. 'Days 1-15'
    description_en TEXT NOT NULL,
    description_ne TEXT NOT NULL,
    water_management TEXT NOT NULL,
    labor_activities TEXT NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    UNIQUE(crop_id, stage_order)
);

-- ============================================================================
-- 4. SEASONAL_RECOMMENDATIONS (Makwanpur Planting Calendar)
-- ============================================================================
CREATE TABLE public.seasonal_recommendations (
    id TEXT PRIMARY KEY,
    crop_id TEXT NOT NULL REFERENCES public.crops(id) ON DELETE CASCADE,
    region_type TEXT NOT NULL CHECK (region_type IN ('valley_lowland', 'mid_hills', 'all_makwanpur')),
    planting_month_start INT NOT NULL CHECK (planting_month_start BETWEEN 1 AND 12), -- Nepali or Gregorian month index
    planting_month_end INT NOT NULL CHECK (planting_month_end BETWEEN 1 AND 12),
    harvest_month_start INT NOT NULL CHECK (harvest_month_start BETWEEN 1 AND 12),
    harvest_month_end INT NOT NULL CHECK (harvest_month_end BETWEEN 1 AND 12),
    calendar_type TEXT NOT NULL DEFAULT 'bikram_sambat' CHECK (calendar_type IN ('bikram_sambat', 'gregorian')),
    advisory_notes_en TEXT,
    advisory_notes_ne TEXT,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- ============================================================================
-- 5. PEST_DISEASES (Diagnostic Guide)
-- ============================================================================
CREATE TABLE public.pest_diseases (
    id TEXT PRIMARY KEY, -- e.g. 'pest_fall_armyworm'
    crop_id TEXT NOT NULL REFERENCES public.crops(id) ON DELETE CASCADE,
    name_en TEXT NOT NULL,
    name_ne TEXT NOT NULL,
    scientific_name TEXT,
    category TEXT NOT NULL CHECK (category IN ('insect_pest', 'fungal_disease', 'bacterial_disease', 'viral_disease', 'weed')),
    symptoms_en TEXT NOT NULL,
    symptoms_ne TEXT NOT NULL,
    preventive_cultural_measures TEXT NOT NULL,
    biological_control_measures TEXT NOT NULL,
    image_asset_paths TEXT[] NOT NULL DEFAULT '{}',
    is_quarantine_alert BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- ============================================================================
-- 6. FERTILIZER_GUIDES (Nutrient Advisory per Crop)
-- ============================================================================
CREATE TABLE public.fertilizer_guides (
    id TEXT PRIMARY KEY, -- e.g. 'fert_paddy_improved'
    crop_id TEXT NOT NULL REFERENCES public.crops(id) ON DELETE CASCADE,
    variety_type TEXT NOT NULL DEFAULT 'improved', -- 'local', 'improved', 'hybrid'
    recommendation_source TEXT NOT NULL DEFAULT 'NARC/MoALD',
    -- Standard dosages per 1 Ropani (~508.72 sq meters)
    urea_kg_per_ropani REAL NOT NULL,
    dap_kg_per_ropani REAL NOT NULL,
    mop_kg_per_ropani REAL NOT NULL,
    compost_kg_per_ropani REAL NOT NULL,
    -- Standard dosages per 1 Bigha (~6772.63 sq meters)
    urea_kg_per_bigha REAL NOT NULL,
    dap_kg_per_bigha REAL NOT NULL,
    mop_kg_per_bigha REAL NOT NULL,
    compost_kg_per_bigha REAL NOT NULL,
    basal_application_guide TEXT NOT NULL,
    top_dress_split_guide TEXT NOT NULL,
    micronutrient_notes TEXT,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- ============================================================================
-- 7. PESTICIDE_GUIDES (Verified Chemical Interventions)
-- ============================================================================
CREATE TABLE public.pesticide_guides (
    id TEXT PRIMARY KEY, -- e.g. 'pestchem_emamectin_benzoate'
    pest_disease_id TEXT NOT NULL REFERENCES public.pest_diseases(id) ON DELETE CASCADE,
    trade_names TEXT[] NOT NULL, -- e.g. ['Proclaim', 'EM-1']
    active_ingredient TEXT NOT NULL, -- e.g. 'Emamectin Benzoate 5% SG'
    formulation TEXT NOT NULL, -- 'EC', 'WP', 'SG', 'SC'
    dosage_per_liter_water TEXT NOT NULL, -- e.g. '0.5 g/L'
    dosage_per_16l_knapsack TEXT NOT NULL, -- e.g. '8 g per 16L tank'
    phi_days INT NOT NULL, -- Pre-Harvest Interval (Waiting period in days)
    toxicity_class TEXT NOT NULL CHECK (toxicity_class IN ('red', 'yellow', 'blue', 'green')),
    safety_gear_checklist TEXT[] NOT NULL DEFAULT '{gloves, mask}',
    registration_number TEXT NOT NULL, -- PQPMC Nepal Reg No
    verified_by_expert BOOLEAN NOT NULL DEFAULT FALSE,
    expert_reviewer_notes TEXT,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- ============================================================================
-- INDEXES FOR PERFORMANCE
-- ============================================================================
CREATE INDEX idx_crop_guides_crop_order ON public.crop_guides(crop_id, stage_order);
CREATE INDEX idx_seasonal_crop_region ON public.seasonal_recommendations(crop_id, region_type);
CREATE INDEX idx_pest_crop ON public.pest_diseases(crop_id);
CREATE INDEX idx_pesticide_pest ON public.pesticide_guides(pest_disease_id);
CREATE INDEX idx_fertilizer_crop ON public.fertilizer_guides(crop_id);
```

---

## 3. Android Room Entities (Kotlin)

### 3.1 `CropEntity.kt`
```kotlin
package com.krishisaathi.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "crops")
data class CropEntity(
    @PrimaryKey val id: String,
    val nameEn: String,
    val nameNe: String,
    val scientificName: String,
    val category: String,
    val growthDurationDaysMin: Int,
    val growthDurationDaysMax: Int,
    val optimalTempCelsius: String,
    val rainfallMm: String,
    val soilType: String,
    val thumbnailAssetPath: String,
    val isActive: Boolean = true,
    val updatedAt: Long = System.currentTimeMillis()
)
```

### 3.2 `CropGuideEntity.kt`
```kotlin
package com.krishisaathi.app.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "crop_guides",
    foreignKeys = [
        ForeignKey(
            entity = CropEntity::class,
            parentColumns = ["id"],
            childColumns = ["cropId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["cropId", "stageOrder"])]
)
data class CropGuideEntity(
    @PrimaryKey val id: String,
    val cropId: String,
    val stageOrder: Int,
    val stageNameEn: String,
    val stageNameNe: String,
    val durationDaysText: String,
    val descriptionEn: String,
    val descriptionNe: String,
    val waterManagement: String,
    val laborActivities: String,
    val updatedAt: Long = System.currentTimeMillis()
)
```

### 3.3 `PestDiseaseEntity.kt`
```kotlin
package com.krishisaathi.app.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "pest_diseases",
    foreignKeys = [
        ForeignKey(
            entity = CropEntity::class,
            parentColumns = ["id"],
            childColumns = ["cropId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("cropId")]
)
data class PestDiseaseEntity(
    @PrimaryKey val id: String,
    val cropId: String,
    val nameEn: String,
    val nameNe: String,
    val scientificName: String?,
    val category: String,
    val symptomsEn: String,
    val symptomsNe: String,
    val preventiveCulturalMeasures: String,
    val biologicalControlMeasures: String,
    val imageAssetPaths: List<String>,
    val isQuarantineAlert: Boolean = false,
    val updatedAt: Long = System.currentTimeMillis()
)
```

### 3.4 `PesticideGuideEntity.kt`
```kotlin
package com.krishisaathi.app.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "pesticide_guides",
    foreignKeys = [
        ForeignKey(
            entity = PestDiseaseEntity::class,
            parentColumns = ["id"],
            childColumns = ["pestDiseaseId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("pestDiseaseId")]
)
data class PesticideGuideEntity(
    @PrimaryKey val id: String,
    val pestDiseaseId: String,
    val tradeNames: List<String>,
    val activeIngredient: String,
    val formulation: String,
    val dosagePerLiterWater: String,
    val dosagePer16lKnapsack: String,
    val phiDays: Int,
    val toxicityClass: String, // "red", "yellow", "blue", "green"
    val safetyGearChecklist: List<String>,
    val registrationNumber: String,
    val verifiedByExpert: Boolean,
    val expertReviewerNotes: String?,
    val updatedAt: Long = System.currentTimeMillis()
)
```

### 3.5 `FertilizerGuideEntity.kt`
```kotlin
package com.krishisaathi.app.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "fertilizer_guides",
    foreignKeys = [
        ForeignKey(
            entity = CropEntity::class,
            parentColumns = ["id"],
            childColumns = ["cropId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("cropId")]
)
data class FertilizerGuideEntity(
    @PrimaryKey val id: String,
    val cropId: String,
    val varietyType: String,
    val recommendationSource: String,
    val ureaKgPerRopani: Float,
    val dapKgPerRopani: Float,
    val mopKgPerRopani: Float,
    val compostKgPerRopani: Float,
    val ureaKgPerBigha: Float,
    val dapKgPerBigha: Float,
    val mopKgPerBigha: Float,
    val compostKgPerBigha: Float,
    val basalApplicationGuide: String,
    val topDressSplitGuide: String,
    val micronutrientNotes: String?,
    val updatedAt: Long = System.currentTimeMillis()
)
```

### 3.6 `ProfileEntity.kt`
```kotlin
package com.krishisaathi.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "profiles")
data class ProfileEntity(
    @PrimaryKey val id: String,
    val phone: String,
    val fullName: String,
    val role: String,
    val district: String = "Makwanpur",
    val municipality: String,
    val wardNumber: Int,
    val primaryCrops: List<String>,
    val landUnitPreference: String = "ropani",
    val updatedAt: Long = System.currentTimeMillis()
)
```

---

## 4. Room TypeConverters

Room requires TypeConverters to map Kotlin `List<String>` to SQLite-compatible representations:

```kotlin
package com.krishisaathi.app.core.database.converter

import androidx.room.TypeConverter
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class StringListConverter {
    @TypeConverter
    fun fromStringList(value: List<String>): String = Json.encodeToString(value)

    @TypeConverter
    fun toStringList(value: String): List<String> = Json.decodeFromString(value)
}
```

---

## 5. Seed Dataset Plan for Makwanpur

The APK ships with an embedded SQLite asset (`database/krishi_saathi_seed.db`) populated with:
1. **7 Key Crops:**
   - Paddy (धान - Radha-4, Chaite-5)
   - Maize (मकै - Rampur Composite, Deuti)
   - Cauliflower (काउली - Snow Mystique, Kathmandu Local)
   - Cabbage (बन्दा - Green Coronet)
   - Off-Season Tomato (गोलभेडा - Srijana, Samjhana)
   - Mustard (तोरी - Bikash)
   - Ginger (अदुवा - Bose)
2. **Key Pests & Pathogens:**
   - Fall Armyworm (*Spodoptera frugiperda*) on Maize
   - Late Blight (*Phytophthora infestans*) on Tomato/Potato
   - Rice Stem Borer (*Scirpophaga incertulas*) on Paddy
   - Diamondback Moth (*Plutella xylostella*) on Cauliflower/Cabbage
   - Clubroot (*Plasmodiophora brassicae*) in Mid-Hills vegetables
3. **Verified MoALD/NARC Fertilizer Baselines** for both Ropani and Bigha systems.

