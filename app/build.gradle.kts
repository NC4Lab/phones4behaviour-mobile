plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    id("org.jetbrains.kotlin.plugin.serialization") version "1.5.0"
}

android {
    namespace = "com.example.phones4behaviour"
    compileSdk = 34

    defaultConfig {
        buildConfigField(
            "String",
            "SERVER_IP",
            "\"${project.findProperty("SERVER_IP")}\""
        )
        buildConfigField(
            "String",
            "PORT",
            "\"${project.findProperty("PORT")}\""
        )
        buildConfigField(
            "String",
            "STREAM_API_KEY",
            "\"${project.findProperty("STREAM_API_KEY")}\""
        )
        buildConfigField(
            "String",
            "STREAM_VIEWER_TOKEN",
            "\"${project.findProperty("STREAM_VIEWER_TOKEN")}\""
        )
        buildConfigField(
            "String",
            "STREAM_LIVESTREAM_ID",
            "\"${project.findProperty("STREAM_LIVESTREAM_ID")}\""
        )
        applicationId = "com.example.phones4behaviour"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        compose = true
        buildConfig = true
        viewBinding = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
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
    implementation(libs.okhttp)
    implementation(libs.okhttp.logging.interceptor)
    implementation(libs.kotlinx.serialization.json)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
    implementation(libs.coil.compose)
    implementation(libs.gson)
    implementation(libs.socket.io.client)
//    implementation(libs.androidx.camera.core)
    implementation(libs.androidx.camera.video)
//    implementation(libs.androidx.camera.lifecycle)
//    implementation(libs.androidx.camera.view)
    implementation(libs.stream.video.android.ui.compose.v1014)
    implementation (libs.androidx.camera.core.v131)
    implementation (libs.androidx.camera.camera2)
    implementation (libs.androidx.camera.view.v131)
    implementation (libs.androidx.camera.lifecycle.v131)
}

