plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.kapt)
    alias(libs.plugins.hilt)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.google.gms.google.services)
    alias(libs.plugins.ksp)
    alias(libs.plugins.room)
    alias(libs.plugins.protobuf)
    alias(libs.plugins.compose.screenshot)
}

android {
    namespace = "com.jakubmeysner.legitnik"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.jakubmeysner.legitnik"
        minSdk = 29
        targetSdk = 35
        versionCode = 1
        versionName = "0.1.0"

        testInstrumentationRunner = "com.jakubmeysner.legitnik.HiltTestRunner"
    }

    buildFeatures {
        buildConfig = true
    }

    signingConfigs {
        if (properties.containsKey("storeFile")) {
            create("release") {
                storeFile = properties["storeFile"]?.let { file(it) }
                storePassword = properties["storePassword"].toString()
                keyAlias = properties["keyAlias"].toString()
                keyPassword = properties["keyPassword"].toString()
            }
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false

            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )

            if (properties.containsKey("storeFile")) {
                signingConfig = signingConfigs.getByName("release")
            }
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    kotlinOptions {
        jvmTarget = "11"
    }

    androidResources {
        generateLocaleConfig = true
    }

    packaging {
        resources {
            excludes += "META-INF/versions/9/OSGI-INF/MANIFEST.MF"
            excludes += "META-INF/*"
            merges += "META-INF/androidx.compose.*.version"
            pickFirsts += "xsd/*"
        }
    }

    room {
        schemaDirectory("$projectDir/schemas")
    }

    testOptions {
        managedDevices {
            localDevices {
                create("pixel5api30") {
                    device = "Pixel 5"
                    apiLevel = 30
                    systemImageSource = "google"
                }
            }
        }
    }

    experimentalProperties["android.experimental.enableScreenshotTest"] = true
}

dependencies {
    implementation(libs.coil.compose)
    implementation(libs.coil.network.okhttp)
    implementation(libs.retrofit)
    implementation(libs.converter.gson)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.play.services.wearable)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.hilt)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.kotlinx.serialization.core)
    implementation(libs.androidx.hilt.navigation.compose)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.lifecycle.viewmodel.savedstate)
    implementation(libs.play.services.maps)
    implementation(libs.google.maps.compose)
    implementation(libs.firebase.messaging)
    implementation(libs.vico.compose)
    implementation(libs.vico.compose.m3)
    implementation(files("libs/acssmc-1.1.5.jar"))
    implementation(libs.bouncycastle.bcprov)
    implementation(libs.bouncycastle.bcpkix)
    implementation(libs.dss.cades)
    implementation(libs.dss.service)
    implementation(libs.dss.spi)
    implementation(libs.dss.tsl.validation)
    implementation(libs.dss.utils.google.guava)
    implementation(libs.accompanist.permissions)
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    debugImplementation(libs.mockk.android)
    debugImplementation(libs.mockk.agent)
    androidTestImplementation(libs.androidx.junit.ktx)
    androidTestImplementation(libs.androidx.ui.test.junit4.android)
    ksp(libs.androidx.room.compiler)
    kapt(libs.hilt.compiler)
    kapt(libs.androidx.lifecycle.compiler)
    testImplementation(libs.junit)
    testImplementation(libs.mockk.android)
    testImplementation(libs.mockk.agent)
    testImplementation(libs.kotlinx.coroutines.test)
    screenshotTestImplementation(libs.androidx.compose.ui.tooling)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    androidTestImplementation(libs.hilt.testing)
    kaptAndroidTest(libs.hilt.compiler)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
    implementation(libs.datastore.protobuf)
    implementation(libs.protobuf.lite)
    kaptAndroidTest(libs.hilt.compiler)
}

kapt {
    correctErrorTypes = true
}

protobuf {
    protoc {
        artifact = "com.google.protobuf:protoc:3.25.3"
    }
    generateProtoTasks {
        all().forEach { task ->
            task.builtins {
                register("java") {
                    option("lite")
                }
            }
        }
    }
}
