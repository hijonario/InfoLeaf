plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.infoleaf"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.infoleaf"
        minSdk = 33
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

    implementation(libs.postgresql)
    implementation(libs.jcraft)
    implementation("org.postgresql:postgresql:42.2.9")
    implementation("org.locationtech.proj4j:proj4j:1.1.0")
    implementation("com.google.android.material:material:1.9.0")
}