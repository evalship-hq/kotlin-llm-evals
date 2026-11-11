plugins {
    kotlin("jvm") version "2.4.21"
    application
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("com.anthropic:anthropic-java:3.6.0")
    implementation("com.evalship:tinyeval:0.11.0")
}

kotlin {
    jvmToolchain(21)
}

// ./gradlew installDist writes build/install/support/bin/support, the eval worker's start script.
application {
    mainClass = "com.example.support.EvalWorkerKt"
}

// ./gradlew -q installDist tinyeval runs the evals in evals/.
tasks.register<JavaExec>("tinyeval") {
    classpath = sourceSets["main"].runtimeClasspath
    mainClass = "com.evalship.tinyeval.Main"
    standardInput = System.`in`
}
