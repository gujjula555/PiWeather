
buildscript {
    repositories {
        google()
        mavenCentral()

        // Android Build Server
        maven { url = uri("../nowinandroid-prebuilts/m2repository") }
    }
    dependencies {

        classpath ("com.google.dagger:hilt-android-gradle-plugin:${libs.versions.hilt.version.get()}")
        //To pass parameters as part of deeplink
        classpath ("androidx.navigation:navigation-safe-args-gradle-plugin:${libs.versions.nav.version.get()}")
        classpath ("org.jetbrains.kotlin:kotlin-gradle-plugin:${libs.versions.kotlin.version.get()}")
    }
}

// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    id("com.android.application") version "8.2.0-alpha03" apply false
    id("org.jetbrains.kotlin.android") version "1.8.10" apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.kotlin.kapt) apply false
    alias(libs.plugins.hilt) apply false
}