# ==========================================
# 🚀 장날가자 (Jangnal Gaja) ProGuard / R8 Rules
# ==========================================

# 1. Line numbers for crash reporting
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile

# 2. General Reflection, Annotations & Native JNI
-keepattributes *Annotation*,Signature,InnerClasses,EnclosingMethod
-keepclasseswithmembernames class * {
    native <methods>;
}

# 3. Application Components & Services
-keep class com.jangnal.gaja.MarketApplication { *; }
-keep class com.jangnal.gaja.MainActivity { *; }
-keep class com.jangnal.gaja.notification.** { *; }
-keep class com.jangnal.gaja.util.** { *; }

# 4. Data Entities & Local DB (Room)
-keep class com.jangnal.gaja.data.local.** { *; }
-keep class * extends androidx.room.RoomDatabase
-keep @androidx.room.Entity class * { *; }
-keep @androidx.room.Dao class * { *; }
-keep class androidx.room.** { *; }
-dontwarn androidx.room.**
-dontwarn androidx.room.paging.**

# 5. Data Models & JSON Serialization (Gson / Firestore)
-keep class com.jangnal.gaja.model.** { *; }
-keepclassmembers class com.jangnal.gaja.data.local.entity.** { *; }
-keepclassmembers class * {
    @com.google.firebase.firestore.PropertyName <fields>;
    @com.google.firebase.firestore.PropertyName <methods>;
    @com.google.firebase.firestore.Exclude <fields>;
    @com.google.firebase.firestore.Exclude <methods>;
    @com.google.gson.annotations.SerializedName <fields>;
}

# 6. Kakao Maps Open SDK v2 (★ 핵심: vectormap 및 JNI 보존)
-keep class com.kakao.vectormap.** { *; }
-keep interface com.kakao.vectormap.** { *; }
-keep class com.kakao.maps.** { *; }
-keep interface com.kakao.maps.** { *; }
-dontwarn com.kakao.vectormap.**
-dontwarn com.kakao.maps.**

# 7. Google Mobile Ads (AdMob)
-keep public class com.google.android.gms.ads.** {
   public *;
}
-keep public class com.google.ads.** {
   public *;
}
-keep class com.google.android.gms.ads.AdView { *; }
-keep class com.google.android.gms.ads.AdSize { *; }
-dontwarn com.google.android.gms.ads.**

# 8. Google Play Services & Play In-App Updates
-keep class com.google.android.gms.** { *; }
-dontwarn com.google.android.gms.**
-keep class com.google.android.play.core.** { *; }
-dontwarn com.google.android.play.core.**

# 9. Firebase
-keep class com.google.firebase.** { *; }
-dontwarn com.google.firebase.**

# 10. Coil Image Loader
-keep class coil.** { *; }
-dontwarn coil.**

# 11. Kotlin Coroutines
-dontwarn kotlinx.coroutines.**
-keepclassmembers class kotlinx.coroutines.** { *; }

# 12. Android Jetpack Compose & ViewModel & Repositories
-keep class androidx.compose.** { *; }
-keep class androidx.lifecycle.** { *; }
-keep class com.jangnal.gaja.ui.viewmodel.** { *; }
-keep class com.jangnal.gaja.data.repository.** { *; }