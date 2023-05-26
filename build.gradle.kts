plugins {
    kotlin("jvm") version "1.8.21"
    application
}

group = "com.rsplwe.esurfing"
version = "1.0.0"

repositories {
    mavenCentral()
    maven(url = "https://jitpack.io")
}

dependencies {
    implementation("com.github.zhkl0228:unidbg-dynarmic:0.9.7")
    implementation("com.github.zhkl0228:unicorn:1.0.13")
    implementation("com.github.zhkl0228:unidbg-android:0.9.7")
    implementation("com.github.zhkl0228:unidbg-api:0.9.7")
    implementation("org.slf4j:slf4j-log4j12:2.0.3")
    implementation("com.squareup.okhttp3:okhttp:4.10.0")
    implementation("com.github.yescallop:fluent-uri:d8e12f23af")
    implementation("commons-cli:commons-cli:1.5.0")
    implementation("commons-codec:commons-codec:1.15")


}

kotlin {
    jvmToolchain(17)
}

tasks.jar {
    manifest.attributes["Main-Class"] = "com.rsplwe.esurfing.DialerApp"
    val deps = configurations.runtimeClasspath.get().map(::zipTree)
    from(deps)
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}

application {
    mainClass.set("com.rsplwe.esurfing.DialerApp")
}