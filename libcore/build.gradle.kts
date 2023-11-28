plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.parcelize")
}
android {
    namespace = "dev.ebnbin.android.core"
    compileSdk = 34
    defaultConfig {
        minSdk = 26
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
}
dependencies {
    api("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3") // https://github.com/Kotlin/kotlinx.coroutines
    api("androidx.activity:activity-ktx:1.8.1") // https://developer.android.com/jetpack/androidx/versions
    api("androidx.annotation:annotation:1.7.0")
    api("androidx.appcompat:appcompat:1.6.1")
    api("androidx.camera:camera-camera2:1.3.0")
    api("androidx.camera:camera-lifecycle:1.3.0")
    api("androidx.camera:camera-video:1.3.0")
    api("androidx.camera:camera-view:1.3.0")
    api("androidx.cardview:cardview:1.0.0")
    api("androidx.collection:collection-ktx:1.3.0")
    api("androidx.coordinatorlayout:coordinatorlayout:1.2.0")
    api("androidx.core:core-ktx:1.12.0")
    api("androidx.fragment:fragment-ktx:1.6.2")
    api("androidx.lifecycle:lifecycle-livedata-ktx:2.6.2")
    api("androidx.lifecycle:lifecycle-service:2.6.2")
    api("androidx.lifecycle:lifecycle-viewmodel-ktx:2.6.2")
    api("androidx.preference:preference-ktx:1.2.1")
    api("androidx.recyclerview:recyclerview:1.3.2")
    api("androidx.security:security-crypto:1.0.0")
    api("androidx.viewpager2:viewpager2:1.0.0")
    api("androidx.window:window:1.2.0")
    api("com.google.android.material:material:1.10.0") // https://github.com/material-components/material-components-android
    api(platform("com.google.firebase:firebase-bom:32.6.0")) // https://firebase.google.com/docs/android/setup
    api("com.google.firebase:firebase-analytics")
    api("com.google.firebase:firebase-crashlytics")
    api("com.google.android.gms:play-services-ads:22.5.0")
}
