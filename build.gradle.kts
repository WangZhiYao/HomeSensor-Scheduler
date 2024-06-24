plugins {
    war
    alias(libs.plugins.spring.boot)
    alias(libs.plugins.dependency.management)
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.plugin.spring)
}

group = "com.paperloong.homesensor"
version = "0.0.1-SNAPSHOT"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(17)
    }
}

kotlin {
    jvmToolchain(17)
}

repositories {
    mavenCentral()
}

dependencies {
    providedRuntime(libs.spring.boot.starter.tomcat)
    testRuntimeOnly(libs.junit.platform.launcher)
    testImplementation(libs.reactor.test)
    testImplementation(libs.kotlin.test.junit5)
    testImplementation(libs.spring.boot.starter.test)

    implementation(libs.spring.boot.starter.data.rest)
    implementation(libs.spring.boot.starter.data.mongodb)
    implementation(libs.spring.boot.starter.mail)
    implementation(libs.spring.boot.starter.quartz)
    implementation(libs.spring.boot.starter.web)
    implementation(libs.spring.boot.starter.webflux)
    implementation(libs.spring.integration.mqtt)

    implementation(libs.kotlin.reflect)
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.coroutines.reactor)
    implementation(libs.jackson.module.kotlin)
    implementation(libs.reactor.kotlin.extensions)

    implementation(libs.bundles.network)
}

kotlin {
    compilerOptions {
        freeCompilerArgs.addAll("-Xjsr305=strict")
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}
