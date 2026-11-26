package com.krishisaathi.app.data.local;

import androidx.annotation.NonNull;
import androidx.room.DatabaseConfiguration;
import androidx.room.InvalidationTracker;
import androidx.room.RoomDatabase;
import androidx.room.RoomOpenHelper;
import androidx.room.migration.AutoMigrationSpec;
import androidx.room.migration.Migration;
import androidx.room.util.DBUtil;
import androidx.room.util.TableInfo;
import androidx.sqlite.db.SupportSQLiteDatabase;
import androidx.sqlite.db.SupportSQLiteOpenHelper;
import java.lang.Class;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.processing.Generated;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class KrishiSaathiDatabase_Impl extends KrishiSaathiDatabase {
  private volatile BakaiyaCropDao _bakaiyaCropDao;

  @Override
  @NonNull
  protected SupportSQLiteOpenHelper createOpenHelper(@NonNull final DatabaseConfiguration config) {
    final SupportSQLiteOpenHelper.Callback _openCallback = new RoomOpenHelper(config, new RoomOpenHelper.Delegate(7) {
      @Override
      public void createAllTables(@NonNull final SupportSQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS `crop_categories` (`id` TEXT NOT NULL, `name_ne` TEXT NOT NULL, `name_en` TEXT NOT NULL, `icon_name` TEXT, PRIMARY KEY(`id`))");
        db.execSQL("CREATE TABLE IF NOT EXISTS `crops` (`id` TEXT NOT NULL, `category_id` TEXT NOT NULL, `name_ne` TEXT NOT NULL, `name_en` TEXT NOT NULL, `scientific_name` TEXT NOT NULL, `description_ne` TEXT NOT NULL, `varieties` TEXT, `climate_overview` TEXT, `land_preparation` TEXT, `seed_selection` TEXT, `spacing` TEXT, `fertilizer_management` TEXT, `crop_care` TEXT, `pest_disease_control` TEXT, `harvesting` TEXT, `storage` TEXT, `source_reference` TEXT NOT NULL, `is_verified_content` INTEGER NOT NULL, `thumbnail_url` TEXT, `is_published` INTEGER NOT NULL, `altitude_min_m` INTEGER, `altitude_opt_min_m` INTEGER, `altitude_opt_max_m` INTEGER, `altitude_max_m` INTEGER, `suitable_altitude` TEXT, `altitude_growth_notes_ne` TEXT, `suitable_zones_csv` TEXT, `suitable_regions_ne` TEXT, `soil_types` TEXT, `soil_texture_ne` TEXT, `soil_ph_min` REAL, `soil_ph_max` REAL, `soil_drainage_ne` TEXT, `soil_fertility_ne` TEXT, `soil_organic_matter_ne` TEXT, `temp_min` REAL, `temp_opt_min` REAL, `temp_opt_max` REAL, `temp_max` REAL, `humidity_min` REAL, `humidity_max` REAL, `rainfall_min_mm` INTEGER, `rainfall_max_mm` INTEGER, `rainfall_period_label_ne` TEXT, `water_requirement` TEXT, `sunlight` TEXT, `planting_season` TEXT, `sowing_period_ne` TEXT, `transplanting_period_ne` TEXT, `growing_duration_ne` TEXT, `harvesting_period_ne` TEXT, `irrigation_management` TEXT, `special_considerations_ne` TEXT, `last_updated_date` TEXT, PRIMARY KEY(`id`))");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_crops_category_id` ON `crops` (`category_id`)");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_crops_is_published` ON `crops` (`is_published`)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `crop_variety_requirements` (`id` TEXT NOT NULL, `crop_id` TEXT NOT NULL, `crop_name_ne` TEXT NOT NULL, `crop_name_en` TEXT NOT NULL, `variety_name_ne` TEXT NOT NULL, `variety_name_en` TEXT, `notes_ne` TEXT, `source_reference` TEXT NOT NULL, `altitude_min_m` INTEGER, `altitude_opt_min_m` INTEGER, `altitude_opt_max_m` INTEGER, `altitude_max_m` INTEGER, `suitable_altitude` TEXT, `altitude_growth_notes_ne` TEXT, `suitable_zones_csv` TEXT, `suitable_regions_ne` TEXT, `soil_types` TEXT, `soil_texture_ne` TEXT, `soil_ph_min` REAL, `soil_ph_max` REAL, `soil_drainage_ne` TEXT, `soil_fertility_ne` TEXT, `soil_organic_matter_ne` TEXT, `temp_min` REAL, `temp_opt_min` REAL, `temp_opt_max` REAL, `temp_max` REAL, `humidity_min` REAL, `humidity_max` REAL, `rainfall_min_mm` INTEGER, `rainfall_max_mm` INTEGER, `rainfall_period_label_ne` TEXT, `water_requirement` TEXT, `sunlight` TEXT, `planting_season` TEXT, `sowing_period_ne` TEXT, `transplanting_period_ne` TEXT, `growing_duration_ne` TEXT, `harvesting_period_ne` TEXT, `irrigation_management` TEXT, `special_considerations_ne` TEXT, `last_updated_date` TEXT, PRIMARY KEY(`id`))");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_crop_variety_requirements_crop_id` ON `crop_variety_requirements` (`crop_id`)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `crop_images` (`id` TEXT NOT NULL, `crop_id` TEXT NOT NULL, `image_url` TEXT NOT NULL, `caption_ne` TEXT NOT NULL, `stage_category` TEXT NOT NULL, `is_sample_image` INTEGER NOT NULL, PRIMARY KEY(`id`))");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_crop_images_crop_id` ON `crop_images` (`crop_id`)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `educational_videos` (`id` TEXT NOT NULL, `crop_id` TEXT NOT NULL, `topic_id` INTEGER NOT NULL, `title_ne` TEXT NOT NULL, `description_ne` TEXT NOT NULL, `channel_name` TEXT NOT NULL, `thumbnail_url` TEXT NOT NULL, `video_url` TEXT NOT NULL, `duration` TEXT, `provider` TEXT NOT NULL, `download_permission` INTEGER NOT NULL, `downloadable_file_url` TEXT, `local_file_path` TEXT, `file_size_bytes` INTEGER, `is_downloaded` INTEGER NOT NULL, `is_sample_media` INTEGER NOT NULL, PRIMARY KEY(`id`))");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_educational_videos_crop_id` ON `educational_videos` (`crop_id`)");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_educational_videos_topic_id` ON `educational_videos` (`topic_id`)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `helpline_contacts` (`id` TEXT NOT NULL, `name_ne` TEXT NOT NULL, `designation_ne` TEXT NOT NULL, `ward_ne` TEXT NOT NULL, `ward_number` INTEGER NOT NULL, `mobile_number` TEXT NOT NULL, `email` TEXT, `is_verified` INTEGER NOT NULL, PRIMARY KEY(`id`))");
        db.execSQL("CREATE TABLE IF NOT EXISTS `farmer_registration` (`id` TEXT NOT NULL, `full_name` TEXT NOT NULL, `municipality` TEXT NOT NULL, `ward_number` INTEGER NOT NULL, `phone_number` TEXT NOT NULL, `registered_at` INTEGER NOT NULL, PRIMARY KEY(`id`))");
        db.execSQL("CREATE TABLE IF NOT EXISTS room_master_table (id INTEGER PRIMARY KEY,identity_hash TEXT)");
        db.execSQL("INSERT OR REPLACE INTO room_master_table (id,identity_hash) VALUES(42, 'f24bb36e2f046837693513cfaf2759c0')");
      }

      @Override
      public void dropAllTables(@NonNull final SupportSQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS `crop_categories`");
        db.execSQL("DROP TABLE IF EXISTS `crops`");
        db.execSQL("DROP TABLE IF EXISTS `crop_variety_requirements`");
        db.execSQL("DROP TABLE IF EXISTS `crop_images`");
        db.execSQL("DROP TABLE IF EXISTS `educational_videos`");
        db.execSQL("DROP TABLE IF EXISTS `helpline_contacts`");
        db.execSQL("DROP TABLE IF EXISTS `farmer_registration`");
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onDestructiveMigration(db);
          }
        }
      }

      @Override
      public void onCreate(@NonNull final SupportSQLiteDatabase db) {
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onCreate(db);
          }
        }
      }

      @Override
      public void onOpen(@NonNull final SupportSQLiteDatabase db) {
        mDatabase = db;
        internalInitInvalidationTracker(db);
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onOpen(db);
          }
        }
      }

      @Override
      public void onPreMigrate(@NonNull final SupportSQLiteDatabase db) {
        DBUtil.dropFtsSyncTriggers(db);
      }

      @Override
      public void onPostMigrate(@NonNull final SupportSQLiteDatabase db) {
      }

      @Override
      @NonNull
      public RoomOpenHelper.ValidationResult onValidateSchema(
          @NonNull final SupportSQLiteDatabase db) {
        final HashMap<String, TableInfo.Column> _columnsCropCategories = new HashMap<String, TableInfo.Column>(4);
        _columnsCropCategories.put("id", new TableInfo.Column("id", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCropCategories.put("name_ne", new TableInfo.Column("name_ne", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCropCategories.put("name_en", new TableInfo.Column("name_en", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCropCategories.put("icon_name", new TableInfo.Column("icon_name", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysCropCategories = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesCropCategories = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoCropCategories = new TableInfo("crop_categories", _columnsCropCategories, _foreignKeysCropCategories, _indicesCropCategories);
        final TableInfo _existingCropCategories = TableInfo.read(db, "crop_categories");
        if (!_infoCropCategories.equals(_existingCropCategories)) {
          return new RoomOpenHelper.ValidationResult(false, "crop_categories(com.krishisaathi.app.data.local.CategoryEntity).\n"
                  + " Expected:\n" + _infoCropCategories + "\n"
                  + " Found:\n" + _existingCropCategories);
        }
        final HashMap<String, TableInfo.Column> _columnsCrops = new HashMap<String, TableInfo.Column>(54);
        _columnsCrops.put("id", new TableInfo.Column("id", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCrops.put("category_id", new TableInfo.Column("category_id", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCrops.put("name_ne", new TableInfo.Column("name_ne", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCrops.put("name_en", new TableInfo.Column("name_en", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCrops.put("scientific_name", new TableInfo.Column("scientific_name", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCrops.put("description_ne", new TableInfo.Column("description_ne", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCrops.put("varieties", new TableInfo.Column("varieties", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCrops.put("climate_overview", new TableInfo.Column("climate_overview", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCrops.put("land_preparation", new TableInfo.Column("land_preparation", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCrops.put("seed_selection", new TableInfo.Column("seed_selection", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCrops.put("spacing", new TableInfo.Column("spacing", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCrops.put("fertilizer_management", new TableInfo.Column("fertilizer_management", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCrops.put("crop_care", new TableInfo.Column("crop_care", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCrops.put("pest_disease_control", new TableInfo.Column("pest_disease_control", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCrops.put("harvesting", new TableInfo.Column("harvesting", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCrops.put("storage", new TableInfo.Column("storage", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCrops.put("source_reference", new TableInfo.Column("source_reference", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCrops.put("is_verified_content", new TableInfo.Column("is_verified_content", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCrops.put("thumbnail_url", new TableInfo.Column("thumbnail_url", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCrops.put("is_published", new TableInfo.Column("is_published", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCrops.put("altitude_min_m", new TableInfo.Column("altitude_min_m", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCrops.put("altitude_opt_min_m", new TableInfo.Column("altitude_opt_min_m", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCrops.put("altitude_opt_max_m", new TableInfo.Column("altitude_opt_max_m", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCrops.put("altitude_max_m", new TableInfo.Column("altitude_max_m", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCrops.put("suitable_altitude", new TableInfo.Column("suitable_altitude", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCrops.put("altitude_growth_notes_ne", new TableInfo.Column("altitude_growth_notes_ne", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCrops.put("suitable_zones_csv", new TableInfo.Column("suitable_zones_csv", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCrops.put("suitable_regions_ne", new TableInfo.Column("suitable_regions_ne", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCrops.put("soil_types", new TableInfo.Column("soil_types", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCrops.put("soil_texture_ne", new TableInfo.Column("soil_texture_ne", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCrops.put("soil_ph_min", new TableInfo.Column("soil_ph_min", "REAL", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCrops.put("soil_ph_max", new TableInfo.Column("soil_ph_max", "REAL", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCrops.put("soil_drainage_ne", new TableInfo.Column("soil_drainage_ne", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCrops.put("soil_fertility_ne", new TableInfo.Column("soil_fertility_ne", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCrops.put("soil_organic_matter_ne", new TableInfo.Column("soil_organic_matter_ne", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCrops.put("temp_min", new TableInfo.Column("temp_min", "REAL", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCrops.put("temp_opt_min", new TableInfo.Column("temp_opt_min", "REAL", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCrops.put("temp_opt_max", new TableInfo.Column("temp_opt_max", "REAL", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCrops.put("temp_max", new TableInfo.Column("temp_max", "REAL", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCrops.put("humidity_min", new TableInfo.Column("humidity_min", "REAL", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCrops.put("humidity_max", new TableInfo.Column("humidity_max", "REAL", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCrops.put("rainfall_min_mm", new TableInfo.Column("rainfall_min_mm", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCrops.put("rainfall_max_mm", new TableInfo.Column("rainfall_max_mm", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCrops.put("rainfall_period_label_ne", new TableInfo.Column("rainfall_period_label_ne", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCrops.put("water_requirement", new TableInfo.Column("water_requirement", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCrops.put("sunlight", new TableInfo.Column("sunlight", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCrops.put("planting_season", new TableInfo.Column("planting_season", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCrops.put("sowing_period_ne", new TableInfo.Column("sowing_period_ne", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCrops.put("transplanting_period_ne", new TableInfo.Column("transplanting_period_ne", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCrops.put("growing_duration_ne", new TableInfo.Column("growing_duration_ne", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCrops.put("harvesting_period_ne", new TableInfo.Column("harvesting_period_ne", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCrops.put("irrigation_management", new TableInfo.Column("irrigation_management", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCrops.put("special_considerations_ne", new TableInfo.Column("special_considerations_ne", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCrops.put("last_updated_date", new TableInfo.Column("last_updated_date", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysCrops = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesCrops = new HashSet<TableInfo.Index>(2);
        _indicesCrops.add(new TableInfo.Index("index_crops_category_id", false, Arrays.asList("category_id"), Arrays.asList("ASC")));
        _indicesCrops.add(new TableInfo.Index("index_crops_is_published", false, Arrays.asList("is_published"), Arrays.asList("ASC")));
        final TableInfo _infoCrops = new TableInfo("crops", _columnsCrops, _foreignKeysCrops, _indicesCrops);
        final TableInfo _existingCrops = TableInfo.read(db, "crops");
        if (!_infoCrops.equals(_existingCrops)) {
          return new RoomOpenHelper.ValidationResult(false, "crops(com.krishisaathi.app.data.local.BakaiyaCropEntity).\n"
                  + " Expected:\n" + _infoCrops + "\n"
                  + " Found:\n" + _existingCrops);
        }
        final HashMap<String, TableInfo.Column> _columnsCropVarietyRequirements = new HashMap<String, TableInfo.Column>(42);
        _columnsCropVarietyRequirements.put("id", new TableInfo.Column("id", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCropVarietyRequirements.put("crop_id", new TableInfo.Column("crop_id", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCropVarietyRequirements.put("crop_name_ne", new TableInfo.Column("crop_name_ne", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCropVarietyRequirements.put("crop_name_en", new TableInfo.Column("crop_name_en", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCropVarietyRequirements.put("variety_name_ne", new TableInfo.Column("variety_name_ne", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCropVarietyRequirements.put("variety_name_en", new TableInfo.Column("variety_name_en", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCropVarietyRequirements.put("notes_ne", new TableInfo.Column("notes_ne", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCropVarietyRequirements.put("source_reference", new TableInfo.Column("source_reference", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCropVarietyRequirements.put("altitude_min_m", new TableInfo.Column("altitude_min_m", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCropVarietyRequirements.put("altitude_opt_min_m", new TableInfo.Column("altitude_opt_min_m", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCropVarietyRequirements.put("altitude_opt_max_m", new TableInfo.Column("altitude_opt_max_m", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCropVarietyRequirements.put("altitude_max_m", new TableInfo.Column("altitude_max_m", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCropVarietyRequirements.put("suitable_altitude", new TableInfo.Column("suitable_altitude", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCropVarietyRequirements.put("altitude_growth_notes_ne", new TableInfo.Column("altitude_growth_notes_ne", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCropVarietyRequirements.put("suitable_zones_csv", new TableInfo.Column("suitable_zones_csv", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCropVarietyRequirements.put("suitable_regions_ne", new TableInfo.Column("suitable_regions_ne", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCropVarietyRequirements.put("soil_types", new TableInfo.Column("soil_types", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCropVarietyRequirements.put("soil_texture_ne", new TableInfo.Column("soil_texture_ne", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCropVarietyRequirements.put("soil_ph_min", new TableInfo.Column("soil_ph_min", "REAL", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCropVarietyRequirements.put("soil_ph_max", new TableInfo.Column("soil_ph_max", "REAL", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCropVarietyRequirements.put("soil_drainage_ne", new TableInfo.Column("soil_drainage_ne", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCropVarietyRequirements.put("soil_fertility_ne", new TableInfo.Column("soil_fertility_ne", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCropVarietyRequirements.put("soil_organic_matter_ne", new TableInfo.Column("soil_organic_matter_ne", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCropVarietyRequirements.put("temp_min", new TableInfo.Column("temp_min", "REAL", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCropVarietyRequirements.put("temp_opt_min", new TableInfo.Column("temp_opt_min", "REAL", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCropVarietyRequirements.put("temp_opt_max", new TableInfo.Column("temp_opt_max", "REAL", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCropVarietyRequirements.put("temp_max", new TableInfo.Column("temp_max", "REAL", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCropVarietyRequirements.put("humidity_min", new TableInfo.Column("humidity_min", "REAL", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCropVarietyRequirements.put("humidity_max", new TableInfo.Column("humidity_max", "REAL", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCropVarietyRequirements.put("rainfall_min_mm", new TableInfo.Column("rainfall_min_mm", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCropVarietyRequirements.put("rainfall_max_mm", new TableInfo.Column("rainfall_max_mm", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCropVarietyRequirements.put("rainfall_period_label_ne", new TableInfo.Column("rainfall_period_label_ne", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCropVarietyRequirements.put("water_requirement", new TableInfo.Column("water_requirement", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCropVarietyRequirements.put("sunlight", new TableInfo.Column("sunlight", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCropVarietyRequirements.put("planting_season", new TableInfo.Column("planting_season", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCropVarietyRequirements.put("sowing_period_ne", new TableInfo.Column("sowing_period_ne", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCropVarietyRequirements.put("transplanting_period_ne", new TableInfo.Column("transplanting_period_ne", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCropVarietyRequirements.put("growing_duration_ne", new TableInfo.Column("growing_duration_ne", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCropVarietyRequirements.put("harvesting_period_ne", new TableInfo.Column("harvesting_period_ne", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCropVarietyRequirements.put("irrigation_management", new TableInfo.Column("irrigation_management", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCropVarietyRequirements.put("special_considerations_ne", new TableInfo.Column("special_considerations_ne", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCropVarietyRequirements.put("last_updated_date", new TableInfo.Column("last_updated_date", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysCropVarietyRequirements = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesCropVarietyRequirements = new HashSet<TableInfo.Index>(1);
        _indicesCropVarietyRequirements.add(new TableInfo.Index("index_crop_variety_requirements_crop_id", false, Arrays.asList("crop_id"), Arrays.asList("ASC")));
        final TableInfo _infoCropVarietyRequirements = new TableInfo("crop_variety_requirements", _columnsCropVarietyRequirements, _foreignKeysCropVarietyRequirements, _indicesCropVarietyRequirements);
        final TableInfo _existingCropVarietyRequirements = TableInfo.read(db, "crop_variety_requirements");
        if (!_infoCropVarietyRequirements.equals(_existingCropVarietyRequirements)) {
          return new RoomOpenHelper.ValidationResult(false, "crop_variety_requirements(com.krishisaathi.app.data.local.CropVarietyRequirementEntity).\n"
                  + " Expected:\n" + _infoCropVarietyRequirements + "\n"
                  + " Found:\n" + _existingCropVarietyRequirements);
        }
        final HashMap<String, TableInfo.Column> _columnsCropImages = new HashMap<String, TableInfo.Column>(6);
        _columnsCropImages.put("id", new TableInfo.Column("id", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCropImages.put("crop_id", new TableInfo.Column("crop_id", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCropImages.put("image_url", new TableInfo.Column("image_url", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCropImages.put("caption_ne", new TableInfo.Column("caption_ne", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCropImages.put("stage_category", new TableInfo.Column("stage_category", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCropImages.put("is_sample_image", new TableInfo.Column("is_sample_image", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysCropImages = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesCropImages = new HashSet<TableInfo.Index>(1);
        _indicesCropImages.add(new TableInfo.Index("index_crop_images_crop_id", false, Arrays.asList("crop_id"), Arrays.asList("ASC")));
        final TableInfo _infoCropImages = new TableInfo("crop_images", _columnsCropImages, _foreignKeysCropImages, _indicesCropImages);
        final TableInfo _existingCropImages = TableInfo.read(db, "crop_images");
        if (!_infoCropImages.equals(_existingCropImages)) {
          return new RoomOpenHelper.ValidationResult(false, "crop_images(com.krishisaathi.app.data.local.CropImageEntity).\n"
                  + " Expected:\n" + _infoCropImages + "\n"
                  + " Found:\n" + _existingCropImages);
        }
        final HashMap<String, TableInfo.Column> _columnsEducationalVideos = new HashMap<String, TableInfo.Column>(16);
        _columnsEducationalVideos.put("id", new TableInfo.Column("id", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsEducationalVideos.put("crop_id", new TableInfo.Column("crop_id", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsEducationalVideos.put("topic_id", new TableInfo.Column("topic_id", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsEducationalVideos.put("title_ne", new TableInfo.Column("title_ne", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsEducationalVideos.put("description_ne", new TableInfo.Column("description_ne", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsEducationalVideos.put("channel_name", new TableInfo.Column("channel_name", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsEducationalVideos.put("thumbnail_url", new TableInfo.Column("thumbnail_url", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsEducationalVideos.put("video_url", new TableInfo.Column("video_url", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsEducationalVideos.put("duration", new TableInfo.Column("duration", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsEducationalVideos.put("provider", new TableInfo.Column("provider", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsEducationalVideos.put("download_permission", new TableInfo.Column("download_permission", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsEducationalVideos.put("downloadable_file_url", new TableInfo.Column("downloadable_file_url", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsEducationalVideos.put("local_file_path", new TableInfo.Column("local_file_path", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsEducationalVideos.put("file_size_bytes", new TableInfo.Column("file_size_bytes", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsEducationalVideos.put("is_downloaded", new TableInfo.Column("is_downloaded", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsEducationalVideos.put("is_sample_media", new TableInfo.Column("is_sample_media", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysEducationalVideos = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesEducationalVideos = new HashSet<TableInfo.Index>(2);
        _indicesEducationalVideos.add(new TableInfo.Index("index_educational_videos_crop_id", false, Arrays.asList("crop_id"), Arrays.asList("ASC")));
        _indicesEducationalVideos.add(new TableInfo.Index("index_educational_videos_topic_id", false, Arrays.asList("topic_id"), Arrays.asList("ASC")));
        final TableInfo _infoEducationalVideos = new TableInfo("educational_videos", _columnsEducationalVideos, _foreignKeysEducationalVideos, _indicesEducationalVideos);
        final TableInfo _existingEducationalVideos = TableInfo.read(db, "educational_videos");
        if (!_infoEducationalVideos.equals(_existingEducationalVideos)) {
          return new RoomOpenHelper.ValidationResult(false, "educational_videos(com.krishisaathi.app.data.local.EducationalVideoEntity).\n"
                  + " Expected:\n" + _infoEducationalVideos + "\n"
                  + " Found:\n" + _existingEducationalVideos);
        }
        final HashMap<String, TableInfo.Column> _columnsHelplineContacts = new HashMap<String, TableInfo.Column>(8);
        _columnsHelplineContacts.put("id", new TableInfo.Column("id", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsHelplineContacts.put("name_ne", new TableInfo.Column("name_ne", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsHelplineContacts.put("designation_ne", new TableInfo.Column("designation_ne", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsHelplineContacts.put("ward_ne", new TableInfo.Column("ward_ne", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsHelplineContacts.put("ward_number", new TableInfo.Column("ward_number", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsHelplineContacts.put("mobile_number", new TableInfo.Column("mobile_number", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsHelplineContacts.put("email", new TableInfo.Column("email", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsHelplineContacts.put("is_verified", new TableInfo.Column("is_verified", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysHelplineContacts = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesHelplineContacts = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoHelplineContacts = new TableInfo("helpline_contacts", _columnsHelplineContacts, _foreignKeysHelplineContacts, _indicesHelplineContacts);
        final TableInfo _existingHelplineContacts = TableInfo.read(db, "helpline_contacts");
        if (!_infoHelplineContacts.equals(_existingHelplineContacts)) {
          return new RoomOpenHelper.ValidationResult(false, "helpline_contacts(com.krishisaathi.app.data.local.HelplineContactEntity).\n"
                  + " Expected:\n" + _infoHelplineContacts + "\n"
                  + " Found:\n" + _existingHelplineContacts);
        }
        final HashMap<String, TableInfo.Column> _columnsFarmerRegistration = new HashMap<String, TableInfo.Column>(6);
        _columnsFarmerRegistration.put("id", new TableInfo.Column("id", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsFarmerRegistration.put("full_name", new TableInfo.Column("full_name", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsFarmerRegistration.put("municipality", new TableInfo.Column("municipality", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsFarmerRegistration.put("ward_number", new TableInfo.Column("ward_number", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsFarmerRegistration.put("phone_number", new TableInfo.Column("phone_number", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsFarmerRegistration.put("registered_at", new TableInfo.Column("registered_at", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysFarmerRegistration = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesFarmerRegistration = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoFarmerRegistration = new TableInfo("farmer_registration", _columnsFarmerRegistration, _foreignKeysFarmerRegistration, _indicesFarmerRegistration);
        final TableInfo _existingFarmerRegistration = TableInfo.read(db, "farmer_registration");
        if (!_infoFarmerRegistration.equals(_existingFarmerRegistration)) {
          return new RoomOpenHelper.ValidationResult(false, "farmer_registration(com.krishisaathi.app.data.local.FarmerRegistrationEntity).\n"
                  + " Expected:\n" + _infoFarmerRegistration + "\n"
                  + " Found:\n" + _existingFarmerRegistration);
        }
        return new RoomOpenHelper.ValidationResult(true, null);
      }
    }, "f24bb36e2f046837693513cfaf2759c0", "3c49d1609669e21c77930bdf2db9c51e");
    final SupportSQLiteOpenHelper.Configuration _sqliteConfig = SupportSQLiteOpenHelper.Configuration.builder(config.context).name(config.name).callback(_openCallback).build();
    final SupportSQLiteOpenHelper _helper = config.sqliteOpenHelperFactory.create(_sqliteConfig);
    return _helper;
  }

  @Override
  @NonNull
  protected InvalidationTracker createInvalidationTracker() {
    final HashMap<String, String> _shadowTablesMap = new HashMap<String, String>(0);
    final HashMap<String, Set<String>> _viewTables = new HashMap<String, Set<String>>(0);
    return new InvalidationTracker(this, _shadowTablesMap, _viewTables, "crop_categories","crops","crop_variety_requirements","crop_images","educational_videos","helpline_contacts","farmer_registration");
  }

  @Override
  public void clearAllTables() {
    super.assertNotMainThread();
    final SupportSQLiteDatabase _db = super.getOpenHelper().getWritableDatabase();
    try {
      super.beginTransaction();
      _db.execSQL("DELETE FROM `crop_categories`");
      _db.execSQL("DELETE FROM `crops`");
      _db.execSQL("DELETE FROM `crop_variety_requirements`");
      _db.execSQL("DELETE FROM `crop_images`");
      _db.execSQL("DELETE FROM `educational_videos`");
      _db.execSQL("DELETE FROM `helpline_contacts`");
      _db.execSQL("DELETE FROM `farmer_registration`");
      super.setTransactionSuccessful();
    } finally {
      super.endTransaction();
      _db.query("PRAGMA wal_checkpoint(FULL)").close();
      if (!_db.inTransaction()) {
        _db.execSQL("VACUUM");
      }
    }
  }

  @Override
  @NonNull
  protected Map<Class<?>, List<Class<?>>> getRequiredTypeConverters() {
    final HashMap<Class<?>, List<Class<?>>> _typeConvertersMap = new HashMap<Class<?>, List<Class<?>>>();
    _typeConvertersMap.put(BakaiyaCropDao.class, BakaiyaCropDao_Impl.getRequiredConverters());
    return _typeConvertersMap;
  }

  @Override
  @NonNull
  public Set<Class<? extends AutoMigrationSpec>> getRequiredAutoMigrationSpecs() {
    final HashSet<Class<? extends AutoMigrationSpec>> _autoMigrationSpecsSet = new HashSet<Class<? extends AutoMigrationSpec>>();
    return _autoMigrationSpecsSet;
  }

  @Override
  @NonNull
  public List<Migration> getAutoMigrations(
      @NonNull final Map<Class<? extends AutoMigrationSpec>, AutoMigrationSpec> autoMigrationSpecs) {
    final List<Migration> _autoMigrations = new ArrayList<Migration>();
    return _autoMigrations;
  }

  @Override
  public BakaiyaCropDao bakaiyaCropDao() {
    if (_bakaiyaCropDao != null) {
      return _bakaiyaCropDao;
    } else {
      synchronized(this) {
        if(_bakaiyaCropDao == null) {
          _bakaiyaCropDao = new BakaiyaCropDao_Impl(this);
        }
        return _bakaiyaCropDao;
      }
    }
  }
}
