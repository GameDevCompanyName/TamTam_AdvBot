import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import java.net.URI

plugins {
    kotlin("jvm") version "1.3.50"
    id("com.github.johnrengelman.shadow") version "5.2.0"
}

group = "me.evgen"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven { url = URI("https://dl.bintray.com/kotlin/kotlinx") }
    maven { url = URI("https://plugins.gradle.org/m2/") }
    jcenter()
    maven { url = URI("https://oss.jfrog.org//artifactory/oss-snapshot-local") }
}

buildscript {
    repositories {
        jcenter()
    }
    dependencies {
        classpath("com.github.jengelman.gradle.plugins:shadow:5.2.0")
    }
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))

    implementation ("com.namazed.botsdk:library:0.4.0")

    implementation("ch.qos.logback:logback-classic:1.2.1")

    implementation("com.h2database:h2:1.4.197")

    implementation("com.squareup.okhttp3:logging-interceptor:3.12.0")

    implementation("org.jetbrains.kotlinx:kotlinx-serialization-runtime:0.14.0")
    implementation("org.jetbrains.exposed:exposed:0.11.2")

    implementation("org.postgresql:postgresql:42.2.20")
    implementation("org.hibernate:hibernate-core:5.4.31.Final")

    implementation("com.google.code.gson:gson:2.8.6")

    implementation("io.javalin:javalin:3.13.7")

    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.3.0")
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

tasks {
    named<com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar>("shadowJar") {
        archiveBaseName.set("AdvBot")
        classifier = null
        version = null
        mergeServiceFiles()
        manifest {
            attributes(mapOf("Main-Class" to "me.evgen.advbot.BotLogicKt"))
        }
    }
}
