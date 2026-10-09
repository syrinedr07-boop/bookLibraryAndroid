import java.util.Properties
import org.gradle.api.DefaultTask
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.google.services)
}

val localProperties = Properties()
val localPropertiesFile = rootProject.file("local.properties")
if (localPropertiesFile.exists()) {
    localProperties.load(localPropertiesFile.inputStream())
}
val googleBooksApiKey = localProperties.getProperty("GOOGLE_BOOKS_API_KEY") ?: ""
val firebaseApiKey = localProperties.getProperty("FIREBASE_API_KEY") ?: googleBooksApiKey

abstract class InjectApiKeyTask : DefaultTask() {
    @get:Input
    abstract val apiKey: Property<String>

    @get:InputFile
    @get:Optional
    abstract val sourceGoogleServicesFile: RegularFileProperty

    @get:OutputFile
    abstract val outputGoogleServicesFile: RegularFileProperty

    @TaskAction
    fun inject() {
        val key = apiKey.get()
        val srcFile = sourceGoogleServicesFile.orNull?.asFile
        val outFile = outputGoogleServicesFile.get().asFile

        val baseContent = if (srcFile != null && srcFile.exists()) {
            srcFile.readText()
        } else {
            """
            {
              "project_info": {
                "project_number": "710086510903",
                "project_id": "book-library-780b8",
                "storage_bucket": "book-library-780b8.firebasestorage.app"
              },
              "client": [
                {
                  "client_info": {
                    "mobilesdk_app_id": "1:710086510903:android:623f5f23a458585d6df2b2",
                    "android_client_info": {
                      "package_name": "com.example.librarybook"
                    }
                  },
                  "oauth_client": [],
                  "api_key": [
                    {
                      "current_key": "FIREBASE_API_KEY"
                    }
                  ],
                  "services": {
                    "appinvite_service": {
                      "other_platform_oauth_client": []
                    }
                  }
                }
              ],
              "configuration_version": "1"
            }
            """.trimIndent()
        }

        var updatedContent = baseContent
        if (key.isNotEmpty()) {
            updatedContent = updatedContent.replace("FIREBASE_API_KEY", key)
        }
        outFile.parentFile.mkdirs()
        outFile.writeText(updatedContent)
    }
}

val injectApiKeyTask = tasks.register<InjectApiKeyTask>("injectFirebaseApiKey") {
    apiKey.set(firebaseApiKey)
    sourceGoogleServicesFile.set(layout.projectDirectory.file("google-services.json"))
    outputGoogleServicesFile.set(layout.projectDirectory.file("src/debug/google-services.json"))
}

tasks.matching { it.name.startsWith("process") && it.name.endsWith("GoogleServices") }.configureEach {
    dependsOn(injectApiKeyTask)
}

tasks.matching { it.name == "preBuild" }.configureEach {
    dependsOn(injectApiKeyTask)
}

android {
    namespace = "com.example.librarybook"
    compileSdk {
        version = release(37)
    }

    defaultConfig {
        applicationId = "com.example.librarybook"
        minSdk = 24
        targetSdk = 37
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        buildConfigField("String", "GOOGLE_BOOKS_API_KEY", "\"$googleBooksApiKey\"")
    }

    buildTypes {
        release {
            optimization {
                enable = false
            }
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
}

dependencies {
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.material.icons.extended)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.compose)

    // Retrofit & OkHttp
    implementation(libs.retrofit)
    implementation(libs.retrofit.converter.gson)
    implementation(libs.okhttp.logging)

    // Koin
    implementation(libs.koin.android)
    implementation(libs.koin.androidx.compose)

    // Coil
    implementation(libs.coil.compose)

    // Firebase
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.auth)

    testImplementation(libs.junit)
    testImplementation(libs.kotlinx.coroutines.test)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
    debugImplementation(libs.androidx.compose.ui.tooling)
}
