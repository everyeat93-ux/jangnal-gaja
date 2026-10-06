# ==========================================
# 🚀 장날가자 (Jangnal Gaja) ProGuard / R8 Rules
# ==========================================

# 1. Line numbers for crash reporting
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile

# 2. General Reflection & Annotations
-keepattributes *Annotation*,Signature,InnerClasses,EnclosingMethod

# 3. Data Entities & Local DB (Room)
-keep class com.jangnal.gaja.data.local.entity.** { *; }
-keep class com.jangnal.gaja.data.local.dao.** { *; }
-keep class com.jangnal.gaja.data.local.AppDatabase { *; }
-keep class * extends androidx.room.RoomDatabase
-keep @androidx.room.Entity class * { *; }
-keep @androidx.room.Dao class * { *; }
-dontwarn androidx.room.paging.**

# 4. Data Models & JSON Serialization (Gson / Firestore)
-keep class com.jangnal.gaja.model.** { *; }
-keepclassmembers class com.jangnal.gaja.data.local.entity.** { *; }
-keepclassmembers class * {
    @com.google.firebase.firestore.PropertyName <fields>;
    @com.google.firebase.firestore.PropertyName <methods>;
    @com.google.firebase.firestore.Exclude <fields>;
    @com.google.firebase.firestore.Exclude <methods>;
    @com.google.gson.annotations.SerializedName <fields>;
}

# 5. Kakao Maps SDK
-keep class com.kakao.maps.** { *; }
-keep interface com.kakao.maps.** { *; }
-dontwarn com.kakao.maps.**

# 6. Google Play Services & Firebase
-keep class com.google.android.gms.** { *; }
-dontwarn com.google.android.gms.**
-keep class com.google.firebase.** { *; }
-dontwarn com.google.firebase.**

# 7. Coil Image Loader
-keep class coil.** { *; }
-dontwarn coil.**

# 8. Kotlin Coroutines
-dontwarn kotlinx.coroutines.**
-keepclassmembers class kotlinx.coroutines.** { *; }

# 9. Android Jetpack Compose & ViewModel
-keep class androidx.compose.** { *; }
-keep class androidx.lifecycle.** { *; }
-keep class com.jangnal.gaja.ui.viewmodel.** { *; }