buildscript {

    dependencies {
        // https://developer.android.com/studio/releases/gradle-plugin
        classpath("com.android.tools.build:gradle:8.0.0")
        // https://kotlinlang.org/releases.html
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.8.20")
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

defaultTasks("lintKotlin", "clean", "testRelease", "assembleRelease")
