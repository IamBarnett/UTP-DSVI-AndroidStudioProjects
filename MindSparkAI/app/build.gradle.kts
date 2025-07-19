plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("kotlin-kapt")
    id("com.google.dagger.hilt.android")
    id("com.google.gms.google-services")
}

android {
    namespace = "com.universidad.mindsparkai"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.universidad.mindsparkai"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        // API Keys configuration
        // Crear archivo local.properties en la raíz del proyecto con:
        // OPENAI_API_KEY="sk-your-key"
        // CLAUDE_API_KEY="your-key"
        // GEMINI_API_KEY="your-key"

        val localProperties = project.rootProject.file("local.properties")
        val properties = java.util.Properties()

        if (localProperties.exists()) {
            properties.load(localProperties.inputStream())
        }

        buildConfigField("String", "OPENAI_API_KEY", "\"${properties.getProperty("OPENAI_API_KEY", "")}\"")
        buildConfigField("String", "CLAUDE_API_KEY", "\"${properties.getProperty("CLAUDE_API_KEY", "")}\"")
        buildConfigField("String", "GEMINI_API_KEY", "\"${properties.getProperty("GEMINI_API_KEY", "")}\"")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
        debug {
            isDebuggable = true
            applicationIdSuffix = ".debug"
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
        viewBinding = true
        dataBinding = true
        buildConfig = true // Importante para BuildConfig
    }

    kapt {
        correctErrorTypes = true
        useBuildCache = false
        generateStubs = true
    }

    packagingOptions {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    implementation("androidx.core:core-ktx:1.10.1")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.9.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")

    // Navigation
    implementation("androidx.navigation:navigation-fragment-ktx:2.6.0")
    implementation("androidx.navigation:navigation-ui-ktx:2.6.0")

    // Lifecycle
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.6.1")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.6.1")

    // RecyclerView
    implementation("androidx.recyclerview:recyclerview:1.3.1")
    implementation("androidx.cardview:cardview:1.0.0")

    // Firebase
    implementation(platform("com.google.firebase:firebase-bom:32.2.3"))
    implementation("com.google.firebase:firebase-auth-ktx")
    implementation("com.google.firebase:firebase-firestore-ktx")
    implementation("com.google.android.gms:play-services-auth:20.6.0")

    // Networking
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.11.0")

    // Image loading
    implementation("com.github.bumptech.glide:glide:4.15.1")

    // Hilt - Dependency Injection
    implementation("com.google.dagger:hilt-android:2.47")
    kapt("com.google.dagger:hilt-compiler:2.47")

    // Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")

    // JSON Parsing
    implementation("com.google.code.gson:gson:2.10.1")

    // File handling (para PDFs y DOCX en el futuro)
    // implementation("com.itextpdf:itext7-core:7.2.5") // Para PDF
    // implementation("org.apache.poi:poi-ooxml:5.2.4") // Para DOCX

    // Security (para encriptar API keys si es necesario)
    implementation("androidx.security:security-crypto:1.1.0-alpha06")

    // Testing
    testImplementation("junit:junit:4.13.2")
    testImplementation("org.mockito:mockito-core:5.5.0")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.3")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation("com.google.dagger:hilt-android-testing:2.47")
    kaptAndroidTest("com.google.dagger:hilt-android-compiler:2.47")
}