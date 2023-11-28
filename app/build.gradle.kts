import java.util.Properties

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.parcelize")
    id("com.google.gms.google-services")
    id("com.google.firebase.crashlytics")
}
android {
    namespace = "dev.ebnbin.inviscam"
    compileSdk = 34
    defaultConfig {
        minSdk = 26
        targetSdk = 34
        val version = "0.1.2"
        versionCode = run {
            val split = version.split("-")
            val (major, minor, patch) = split[0].split(".").map { it.toInt() }
            val prerelease = split.getOrElse(1) { "100" }.toInt()
            major * 1000000 + minor * 10000 + patch * 100 + prerelease - 100
        }
        versionName = version
    }
    signingConfigs {
        register("release") {
            val localProperties = Properties().apply {
                load(rootProject.file("local.properties").reader())
            }
            keyAlias = localProperties.getProperty("keyAlias")
            keyPassword = localProperties.getProperty("keyPassword")
            storeFile = rootProject.file(localProperties.getProperty("storeFile"))
            storePassword = localProperties.getProperty("storePassword")
        }
    }
    buildTypes {
        debug {
            signingConfig = signingConfigs.getByName("release")
        }
        release {
            isMinifyEnabled = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"))
            signingConfig = signingConfigs.getByName("release")
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
        viewBinding = true
    }
    packaging {
        resources {
            excludes += "DebugProbesKt.bin" // https://github.com/Kotlin/kotlinx.coroutines
        }
    }
}
dependencies {
    implementation(project(":libcore"))
}
