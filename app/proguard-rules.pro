# ==========================================
# 🚀 장날가자 (Jangnal Gaja) ProGuard / R8 Rules
# ==========================================

# 1. Line numbers for crash reporting & Play Console deobfuscation
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile

# 2. General Reflection, Annotations & Native JNI
-keepattributes *Annotation*,Signature,InnerClasses,EnclosingMethod
-keepclasseswithmembernames class * {
    native <methods>;
}

# 3. Data Entities & Local DB (Room)
-keep class com.jangnal.gaja.data.local.entity.** { *; }
-keep class * extends androidx.room.RoomDatabase { *; }
-keep @androidx.room.Entity class * { *; }
-keep @androidx.room.Dao class * { *; }
-dontwarn androidx.room.**
-dontwarn androidx.room.paging.**

# 4. Data Models & JSON Serialization (Firestore)
-keepclassmembers class * {
    @com.google.firebase.firestore.PropertyName <fields>;
    @com.google.firebase.firestore.PropertyName <methods>;
    @com.google.firebase.firestore.Exclude <fields>;
    @com.google.firebase.firestore.Exclude <methods>;
}

# 5. Kakao Maps Open SDK v2 (★ 핵심: C++ JNI 및 vectormap 렌더러 보존)
-keep class com.kakao.vectormap.** { *; }
-keep interface com.kakao.vectormap.** { *; }
-keep class com.kakao.maps.** { *; }
-keep interface com.kakao.maps.** { *; }
-dontwarn com.kakao.vectormap.**
-dontwarn com.kakao.maps.**

# 6. Suppress harmless warnings from third-party libraries
-dontwarn com.google.android.gms.**
-dontwarn com.google.android.play.core.**
-dontwarn com.google.firebase.**
-dontwarn coil.**
-dontwarn kotlinx.coroutines.**
