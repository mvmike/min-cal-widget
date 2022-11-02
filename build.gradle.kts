buildscript {

    //https://kotlinlang.org/releases.html
    val kotlinVersion by extra { "1.7.20" }

    dependencies {
        //https://developer.android.com/studio/releases/gradle-plugin
        classpath("com.android.tools.build:gradle:8.0.0-alpha07")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlinVersion")
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