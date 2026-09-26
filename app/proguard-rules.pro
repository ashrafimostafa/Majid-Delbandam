# Keep symbols readable in release. Do not rename classes or members.
-dontobfuscate

# Prefer keeping app + DataStore types if minify is ever re-enabled.
-keep class com.mostafa.majiddelbandam.** { *; }
-keepclassmembers class com.mostafa.majiddelbandam.** { *; }

# DataStore Preferences (progress / قران / helpers)
-keep class androidx.datastore.** { *; }
-keep class androidx.datastore.preferences.** { *; }
-keepclassmembers class * extends androidx.datastore.core.Serializer { *; }

# SQLite puzzle catalog + JSON path parsing
-keep class org.json.** { *; }

# Compose / ViewModel entry points
-keep class * extends androidx.lifecycle.ViewModel { *; }
-keep class * extends android.app.Application { *; }
-keep class * extends android.app.Activity { *; }
