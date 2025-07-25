import org.gradle.api.tasks.testing.logging.TestLogEvent.FAILED
import org.gradle.api.tasks.testing.logging.TestLogEvent.SKIPPED
import org.gradle.api.tasks.testing.logging.TestLogEvent.STANDARD_ERROR
import org.gradle.api.tasks.testing.logging.TestLogEvent.STANDARD_OUT
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    id("com.android.application")
    id("kotlin-android")
    // https://github.com/jeremymailen/kotlinter-gradle/releases
    id("org.jmailen.kotlinter") version "5.1.1"
    // https://github.com/Kotlin/kotlinx-kover/releases
    id("org.jetbrains.kotlinx.kover") version "0.9.1"
}

android {
    namespace = "cat.mvmike.minimalcalendarwidget"

    // https://source.android.com/setup/start/build-numbers
    val minAndroidVersion = 26   // 8.0
    val androidVersion = 36      // 16.0

    // https://adoptium.net/temurin/releases/
    val javaVersion = JavaVersion.VERSION_21

    compileSdk = androidVersion

    defaultConfig {
        applicationId = namespace
        minSdk = minAndroidVersion
        targetSdk = androidVersion
        versionCode = 90
        versionName = "2.18.4"

        multiDexEnabled = true
    }

    compileOptions {
        compileOptions.encoding = Charsets.UTF_8.name()

        sourceCompatibility = javaVersion
        targetCompatibility = javaVersion
    }

    buildFeatures {
        buildConfig = true
    }

    kotlin {
        compilerOptions {
            jvmTarget = JvmTarget.fromTarget(javaVersion.toString())
        }
    }

    androidResources {
        @Suppress("UnstableApiUsage")
        generateLocaleConfig = true
    }

    tasks.withType<Test> {
        useJUnitPlatform()
        failFast = true
        jvmArgs("-XX:+EnableDynamicAgentLoading")
        testLogging {
            events(SKIPPED, FAILED, STANDARD_ERROR, STANDARD_OUT)
        }
        afterSuite(
            KotlinClosure2(
                { desc: TestDescriptor, result: TestResult ->
                    desc.parent
                        ?.let { return@KotlinClosure2 } // only the outermost suite
                        ?: println(
                            "${result.resultType} (" +
                                "${result.testCount} tests - " +
                                "${result.successfulTestCount} successes, " +
                                "${result.failedTestCount} failures, " +
                                "${result.skippedTestCount} skipped)"
                        )
                }
            )
        )
    }

    /*
     * To sign release builds, create the file gradle.properties in ~/.gradle/ with this content:
     * signingStoreFile=key.store
     * signingStorePassword=xxx
     * signingKeyAlias=alias
     * signingKeyPassword=xxx
     */
    val isKeyStoreDefined = project.hasProperty("signingStoreFile")
        && project.hasProperty("signingStorePassword")
        && project.hasProperty("signingKeyAlias")
        && project.hasProperty("signingKeyPassword")
    signingConfigs {
        if (isKeyStoreDefined) {
            create("release") {
                println("Found sign properties in gradle.properties! Signing build...")
                storeFile = file(properties["signingStoreFile"]!!)
                storePassword = properties["signingStorePassword"]!! as String
                keyAlias = properties["signingKeyAlias"]!! as String
                keyPassword = properties["signingKeyPassword"]!! as String
            }
        }
    }

    buildTypes {
        release {
            signingConfig = when {
                isKeyStoreDefined -> signingConfigs.getByName("release")
                else -> null
            }
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android.txt"),
                "${project.rootDir}/config/proguard/proguard-rules.pro"
            )
        }
    }

    applicationVariants.all {
        if (buildType.name == "release") {
            outputs.all {
                val output = this as? com.android.build.gradle.internal.api.BaseVariantOutputImpl
                output?.outputFileName = "min-cal-widget-v${defaultConfig.versionName}.apk"
            }
        }
    }
}

kover {
    reports {
        filters {
            excludes {
                androidGeneratedClasses()
                classes(
                    "cat.mvmike.minimalcalendarwidget.domain.configuration.MultilinePreference",
                    "cat.mvmike.minimalcalendarwidget.domain.configuration.MultilineListPreference",
                    "cat.mvmike.minimalcalendarwidget.domain.configuration.MultilineCheckBoxPreference",
                    "cat.mvmike.minimalcalendarwidget.domain.configuration.MultilineSeekBarPreference",
                    "cat.mvmike.minimalcalendarwidget.infrastructure.resolver.GraphicResolver",
                    "cat.mvmike.minimalcalendarwidget.infrastructure.resolver.SystemResolver"
                )
                packages("cat.mvmike.minimalcalendarwidget.infrastructure.activity.*")
            }
        }
        verify {
            rule {
                bound {
                    minValue = 85
                }
            }
        }
    }
}

dependencies {
    // https://developer.android.com/jetpack/androidx/releases/appcompat
    implementation("androidx.appcompat:appcompat:1.7.1")
    // https://developer.android.com/jetpack/androidx/releases/core
    implementation("androidx.core:core-ktx:1.16.0")
    // https://developer.android.com/jetpack/androidx/releases/multidex
    implementation("androidx.multidex:multidex:2.0.1")
    // https://developer.android.com/jetpack/androidx/releases/preference
    implementation("androidx.preference:preference-ktx:1.2.1")

    // https://github.com/junit-team/junit5/releases
    val junitJupiterVersion = "5.13.4"
    testImplementation("org.junit.jupiter:junit-jupiter-api:$junitJupiterVersion")
    testImplementation("org.junit.jupiter:junit-jupiter-params:$junitJupiterVersion")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:$junitJupiterVersion")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

    // https://github.com/mockk/mockk/releases
    testImplementation("io.mockk:mockk:1.14.5")

    // https://github.com/assertj/assertj-core/tags
    testImplementation("org.assertj:assertj-core:3.27.3")

    // https://github.com/TNG/ArchUnit/releases
    testImplementation("com.tngtech.archunit:archunit-junit5:1.4.1")

    // https://github.com/qos-ch/slf4j/tags
    testImplementation("org.slf4j:slf4j-simple:2.0.17")
}
