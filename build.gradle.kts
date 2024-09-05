plugins {
    kotlin("jvm") version "2.0.20"
    id("com.github.johnrengelman.shadow") version "8.1.1"
    application
}

group = "com.rsplwe.esurfing"
version = "1.5.0"

repositories {
    mavenCentral()
    maven(url = "https://jitpack.io")
}

dependencies {
    implementation("com.github.zhkl0228:unicorn:1.0.14")
    implementation("com.github.zhkl0228:unidbg-android:0.9.7")
    implementation("com.github.zhkl0228:unidbg-api:0.9.7")
    implementation("com.github.zhkl0228:unidbg-dynarmic:0.9.7")
    implementation("com.github.zhkl0228:unidbg-unicorn2:0.9.7")
    implementation("org.slf4j:slf4j-log4j12:2.0.16")
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("com.github.yescallop:fluent-uri:d8e12f23af")
    implementation("commons-cli:commons-cli:1.9.0")
    implementation("commons-codec:commons-codec:1.17.1")
    implementation("org.jsoup:jsoup:1.18.1")
    implementation("com.google.code.gson:gson:2.11.0")

}

kotlin {
    jvmToolchain(20)
}

application {
    mainClass.set("com.rsplwe.esurfing.DialerApp")
}
