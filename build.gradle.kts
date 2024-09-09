import org.jetbrains.kotlin.gradle.tasks.KotlinCompilationTask
import java.net.URI

plugins {
    kotlin("jvm") version "2.0.20"
    kotlin("plugin.serialization") version "2.0.20"
}

group = "io.github.tmarsteel"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven {
        url = URI.create("https://jitpack.io")
    }
}

dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.7.1")
    implementation("com.github.ajalt.clikt:clikt-jvm:4.2.2")
    implementation("com.github.nwillc:ksvg:2.2.0")
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(21)
}

tasks.named<KotlinCompilationTask<*>>("compileKotlin").configure {
    compilerOptions.freeCompilerArgs.add("-opt-in=kotlinx.serialization.ExperimentalSerializationApi")
}