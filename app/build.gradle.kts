plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    id("kotlin-kapt")
    id("com.google.dagger.hilt.android")
}

android {
    namespace = "com.xz.schoolnavinfo"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.xz.schoolnavinfo"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"
        multiDexEnabled = true

        javaCompileOptions {
            annotationProcessorOptions {
                arguments["includeCompileClasspath"] = "true"
            }
        }

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
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    //百度地图
    implementation(files("libs/onsdk_all.aar"))
    implementation(files("libs/NaviTts.aar"))
    implementation(files("libs/BaiduLBS_Android.aar"))
    implementation("com.airbnb.android:lottie:5.2.0")
    //权限
    implementation("com.google.accompanist:accompanist-permissions:0.37.0")
    //依赖注入
    implementation("com.google.dagger:hilt-android:2.51.1")
    kapt("com.google.dagger:hilt-android-compiler:2.51.1")
    //路由
    implementation("androidx.hilt:hilt-navigation-compose:1.2.0")
    //ds
    implementation("androidx.datastore:datastore-preferences:1.1.2")
    //room
    val roomVersion = "2.6.1" // 使用最新版本
    implementation("androidx.room:room-runtime:$roomVersion")
    implementation("androidx.room:room-ktx:$roomVersion")
    kapt("androidx.room:room-compiler:$roomVersion")
    //加载异步图片
    implementation("io.coil-kt:coil-compose:2.6.0")
    //网络请求
    implementation("com.squareup.retrofit2:retrofit:2.9.0") // Retrofit 核心库
    implementation("com.squareup.retrofit2:converter-gson:2.9.0") // Gson 解析 JSON
    implementation("com.squareup.okhttp3:okhttp:4.10.0") // OkHttp 日志
}

kapt {
    correctErrorTypes = true
}
