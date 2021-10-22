buildscript {

    //https://kotlinlang.org/releases.html
    val kotlinVersion by extra { "1.5.31" }

    dependencies {
        //https://developer.android.com/studio/releases/gradle-plugin
        classpath("com.android.tools.build:gradle:7.0.3")
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