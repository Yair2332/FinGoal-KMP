import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidMultiplatformLibrary)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.ksp)
    alias(libs.plugins.room)
    alias(libs.plugins.kotlinSerialization)

}

kotlin {
    androidLibrary {
        namespace = "com.fingoal.app.sharedLogic"

        compileSdk = libs.versions.android.compileSdk.get().toInt()
        minSdk = libs.versions.android.minSdk.get().toInt()

        androidResources {
            enable = true
        }

        compilerOptions {
            jvmTarget = JvmTarget.JVM_11
        }
    }

    listOf(
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "SharedLogic"
            isStatic = true
        }
    }

    sourceSets {
        commonMain.dependencies {

            implementation(libs.compose.material.icons.extended)

            // Compose Resources
            implementation(libs.compose.components.resources)

            // Compose Multiplatform
            implementation(libs.compose.runtime)
            implementation(libs.compose.foundation)
            implementation(libs.compose.ui)
            implementation(compose.material3)

            // Navigation
            implementation(libs.androidx.navigation.compose)

            // Room
            implementation(libs.androidx.room.runtime)
            implementation(libs.sqlite.bundled)

            implementation(libs.koin.core)
            implementation(libs.koin.compose)
            // Ktor
            implementation(libs.ktor.client.core)
            implementation(libs.ktor.client.content.negotiation)
            implementation(libs.ktor.client.logging)
            implementation(libs.ktor.serialization.kotlinx.json)

            // Lifecycle
            implementation(libs.androidx.lifecycle.viewmodelCompose)

            // Kotlinx
            implementation(libs.kotlinx.datetime)
            implementation(libs.kotlinx.serialization.json)

            // DataStore
            implementation(libs.androidx.datastore.preferences)

            implementation(libs.coil.compose)
            implementation(libs.coil.network.ktor2)

        }

        androidMain.dependencies {
            implementation(libs.ktor.client.okhttp)
        }

        iosMain.dependencies {
            implementation(libs.ktor.client.darwin)
        }

        commonTest.dependencies {
            implementation(kotlin("test"))
            implementation(libs.kotlinx.coroutines.test)
        }
    }
}

// Room
room {
    schemaDirectory("$projectDir/schemas")
}

// KSP
dependencies {
    add("kspAndroid", libs.androidx.room.compiler)
    add("kspIosArm64", libs.androidx.room.compiler)
    add("kspIosSimulatorArm64", libs.androidx.room.compiler)
}

