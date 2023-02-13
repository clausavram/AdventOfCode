plugins {
    kotlin("jvm") version "1.8.10"
    java
    id("com.intershop.gradle.javacc") version "4.0.1"
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib"))
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.2")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}

javacc {
    // configuration container for all javacc configurations
    configs {
        register("y2021day18") {
            inputFile = file("src/main/javacc/y2021/day18/SnailFishNumberParser.jj")
            packageName = "y2021.day18"
            lookahead = 2
        }
    }
}