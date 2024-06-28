plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.zippermine"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.zippermine"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        multiDexEnabled = true
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
    kotlinOptions {
        jvmTarget = "1.8"
    }

    buildFeatures{
        viewBinding = true
    }

}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.user.messaging.platform)
    implementation(libs.play.services.ads.lite)
    implementation(libs.firebase.config.ktx)
//    implementation(libs.compose.preview.renderer)
//    implementation(libs.androidx.room.compiler)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

//    implementation ("com.android.support:appcompat-v7:26.0.2")
//    implementation ("com.android.support:design:26.0.2")

    //sdp & ssp
    implementation ("com.intuit.sdp:sdp-android:1.1.1")
    implementation ("com.intuit.ssp:ssp-android:1.1.0")

    //shimmer
    implementation ("com.facebook.shimmer:shimmer:0.5.0")

    //lottie animation
    implementation ("com.airbnb.android:lottie:5.0.3")

    //ads admob
    implementation ("com.google.android.gms:play-services-ads:23.1.0")

    //firebase
    implementation ("com.google.firebase:firebase-config-ktx:22.0.0")
    implementation ("com.google.firebase:firebase-analytics-ktx:22.0.0")

    implementation(platform("com.google.firebase:firebase-bom:33.0.0"))

    implementation("com.google.firebase:firebase-analytics")


    implementation ("io.github.ParkSangGwon:tedpermission-normal:3.3.0")

    implementation ("com.github.bumptech.glide:glide:4.13.2")
    annotationProcessor ("com.github.bumptech.glide:compiler:4.13.2")

    //pin dots view
    implementation ("com.github.poovamraj:PinEditTextField:1.2.6")

    // PowerPreferences Lib
    implementation ("com.aliassadi:power-preference-lib:2.0.0")

    implementation ("com.amirarcane.lock-screen:lockscreen:2.0.0")
    api ("com.multidots:fingerprint-auth:1.0.1")

    val lifecycle_version = ("2.0.0")
    implementation ("androidx.lifecycle:lifecycle-extensions:$lifecycle_version")
    implementation ("androidx.lifecycle:lifecycle-runtime:$lifecycle_version")
    annotationProcessor ("androidx.lifecycle:lifecycle-compiler:$lifecycle_version")

    val multidex_version = "2.0.1"
    implementation("androidx.multidex:multidex:$multidex_version")

}