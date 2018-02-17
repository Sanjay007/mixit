import com.moowork.gradle.gulp.GulpTask
import com.moowork.gradle.node.yarn.YarnInstallTask
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    val kotlinVersion = "1.2.21"
    val nodePluginVersion = "1.1.1"
    id("org.jetbrains.kotlin.jvm") version kotlinVersion
    id("org.jetbrains.kotlin.plugin.spring") version kotlinVersion
    id("com.moowork.node") version nodePluginVersion
    id("com.moowork.gulp") version nodePluginVersion
    id("org.springframework.boot") version "2.0.0.RC1"
    id("io.spring.dependency-management") version "1.0.4.RELEASE"
    id("org.junit.platform.gradle.plugin") version "1.0.2"
}

version = "1.0.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven("https://repo.spring.io/milestone")
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        jvmTarget = "1.8"
        freeCompilerArgs = listOf("-Xjsr305=strict")
    }
}

node {
    version = "9.2.0"
    yarnVersion = "1.3.2"
    download = true
}

dependencies {
    compile("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    compile("org.jetbrains.kotlin:kotlin-reflect")
    compile("com.fasterxml.jackson.module:jackson-module-kotlin")

    compile("org.springframework.boot:spring-boot-starter-webflux") {
        exclude(module = "hibernate-validator")
    }
    compileOnly("org.springframework:spring-context-indexer")
    compile("org.springframework.boot:spring-boot-starter-mail")
    compile("org.springframework.boot:spring-boot-starter-data-mongodb-reactive")
    compile("org.springframework.boot:spring-boot-devtools")

    runtime("de.flapdoodle.embed:de.flapdoodle.embed.mongo")
    compile("com.samskivert:jmustache")
    compile("com.atlassian.commonmark:commonmark:0.9.0")
    compile("com.atlassian.commonmark:commonmark-ext-autolink:0.9.0")
    compile("com.google.api-client:google-api-client:1.23.0")
    compile("com.google.oauth-client:google-oauth-client-jetty:1.23.0")
    compile("com.google.apis:google-api-services-gmail:v1-rev81-1.23.0")
    // TODO delete sendgrid
    compile("com.sendgrid:sendgrid-java:4.1.2")
    compile("com.googlecode.owasp-java-html-sanitizer:owasp-java-html-sanitizer:20171016.1")

    testCompile("org.springframework.boot:spring-boot-starter-test") {
        exclude(module = "junit")
    }
    testCompile("org.junit.jupiter:junit-jupiter-api")
    testRuntime("org.junit.jupiter:junit-jupiter-engine")
    testCompile("io.projectreactor:reactor-test")
}

task<GulpTask>("gulpBuild") {
    dependsOn(YarnInstallTask.NAME)
    inputs.dir("src/main/sass")
    inputs.dir("src/main/ts")
    inputs.dir("src/main/images")
    outputs.dir("build/resources/main/static")
    args = listOf("build")
}

task<GulpTask>("gulpClean") {
    dependsOn(YarnInstallTask.NAME)
    inputs.dir("build/.tmp")
    outputs.dir("build/resources/main/static")
    args = listOf("clean")
}

tasks.getByName("processResources").dependsOn("gulpBuild")
tasks.getByName("clean").dependsOn("gulpClean")