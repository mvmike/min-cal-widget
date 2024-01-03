buildscript {

    dependencies {
        // https://developer.android.com/studio/releases/gradle-plugin
        classpath("com.android.tools.build:gradle:8.2.1")
        // https://kotlinlang.org/releases.html
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.9.22")
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
    "testRelease",
    "assembleRelease"
)
