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

        compileSdk = libs.versions.android.compileSdk
            .get()
            .toInt()

        minSdk = libs.versions.android.minSdk
            .get()
            .toInt()

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

        androidMain.dependencies {

            implementation(libs.compose.uiToolingPreview)
            implementation(libs.compose.uiTooling)
        }


        commonMain.dependencies {
            api(project(":sharedLogic"))
            implementation(libs.compose.runtime)
            implementation(libs.compose.foundation)
            implementation(libs.compose.material3)
            implementation(libs.compose.ui)
            implementation(libs.compose.components.resources)
            implementation(libs.compose.uiToolingPreview)
            implementation(libs.androidx.lifecycle.viewmodelCompose)
            implementation(libs.androidx.lifecycle.runtimeCompose)
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