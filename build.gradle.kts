plugins {
    kotlin("jvm") version "2.1.21"
    id("com.gradleup.shadow") version "8.3.6"
    application
}

group = "com.rsplwe.esurfing"
version = "1.9.5"

repositories {
    mavenCentral()
    maven(url = "https://jitpack.io")
}

dependencies {
    implementation("org.slf4j:slf4j-log4j12:2.0.17")
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("com.github.yescallop:fluent-uri:d8e12f23af")
    implementation("commons-cli:commons-cli:1.9.0")
    implementation("commons-codec:commons-codec:1.18.0")
    implementation("org.jsoup:jsoup:1.20.1")
    implementation("com.google.code.gson:gson:2.13.1")
}

kotlin {
    jvmToolchain(23)
}

application {
    mainClass.set("com.rsplwe.esurfing.DialerApp")
}
