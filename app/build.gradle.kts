plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.jetbrainsKotlinAndroid)
    id("kotlin-kapt")

}

android {
    namespace = "cz.cvut.fel.tasktest"
    compileSdk = 34

    defaultConfig {
        applicationId = "cz.cvut.fel.tasktest"
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

val room_version = "2.5.1" // Define the version here
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
    implementation("androidx.core:core-ktx:1.7.0")
    implementation("androidx.compose.ui:ui:1.2.1")
    implementation("androidx.compose.material3:material3:1.0.0-alpha01") // Adjust for the latest version
    implementation("androidx.compose.ui:ui-tooling-preview:1.2.1")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.4.0")
    implementation("androidx.activity:activity-compose:1.8.2")
    implementation("androidx.navigation:navigation-compose:2.4.2")
    implementation("com.github.skydoves:colorpicker-compose:1.0.0")
    implementation("androidx.compose.material:material:x.y.z")

        //Dependency for Room(database)
    implementation("androidx.room:room-runtime:$room_version")
    kapt("androidx.room:room-compiler:$room_version")
    implementation("androidx.room:room-ktx:$room_version")

    // rememberImagePainte
    implementation(libs.accompanist.coil)
    implementation(libs.coil.compose)
    implementation("io.coil-kt:coil-compose:2.1.0")
}