plugins {
    kotlin("jvm") version "2.2.20"
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