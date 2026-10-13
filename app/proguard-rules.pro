# Keep Room and networking models for release builds.
-keep class com.dwanz.multiprotocolbenchmark.data.local.** { *; }
-keepclassmembers class * {
    @com.google.gson.annotations.SerializedName <fields>;
}
