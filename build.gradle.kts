plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}
android {
    namespace = "com.memeticbox.mobile"
    compileSdk = 35
    defaultConfig {
        applicationId = "com.memeticbox.mobile"
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "0.1.0"
    }
}
