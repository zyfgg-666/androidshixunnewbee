plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.newbee2"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.newbee2"
        minSdk = 24
        targetSdk = 35
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
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

    // Glide图片加载
    implementation("com.github.bumptech.glide:glide:4.12.0")
    // OkHttp网络请求
    implementation("com.squareup.okhttp3:okhttp:4.9.3")
    // RecyclerView
    implementation("androidx.recyclerview:recyclerview:1.3.2")
    // CardView
    implementation("androidx.cardview:cardview:1.0.0")
    // Gson JSON解析
    implementation("com.google.code.gson:gson:2.10.1")
}