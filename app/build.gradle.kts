plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    id("com.google.devtools.ksp")
    id("com.google.dagger.hilt.android")
    kotlin("plugin.serialization") version "2.0.21"
}

android {
    namespace = "com.example.geomodeapp"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.geomodeapp"
        minSdk = 28
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    // DEPENDENCIES USED FOR ROOM
    implementation(libs.androidx.room.runtime)
    ksp(libs.androidx.room.compiler)

    // DEPENDENCY FOR VIEWMODEL
    implementation(libs.androidx.lifecycle.viewmodel.ktx)

    // DEPENDENCY FOR COROUTINES
    implementation(libs.kotlinx.coroutines.android)

    // DEPENDENCY FOR NAVIGATION
    implementation(libs.androidx.navigation.compose)
    implementation(libs.kotlinx.serialization.json)

    // DEPENDENCY FOR HILT
    implementation(libs.hilt.android)
    implementation(libs.androidx.hilt.navigation.compose)
    ksp(libs.hilt.android.compiler)
    implementation(libs.androidx.hilt.work)
    ksp(libs.androidx.hilt.compiler)

    // DEPENDENCY FOR GOOGLE MAPS
    implementation(libs.maps.compose)
    implementation (libs.maps.compose.utils)
    implementation (libs.play.services.location)

    //DEPENDENCY FOR WORKMANAGER
    implementation (libs.androidx.work.runtime.ktx)
}