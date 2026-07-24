import org.gradle.api.tasks.testing.logging.TestLogEvent.FAILED
import org.gradle.api.tasks.testing.logging.TestLogEvent.SKIPPED
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    id("com.android.application")
    // https://github.com/jeremymailen/kotlinter-gradle/releases
    id("org.jmailen.kotlinter") version "5.6.0"
    // https://github.com/Kotlin/kotlinx-kover/releases
    id("org.jetbrains.kotlinx.kover") version "0.9.9"
}

dependencies {
    // https://developer.android.com/jetpack/androidx/releases/appcompat
    implementation("androidx.appcompat:appcompat:1.7.1")
    // https://developer.android.com/jetpack/androidx/releases/core
    implementation("androidx.core:core-ktx:1.19.0")
    // https://developer.android.com/jetpack/androidx/releases/preference
    implementation("androidx.preference:preference-ktx:1.2.1")

    // https://github.com/junit-team/junit5/releases
    val junitJupiterVersion = "6.1.2"
    testImplementation("org.junit.jupiter:junit-jupiter-api:$junitJupiterVersion")
    testImplementation("org.junit.jupiter:junit-jupiter-params:$junitJupiterVersion")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:$junitJupiterVersion")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    // https://github.com/mockk/mockk/releases
    testImplementation("io.mockk:mockk:1.14.11")
    // https://github.com/assertj/assertj-core/tags
    testImplementation("org.assertj:assertj-core:3.27.7")
    // https://github.com/TNG/ArchUnit/releases
    testImplementation("com.tngtech.archunit:archunit-junit5:1.4.2")
    // https://github.com/qos-ch/slf4j/tags
    testImplementation("org.slf4j:slf4j-simple:2.0.18")
}

// https://adoptium.net/temurin/releases/
private val javaVersion = JavaVersion.VERSION_21

kotlin {
    compilerOptions {
        jvmTarget = JvmTarget.fromTarget(javaVersion.toString())
    }
}

android {
    namespace = "cat.mvmike.minimalcalendarwidget"

    // https://source.android.com/setup/start/build-numbers
    val minAndroidVersion = 26   // 8.0
    val androidVersion = 37      // 17.0

    compileSdk = androidVersion

    defaultConfig {
        applicationId = namespace
        minSdk = minAndroidVersion
        targetSdk = androidVersion
        versionCode = 93
        versionName = "2.19.0"

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

    androidResources {
        @Suppress("UnstableApiUsage")
        generateLocaleConfig = true
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
                storeFile = file(property("signingStoreFile")!!)
                storePassword = property("signingStorePassword")!! as String
                keyAlias = property("signingKeyAlias")!! as String
                keyPassword = property("signingKeyPassword")!! as String
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
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "${project.rootDir}/config/proguard/proguard-rules.pro"
            )
        }
        create("beta") {
            initWith(getByName("release"))
            applicationIdSuffix = ".BETA"
        }
    }

    @Suppress("WrongGradleMethod")
    androidComponents {
        onVariants { variant ->
            when (variant.buildType) {
                "release" -> "min-cal-widget-v${defaultConfig.versionName}.apk"
                "beta" -> "min-cal-widget-v${defaultConfig.versionName}-BETA.apk"
                else -> null
            }?.let { apkName ->
                variant.outputs
                    .filterIsInstance<com.android.build.api.variant.impl.VariantOutputImpl>()
                    .forEach { it.outputFileName = apkName }
            }
        }
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
    jvmArgs("-XX:+EnableDynamicAgentLoading")
    maxParallelForks = (Runtime.getRuntime().availableProcessors() / 2).coerceAtLeast(1)
    testLogging {
        events(SKIPPED, FAILED)
    }
    addTestListener(object : TestListener {
        override fun beforeSuite(suite: TestDescriptor) {}
        override fun beforeTest(testDescriptor: TestDescriptor) {}
        override fun afterTest(testDescriptor: TestDescriptor, result: TestResult) {}
        override fun afterSuite(suite: TestDescriptor, result: TestResult) {
            // only print outermost Gradle Test Executor results
            suite.className ?: suite.parent?.let {
                println(
                    "${result.resultType} " +
                        "(${result.testCount} tests - " +
                        "${result.successfulTestCount} successes, " +
                        "${result.failedTestCount} failures, " +
                        "${result.skippedTestCount} skipped)"
                )
            }
        }
    })
}

kover {
    reports {
        filters {
            excludes {
                androidGeneratedClasses()
                classes(
                    // AppWidgetProvider implementation
                    "cat.mvmike.minimalcalendarwidget.MonthWidget",
                    // androidx.preference class extensions to allow multiline title and summary
                    "cat.mvmike.minimalcalendarwidget.domain.configuration.MultilinePreference",
                    "cat.mvmike.minimalcalendarwidget.domain.configuration.MultilineListPreference",
                    "cat.mvmike.minimalcalendarwidget.domain.configuration.MultilineCheckBoxPreference",
                    "cat.mvmike.minimalcalendarwidget.domain.configuration.MultilineSeekBarPreference",
                    // (AppCompat)Activity implementations
                    "cat.mvmike.minimalcalendarwidget.infrastructure.activity.ConfigurationActivity",
                    "cat.mvmike.minimalcalendarwidget.infrastructure.activity.PermissionActivity",
                    // output adapters
                    "cat.mvmike.minimalcalendarwidget.infrastructure.resolver.GraphicResolver",
                    "cat.mvmike.minimalcalendarwidget.infrastructure.resolver.SystemResolver"
                )
            }
        }
        verify {
            rule {
                minBound(95)
            }
        }
    }
}