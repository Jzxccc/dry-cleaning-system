# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.kts.

# Keep data classes
-keep class com.chaser.drycleaningsystem.data.entity.** { *; }

# Keep Room entities
-keep @androidx.room.Entity class com.chaser.drycleaningsystem.data.entity.**
-keepclassmembers class com.chaser.drycleaningsystem.data.entity.** {
    @androidx.room.* <fields>;
}

# Keep DAO interfaces
-keep class com.chaser.drycleaningsystem.data.dao.** { *; }

# Keep Repository classes
-keep class com.chaser.drycleaningsystem.data.repository.** { *; }

# Keep ViewModel classes
-keep class com.chaser.drycleaningsystem.ui.**ViewModel { *; }

# Keep Compose composables
-keep class com.chaser.drycleaningsystem.ui.** {
    @androidx.compose.runtime.Composable <methods>;
}

# Keep Kotlin coroutines
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}

# Keep Mockito
-dontwarn org.mockito.**
-dontwarn org.mockito.kotlin.**
-keep class org.mockito.** { *; }
-keep class org.mockito.kotlin.** { *; }
