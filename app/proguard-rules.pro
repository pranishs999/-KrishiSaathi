# ==============================================================================
# Bakaiya Krishi Saathi — R8 / ProGuard Configuration
# ==============================================================================

-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile

# Kotlinx Serialization (used by Supabase Postgrest and JSON codecs)
-keepattributes *Annotation*,InnerClasses
-dontnote kotlinx.serialization.AnnotationsKt
-keepclassmembers class kotlinx.serialization.json.** {
    *** Companion;
}
-keepclasseswithmembers class kotlinx.serialization.json.** {
    kotlinx.serialization.KSerializer serializer(...);
}
-keep,includedescriptorclasses class com.krishisaathi.app.**$$serializer { *; }
-keepclassmembers class com.krishisaathi.app.** {
    *** Companion;
}
-keepclasseswithmembers class com.krishisaathi.app.** {
    kotlinx.serialization.KSerializer serializer(...);
}

# Room Database
-keep class * extends androidx.room.RoomDatabase
-dontwarn androidx.room.paging.**

# Strip verbose/debug logs in release builds
-assumenosideeffects class android.util.Log {
    public static int v(...);
    public static int d(...);
    public static int i(...);
}
