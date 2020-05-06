plugins {
    `maven-publish`
    java
    kotlin("jvm") version "1.3.72"
    kotlin("plugin.serialization") version "1.3.72"
    id("com.github.johnrengelman.shadow") version "5.2.0"
}

group = "com.serverless"
version = "dev"

description = """realworld-kotlin"""

tasks {
    java {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    compileJava {
        options.encoding = "UTF-8"
    }
    compileKotlin {
        kotlinOptions.jvmTarget = "11"
    }
    compileTestKotlin {
        kotlinOptions.jvmTarget = "11"
    }
    build {
        finalizedBy(shadowJar)
    }
    shadowJar {
        transform(com.github.jengelman.gradle.plugins.shadow.transformers.Log4j2PluginsCacheFileTransformer::class.java)
    }
}

task<Exec>("deploy") {
    dependsOn(tasks.build)
    commandLine("serverless", "deploy")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))

    implementation(platform("org.jetbrains.kotlinx:kotlinx-coroutines-bom:1.3.5"))
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-jdk8")

    implementation("org.jetbrains.kotlinx:kotlinx-serialization-runtime:0.20.0")

    implementation("software.amazon.awssdk:dynamodb-enhanced:2.13.3")

    implementation("com.amazonaws:aws-lambda-java-core:1.2.0")
    implementation("com.amazonaws:aws-lambda-java-log4j2:1.1.0")
    implementation("com.amazonaws:aws-lambda-java-events:2.2.7")

    implementation(platform("com.fasterxml.jackson:jackson-bom:2.11.0"))
    implementation("com.fasterxml.jackson.core:jackson-core")
    implementation("com.fasterxml.jackson.core:jackson-databind")
    implementation("com.fasterxml.jackson.core:jackson-annotations")

    implementation("org.apache.logging.log4j:log4j-core:2.13.2")
    implementation("org.slf4j:slf4j-simple:2.0.0-alpha1")

    implementation("com.lambdaworks:scrypt:1.4.0")

    implementation("io.jsonwebtoken:jjwt-api:0.11.1")
    runtimeOnly("io.jsonwebtoken:jjwt-impl:0.11.1")
    runtimeOnly("io.jsonwebtoken:jjwt-jackson:0.11.1")

    implementation("com.github.slugify:slugify:2.4")

    testImplementation(kotlin("test"))
    testImplementation(kotlin("test-junit"))
}
