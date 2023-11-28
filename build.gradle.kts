plugins {
    id("com.android.application") version "8.1.4" apply false // https://developer.android.com/build/releases/gradle-plugin https://maven.google.com/web/index.html
    id("com.android.library") version "8.1.4" apply false
    id("org.jetbrains.kotlin.android") version "1.9.21" apply false // https://github.com/JetBrains/kotlin
    id("org.jetbrains.kotlin.jvm") version "1.9.21" apply false
    id("com.google.gms.google-services") version "4.4.0" apply false // https://firebase.google.com/docs/android/setup
    id("com.google.firebase.crashlytics") version "2.9.9" apply false // https://firebase.google.com/docs/crashlytics/get-started?platform=android
}
