plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.devtools.ksp")
    id("com.google.gms.google-services")
    id("kotlin-kapt")
    id("androidx.navigation.safeargs.kotlin")
    id("kotlin-parcelize")
    id("com.google.android.libraries.mapsplatform.secrets-gradle-plugin")
}

android {
    namespace = "com.udacity.project4"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.udacity.project4"
        minSdk = 28
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17

    }
    kotlinOptions {
        jvmTarget = "17"
    }

    buildFeatures {
        dataBinding = true
        viewBinding = true
        buildConfig = true
    }

}

dependencies {

    implementation(platform("com.google.firebase:firebase-bom:32.2.3"))
    implementation("androidx.legacy:legacy-support-v4:1.0.0")
    implementation("androidx.test.ext:junit-ktx:1.1.5")
    implementation("androidx.test:runner:1.5.2")

    val roomVersion = "2.5.2"
    implementation("androidx.room:room-runtime:$roomVersion")
    ksp("androidx.room:room-compiler:$roomVersion")
    implementation("androidx.room:room-ktx:$roomVersion")

    val navigationVersion = "2.7.2"
    implementation("androidx.navigation:navigation-fragment-ktx:$navigationVersion")
    implementation("androidx.navigation:navigation-ui-ktx:$navigationVersion")
    implementation("androidx.navigation:navigation-dynamic-features-fragment:$navigationVersion")

    val lifecycleVersion = "2.6.2"
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:$lifecycleVersion")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:$lifecycleVersion")

    implementation("androidx.fragment:fragment-ktx:1.6.1")

    val coroutineVersion = "1.7.3"
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$coroutineVersion")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:$coroutineVersion")

    val koinVersion = "3.4.3"
    implementation("io.insert-koin:koin-core:$koinVersion")
    implementation("io.insert-koin:koin-android:$koinVersion")
    implementation("io.insert-koin:koin-androidx-workmanager:$koinVersion")

    val playServicesVersion = "21.0.1"
    implementation("com.google.android.gms:play-services-location:$playServicesVersion")
    implementation("com.google.android.gms:play-services-maps:18.1.0")

    val workVersion = "2.8.1"
    implementation("androidx.work:work-runtime-ktx:$workVersion")

    implementation("com.google.firebase:firebase-auth-ktx:22.1.1")
    implementation("com.firebaseui:firebase-ui-auth:8.0.2")

    implementation("androidx.core:core-ktx:1.10.1")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.9.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("org.mockito:mockito-android:5.5.0")
    testImplementation("junit:junit:4.13.2")
    testImplementation("org.mockito.kotlin:mockito-kotlin:5.1.0")
    testImplementation("androidx.room:room-testing:$roomVersion")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.6.4")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.6.4")
    testImplementation("android.arch.core:core-testing:1.1.1")
    testImplementation("androidx.test:core-ktx:1.5.0")
    testImplementation("androidx.test.ext:junit-ktx:1.1.5")
    testImplementation("androidx.test:rules:1.5.0")
    debugImplementation("androidx.fragment:fragment-testing:1.6.1")
    androidTestImplementation("androidx.test.espresso:espresso-contrib:3.3.0")
    androidTestImplementation("androidx.test.espresso:espresso-intents:3.3.0")
    androidTestImplementation("androidx.test.espresso.idling:idling-concurrent:3.3.0")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("junit:junit:4.13.2")
    androidTestImplementation("android.arch.core:core-testing:1.1.1")
    androidTestImplementation("org.mockito:mockito-core:5.5.0")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.3.0")
    androidTestImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:$coroutineVersion")
    androidTestImplementation("io.insert-koin:koin-test:3.5.0")
}

