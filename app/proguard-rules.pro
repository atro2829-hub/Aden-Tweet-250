-keepattributes *Annotation*
-keep class com.adentweets.app.domain.model.** { *; }
-keep class com.adentweets.app.data.local.entity.** { *; }
-keepclassmembers class * {
    @com.google.firebase.database.PropertyName <methods>;
}
-dontwarn com.google.firebase.**
-keep class com.google.firebase.** { *; }
-keepclassmembers class com.google.firebase.** { *; }