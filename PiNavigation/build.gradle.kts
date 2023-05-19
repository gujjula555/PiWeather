/*
 * **
 * Pi App Studio. All rights reserved.Copyright (c) 2022.
 *
 */

plugins {
    id ("com.android.library")
    id ("org.jetbrains.kotlin.android")
    id ("kotlin-kapt")
    id ("dagger.hilt.android.plugin")
}

android {
    namespace = "com.piappstudio.pinavigation"
    compileSdk = 33

    defaultConfig {
        minSdk = 24
        targetSdk = 33

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles ("consumer-rules.pro")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
}

dependencies {

    implementation (libs.core.ktx)

    implementation (libs.bundles.hilt)
    kapt(libs.hilt.android.compiler)
    // Hilt integration
    implementation(project(":PiModel"))
    implementation (libs.coroutines.android)

    testImplementation(libs.junit)
    androidTestImplementation(libs.test.ext)
    androidTestImplementation(libs.espresso.core)
}