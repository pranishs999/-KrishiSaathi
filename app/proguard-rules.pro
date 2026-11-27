# ==============================================================================
# Bakaiya Krishi Saathi — Play Store Release R8 / ProGuard Configuration
# ==============================================================================

-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile
-keepattributes *Annotation*,InnerClasses,Signature

# Kotlinx Serialization (Supabase Postgrest DTOs & JSON codecs)
-dontnote kotlinx.serialization.AnnotationsKt
-keepclassmembers class kotlinx.serialization.json.** {
    *** Companion;
}
-keepclasseswithmembers class kotlinx.serialization.json.** {
    kotlinx.serialization.KSerializer serializer(...);
}
-keep,includedescriptorclasses class com.krishisaathi.app.data.remote.**$$serializer { *; }
-keepclassmembers class com.krishisaathi.app.data.remote.** {
    *** Companion;
}
-keepclasseswithmembers class com.krishisaathi.app.data.remote.** {
    kotlinx.serialization.KSerializer serializer(...);
}

# Supabase & Ktor (ServiceLoader engines, Coroutines & Reflection)
-keep class * implements io.ktor.client.HttpClientEngineContainer { *; }
-keep class io.ktor.client.engine.android.** { *; }
-keep class io.github.jan.supabase.** { *; }
-dontwarn org.slf4j.**
-dontwarn io.ktor.**
-dontwarn kotlinx.atomicfu.**
-dontwarn java.lang.instrument.**
-dontwarn sun.misc.**

# Room Database & DAOs
-keep class * extends androidx.room.RoomDatabase { *; }
-keep class com.krishisaathi.app.data.local.** { *; }
-dontwarn androidx.room.paging.**

# WorkManager
-keep class com.krishisaathi.app.worker.SyncWorker { *; }
-keepclassmembers class * extends androidx.work.ListenableWorker {
    public <init>(android.content.Context, androidx.work.WorkerParameters);
}

# Strip verbose/debug logs in release builds
-assumenosideeffects class android.util.Log {
    public static int v(...);
    public static int d(...);
    public static int i(...);
}
