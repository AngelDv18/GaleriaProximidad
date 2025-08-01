// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.compose) apply false
    // 🔽 ADD THIS
    id("com.google.gms.google-services") version "4.4.3" apply false
}
// build.gradle.kts (level project)

buildscript {
    dependencies {
        classpath("com.google.gms:google-services:4.3.15")
    }
}
