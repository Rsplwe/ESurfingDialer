plugins {
    kotlin("jvm") version "1.9.20-RC2"
    id("com.github.johnrengelman.shadow") version "8.1.1"
    application
}

group = "com.rsplwe.esurfing"
version = "1.3.0"

repositories {
    mavenCentral()
    maven(url = "https://jitpack.io")
}

dependencies {
    implementation("com.github.zhkl0228:unicorn:1.0.13")
    implementation("com.github.zhkl0228:unidbg-android:0.9.7")
    implementation("com.github.zhkl0228:unidbg-api:0.9.7")
    implementation("com.github.zhkl0228:unidbg-dynarmic:0.9.7")
    implementation("com.github.zhkl0228:unidbg-unicorn2:0.9.7")
    implementation("org.slf4j:slf4j-log4j12:2.0.9")
    implementation("com.squareup.okhttp3:okhttp:4.11.0")
    implementation("com.github.yescallop:fluent-uri:d8e12f23af")
    implementation("commons-cli:commons-cli:1.5.0")
    implementation("commons-codec:commons-codec:1.16.0")
}

kotlin {
    jvmToolchain(21)
}

application {
    mainClass.set("com.rsplwe.esurfing.DialerApp")
}