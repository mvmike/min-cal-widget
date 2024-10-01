buildscript {

    dependencies {
        // https://developer.android.com/studio/releases/gradle-plugin
        classpath("com.android.tools.build:gradle:8.7.0")
        // https://kotlinlang.org/releases.html
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:2.0.20")
    }

    repositories {
        mavenCentral()
        google()
    }
}

allprojects {
    repositories {
        mavenCentral()
        google()
    }
}

defaultTasks(
    //"formatKotlin",
    "lintKotlin",
    "clean",
    //"koverHtmlReportRelease",
    "koverVerifyRelease",
    "assembleRelease"
)
