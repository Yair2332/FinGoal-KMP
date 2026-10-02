import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidMultiplatformLibrary)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
}

kotlin {

    androidLibrary {
        namespace = "com.fingoal.app.sharedUI"

        compileSdk = libs.versions.android.compileSdk.get().toInt()
        minSdk = libs.versions.android.minSdk.get().toInt()

        compilerOptions {
            jvmTarget = JvmTarget.JVM_11
        }

        androidResources {
            enable = true
        }
    }

    // ---------------------------------------------------------
    // iOS
    // ---------------------------------------------------------

    listOf(
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "SharedUI"
            isStatic = true
        }
    }

    // ---------------------------------------------------------
    // Source Sets
    // ---------------------------------------------------------

    sourceSets {

        commonMain.dependencies {

            // Shared Logic
            api(project(":sharedLogic"))

            // Compose
            implementation(libs.compose.runtime)
            implementation(libs.compose.foundation)
            implementation(libs.compose.material3)
            implementation(libs.compose.ui)
            implementation(libs.compose.components.resources)
            implementation(libs.compose.uiToolingPreview)

            // AndroidX Lifecycle
            implementation(libs.androidx.lifecycle.viewmodelCompose)
            implementation(libs.androidx.lifecycle.runtimeCompose)
        }

        androidMain.dependencies {

            implementation(libs.compose.uiToolingPreview)
            implementation(libs.compose.uiTooling)
        }

        commonTest.dependencies {

            implementation(libs.kotlin.test)
        }
    }
}

// -------------------------------------------------------------
// Android Runtime
// -------------------------------------------------------------

dependencies {
    androidRuntimeClasspath(
        libs.compose.uiTooling
    )
}