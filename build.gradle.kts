buildscript {

    //https://kotlinlang.org/releases.html
    val kotlinVersion by extra { "1.7.10" }

    dependencies {
        //https://developer.android.com/studio/releases/gradle-plugin
        classpath("com.android.tools.build:gradle:7.4.0-alpha10")
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