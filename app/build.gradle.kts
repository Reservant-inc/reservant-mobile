plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    kotlin("plugin.serialization") version "2.0.0"
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.reservant_mobile"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.reservant.reservant_mobile"
        minSdk = 26
        targetSdk = 34
        versionCode = 3
        versionName = "1.0.1"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }

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
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.2"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {

    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.7.0")
    implementation("androidx.activity:activity-compose:1.8.2")
    implementation(platform("androidx.compose:compose-bom:2024.02.02"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation ("com.googlecode.libphonenumber:libphonenumber:8.12.39")
    implementation("androidx.navigation:navigation-testing:2.7.7")
    implementation("com.google.android.material:material:1.12.0")
    implementation("com.google.firebase:firebase-messaging:24.0.1")
    testImplementation("junit:junit:4.13.2")
    testImplementation("com.google.truth:truth:1.0.1")
    testImplementation ("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.8.0")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation(platform("androidx.compose:compose-bom:2024.02.02"))
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    androidTestImplementation("com.google.truth:truth:1.0.1")
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest:1.6.3")
    implementation("com.eygraber:uri-kmp:0.0.18")

    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.3")
    val ktorVersion: String by project

    implementation("io.ktor:ktor-client-core:$ktorVersion")
    implementation("io.ktor:ktor-client-cio:$ktorVersion")
    implementation("io.ktor:ktor-client-content-negotiation:$ktorVersion")
    implementation("io.ktor:ktor-serialization-kotlinx-json:$ktorVersion")
    implementation("io.ktor:ktor-client-logging:$ktorVersion")
    implementation("io.ktor:ktor-client-auth:$ktorVersion")
    implementation("io.ktor:ktor-client-resources:$ktorVersion")
    implementation("io.ktor:ktor-client-websockets:$ktorVersion")


    // BETA VERSION
    implementation("androidx.navigation:navigation-compose:2.8.0-beta01")

    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.0")
    implementation ("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.8.0")
    implementation ("androidx.compose.material:material-icons-extended:1.6.4")
    implementation ("androidx.datastore:datastore-preferences:1.0.0")
    implementation ("androidx.core:core-splashscreen:1.0.1")
    implementation ("org.osmdroid:osmdroid-android:6.1.18")
    implementation ("com.github.MKergall:osmbonuspack:6.9.0")
    implementation("com.google.android.gms:play-services-location:21.3.0")
    implementation ("com.google.accompanist:accompanist-permissions:0.32.0")

    val pagingVersion = "3.3.0"
    implementation("androidx.paging:paging-runtime-ktx:$pagingVersion")
    implementation("androidx.paging:paging-compose:$pagingVersion")
    implementation("androidx.paging:paging-testing:$pagingVersion")

    // Image caching
    implementation("io.coil-kt.coil3:coil-compose:3.0.0-rc01")
    implementation("io.coil-kt.coil3:coil-network-ktor2:3.0.0-rc01")


    // Charts
    val vicoVersion = "1.15.0"
    implementation("com.patrykandpatrick.vico:compose:$vicoVersion")
    implementation("com.patrykandpatrick.vico:compose-m2:$vicoVersion")
    implementation("com.patrykandpatrick.vico:compose-m3:$vicoVersion")
}