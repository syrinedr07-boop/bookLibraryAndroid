# Gson / Retrofit
-keepattributes *Annotation*, Signature, InnerClasses, EnclosingMethod
-keepclassmembers class * {
    @com.google.gson.annotations.SerializedName <fields>;
}
-dontwarn retrofit2.**
-keep class retrofit2.** { *; }
-keep class com.example.librarybook.data.book.remote.dto.** { *; }

# Koin
-keep class org.koin.** { *; }

# Firebase
-keep class com.google.firebase.** { *; }
-dontwarn com.google.firebase.**

# Coil
-keep class coil.** { *; }
-dontwarn coil.**
