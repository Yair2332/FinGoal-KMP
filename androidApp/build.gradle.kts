plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.kotlinAndroid)
}

dependencies {

    implementation(project(":sharedLogic"))
    implementation(project(":sharedUI"))

    implementation(libs.androidx.activity.compose)

    implementation(libs.compose.uiToolingPreview)
    implementation(libs.core.ktx)

    debugImplementation(libs.compose.uiTooling)


}

android {

    namespace = "com.fingoal.app"

    compileSdk = libs.versions.android.compileSdk
        .get()
        .toInt()


    defaultConfig {

        applicationId = "com.fingoal.app"

        minSdk = libs.versions.android.minSdk
            .get()
            .toInt()

        targetSdk = libs.versions.android.targetSdk
            .get()
            .toInt()

        versionCode = 1
        versionName = "1.0"
    }


    // ---------------------------------------------------------
    // Packaging
    // ---------------------------------------------------------

    packaging {

        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }


    // ---------------------------------------------------------
    // Build Types
    // ---------------------------------------------------------

    buildTypes {

        release {

            isMinifyEnabled = false

            proguardFiles(
                getDefaultProguardFile(
                    "proguard-android-optimize.txt"
                ),
                "proguard-rules.pro"
            )
        }
    }


    // ---------------------------------------------------------
    // Java
    // ---------------------------------------------------------

    compileOptions {

        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }


    // ---------------------------------------------------------
    // Compose
    // ---------------------------------------------------------

    buildFeatures {
        compose = true
    }
    kotlinOptions {
        jvmTarget = "11"
    }
}