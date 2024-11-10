plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.google.gms.google.services)
}

android {
    namespace = "com.example.podcatsv2"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.podcatsv2"
        minSdk = 24
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

//    packaging {
//        resources {
//            excludes += listOf(
//                "META-INF/DEPENDENCIES",
//                "META-INF/LICENSE",
//                "META-INF/LICENSE.txt",
//                "META-INF/NOTICE",
//                "META-INF/NOTICE.txt"
//                // Add more exclusions if needed
//            )
//        }
//    }
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

    //
    // Firebase Authentication
    implementation(libs.firebase.auth)
    // Firebase Realtime Database
    implementation(libs.firebase.database)

    // Glide for image loading
    implementation(libs.glide)
    annotationProcessor(libs.glide.compiler)
    // ExoPlayer for video playback
    implementation(libs.exoplayer)
    // RecyclerView
    implementation(libs.recyclerview)

    implementation(libs.exoplayer)
    // AWS S3

    //implementation(libs.aws.java.core)

    //implementation(libs.amplify.core)
    //implementation(libs.amplify.storage.s3)
     implementation(libs.amplify.core)
     implementation(libs.amplify.auth.cognito)
     implementation(libs.amplify.storage.s3)
     implementation(libs.amplify.api)
    implementation(libs.amplify.api)

}