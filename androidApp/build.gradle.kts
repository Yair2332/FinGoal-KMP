plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.kotlinAndroid)
}

dependencies {
    implementation(project(":sharedLogic"))
    implementation(project(":sharedUI"))

    implementation(libs.androidx.activity.compose)
    implementation(libs.core.ktx)

    implementation(libs.compose.uiToolingPreview)
    implementation(libs.compose.foundation)
    implementation(libs.compose.material3)

    debugImplementation(libs.compose.uiTooling)

    implementation("androidx.compose.material:material-icons-extended:1.6.8")
    implementation("io.insert-koin:koin-androidx-compose:3.5.3")
    implementation("io.coil-kt:coil-compose:2.6.0")

    coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:2.0.4")
}

android {

    namespace = "com.fingoal.app"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    defaultConfig {
        applicationId = "com.fingoal.app"

        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()

        versionCode = 1
        versionName = "1.0"
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
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
        isCoreLibraryDesugaringEnabled = true

        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    buildFeatures {
        compose = true
    }

    kotlinOptions {
        jvmTarget = "11"
    }
}