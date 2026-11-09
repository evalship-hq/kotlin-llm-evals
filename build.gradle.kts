plugins {
    kotlin("jvm") version "2.4.21"
    application
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("com.anthropic:anthropic-java:3.6.0")
}

kotlin {
    jvmToolchain(21)
}

// ./gradlew installDist writes build/install/support/bin/support, the eval worker's start script.
application {
    mainClass = "com.example.support.EvalWorkerKt"
}
