repositories {
    mavenCentral()
    maven(url = "https://maven.google.com")

}


// apply false to declare version and skip version in subprojects
plugins {
    kotlin("jvm") version Kotlin.version
    id("org.springframework.boot") version Versions.springBoot
    id("io.spring.dependency-management") version Versions.springBootDependency
    id("org.jetbrains.kotlin.plugin.spring") version Kotlin.version
}


buildscript {
    repositories {
        mavenCentral()
        maven(url = "https://maven.google.com")

    }
    dependencies {
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:${Kotlin.version}")
        classpath("org.jetbrains.kotlin:kotlin-allopen:${Kotlin.version}")
        classpath("org.springframework.boot:spring-boot-gradle-plugin:${Versions.springBoot}")
    }
}


repositories {
    mavenCentral()
//        maven(url = "https://kotlin.bintray.com/kotlinx")
    maven(url = "https://maven.google.com")

}

group = "me.underlow.tgarr"
version = "0.0.1-SNAPSHOT"




java {
    sourceCompatibility = JavaVersion.VERSION_19
    targetCompatibility = JavaVersion.VERSION_19
}

dependencyManagement {
    imports {
        mavenBom( "org.springframework.cloud:spring-cloud-dependencies:${Versions.springCloudDependency}")
    }
}
dependencies {

    implementation(kotlin("stdlib-jdk8"))
    implementation(kotlin("reflect"))

    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.13.3")

    implementation("org.springframework.boot:spring-boot-starter-web") {
        exclude("spring-boot-starter-tomcat")
    }
    implementation("org.springframework.boot:spring-boot-starter-jetty")

    implementation("org.springframework.boot:spring-boot-devtools")

    implementation("org.springdoc:springdoc-openapi-ui:${Versions.swagger}") {
        exclude(group = "org.springframework")
        exclude(group = "org.springframework.boot")
    }
    implementation("org.springdoc:springdoc-openapi-kotlin:${Versions.swagger}") {
        exclude(group = "org.springframework")
        exclude(group = "org.springframework.boot")
    }

    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:${Versions.kotlinCoroutine}")

    implementation("io.ktor:ktor-client-core:${Versions.ktorVersion}")
    implementation("io.ktor:ktor-client-cio:${Versions.ktorVersion}")
    implementation("io.ktor:ktor-serialization-jackson:${Versions.ktorVersion}")
    implementation("io.ktor:ktor-client-content-negotiation:${Versions.ktorVersion}")

    implementation("com.github.pengrad:java-telegram-bot-api:6.3.0")

    implementation("org.jsoup:jsoup:1.15.4")

    implementation("ch.qos.logback:logback-classic")
    implementation("io.github.microutils:kotlin-logging:${Versions.kotlinLogging}")

    implementation("org.springframework.boot:spring-boot-starter-actuator")

    implementation("com.squareup.okhttp3:okhttp:4.9.3")

    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.14.2")

    testImplementation("org.springframework.security:spring-security-test")
    testImplementation("org.springframework.boot:spring-boot-starter-test") {
        exclude(group = "org.junit.vintage", module = "junit-vintage-engine")
        // exclude(module = "mockito-core")
    }
    testImplementation("org.junit.jupiter:junit-jupiter-api:${Versions.jupiter}")
    testImplementation("org.junit.jupiter:junit-jupiter-params:${Versions.jupiter}")
    testImplementation("org.junit.jupiter:junit-jupiter-engine:${Versions.jupiter}")

    testImplementation("io.kotest:kotest-assertions-core:${Versions.kotest}")

    testImplementation("org.testcontainers:postgresql:1.17.6")
    testImplementation("org.testcontainers:junit-jupiter:1.17.6")
    // https://github.com/testcontainers/testcontainers-java/issues/3834
    // these lines required for Mac M1 at least for now
    testImplementation("net.java.dev.jna:jna-platform:5.8.0")
    testImplementation("net.java.dev.jna:jna:5.8.0")
}

springBoot {
    buildInfo()
}

tasks.withType<Test> {
    useJUnitPlatform()
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = JavaVersion.VERSION_11.toString()
        allWarningsAsErrors = false
    }
}

tasks.getByName<Jar>("jar") {
    enabled = false
}
