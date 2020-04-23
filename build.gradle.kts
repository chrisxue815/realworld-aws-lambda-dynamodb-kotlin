plugins {
    `maven-publish`
    java
    kotlin("jvm") version "1.3.71"
    id("io.spring.dependency-management") version "1.0.8.RELEASE"
    id("com.github.johnrengelman.shadow") version "5.2.0"
}

group = "com.serverless"
version = "dev"

description = """hello"""


java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

repositories {
    mavenCentral()
}

// If requiring AWS JDK, uncomment the dependencyManagement to use the bill of materials
//   https://aws.amazon.com/blogs/developer/managing-dependencies-with-aws-sdk-for-java-bill-of-materials-module-bom/
//dependencyManagement {
//    imports {
//        mavenBom("com.amazonaws:aws-java-sdk-bom:1.11.688")
//    }
//}

dependencies {
    implementation(kotlin("stdlib-jdk8"))

    implementation(group = "com.amazonaws", name = "aws-lambda-java-core", version = "1.2.0")
    implementation(group = "com.amazonaws", name = "aws-lambda-java-log4j2", version = "1.1.0")
    implementation(group = "com.amazonaws", name = "aws-lambda-java-events", version = "2.2.7")

    implementation(group = "com.fasterxml.jackson.core", name = "jackson-core", version = "2.10.1")
    implementation(group = "com.fasterxml.jackson.core", name = "jackson-databind", version = "2.10.1")
    implementation(group = "com.fasterxml.jackson.core", name = "jackson-annotations", version = "2.10.1")
}

task<Exec>("deploy") {
    dependsOn("shadowJar")
    commandLine("serverless", "deploy")
}

tasks.named("build") {
    finalizedBy("shadowJar")
}
