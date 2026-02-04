plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    kotlin("plugin.serialization") version "2.0.21"
    id("com.google.devtools.ksp")
    id("kotlin-parcelize")
}

android {
    namespace = "com.bettafish.flarent"

    compileSdk {
        version = release(36) {
            minorApiLevel = 1
        }
    }

    defaultConfig {
        applicationId = "com.bettafish.flarent"
        minSdk = 26
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        // Flarum base URL for API calls (override in gradle.properties if needed)
        buildConfigField("String", "FLARUM_BASE_URL", "\"https://community.wvbtech.com/\"")
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
    buildFeatures {
        compose = true
        buildConfig = true
    }
    packaging {
        exclude ("META-INF/LICENSE-LGPL-2.1.txt")
        exclude ("META-INF/LICENSE-LGPL-3.txt")
        exclude ("META-INF/LICENSE-W3C-TEST")
        exclude ("META-INF/DEPENDENCIES")
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.material3.adaptive.navigation.suite)
    implementation(libs.androidx.navigation.common.ktx)
    implementation(libs.androidx.compose.animation)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
    implementation("com.github.Gurupreet:FontAwesomeCompose:1.0.0")
    implementation("androidx.compose.material:material-icons-extended")
    implementation("com.google.accompanist:accompanist-pager:0.30.1")
    implementation("com.github.jasminb:jsonapi-converter:0.11")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.15.2")
    implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.7.1")
    implementation("com.zmkn.jackson:kotlinx-datetime-jackson-module:1.0.0")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310")
    implementation("androidx.paging:paging-runtime:3.3.6")
    implementation("androidx.paging:paging-compose:3.3.6")
    implementation("com.vladsch.flexmark:flexmark-all:0.64.8")
    implementation("androidx.datastore:datastore-preferences:1.2.0")

    implementation("com.mikepenz:multiplatform-markdown-renderer:0.39.1")
    implementation("com.mikepenz:multiplatform-markdown-renderer-m3:0.39.1")
    implementation("com.mikepenz:multiplatform-markdown-renderer-coil3:0.39.1")
    implementation("com.mikepenz:multiplatform-markdown-renderer-code:0.39.1")
    implementation("dev.snipme:highlights:1.1.0")

    val markwonVersion = "4.6.2"
    implementation("io.noties.markwon:core:${markwonVersion}")
    implementation("io.noties.markwon:ext-latex:${markwonVersion}")
    implementation("io.noties.markwon:ext-strikethrough:${markwonVersion}")
    implementation("io.noties.markwon:ext-tables:${markwonVersion}")
    implementation("io.noties.markwon:ext-tasklist:${markwonVersion}")
    implementation("io.noties.markwon:html:${markwonVersion}")
    implementation("io.noties.markwon:image:${markwonVersion}")
    implementation("io.noties.markwon:inline-parser:${markwonVersion}")
    implementation("io.noties.markwon:linkify:${markwonVersion}")
    implementation("io.noties.markwon:recycler:${markwonVersion}")
    implementation("io.noties.markwon:recycler-table:${markwonVersion}")
    implementation("io.noties.markwon:simple-ext:${markwonVersion}")
    implementation("io.noties.markwon:syntax-highlight:${markwonVersion}")

    modules {
        module("org.jetbrains:annotations-java5") {
            replacedBy("org.jetbrains:annotations", "annotations-java5 已经过时并导致冲突")
        }
    }

    implementation("io.github.raamcosta.compose-destinations:core:2.3.0")
    ksp("io.github.raamcosta.compose-destinations:ksp:2.3.0")
    implementation("io.github.raamcosta.compose-destinations:bottom-sheet:2.2.0")

    // Networking
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.okhttp3:okhttp:4.11.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.11.0")
    implementation("com.squareup.retrofit2:converter-jackson:2.9.0")

    // Koin (upgrade to 3.x)
    implementation("io.insert-koin:koin-android:3.4.0")
    implementation("io.insert-koin:koin-androidx-compose:3.4.0")

    // Coroutines (upgrade)
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
    implementation("io.coil-kt.coil3:coil-compose:3.3.0")
    implementation("io.coil-kt.coil3:coil-network-okhttp:3.3.0")
    val nav_version = "2.9.6"

    // Jetpack Compose integration
    implementation("androidx.navigation:navigation-compose:$nav_version")

    // Views/Fragments integration
    implementation("androidx.navigation:navigation-fragment:$nav_version")
    implementation("androidx.navigation:navigation-ui:$nav_version")

    // Feature module support for Fragments
    implementation("androidx.navigation:navigation-dynamic-features-fragment:$nav_version")

    // Testing Navigation
    androidTestImplementation("androidx.navigation:navigation-testing:$nav_version")

    // JSON serialization library, works with the Kotlin serialization plugin
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.7.3")

}
