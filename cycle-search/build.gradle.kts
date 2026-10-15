plugins {
    kotlin("jvm") version "2.3.0"
    application
}

repositories {
    mavenCentral()
}

kotlin {
    jvmToolchain(25)
}

application {
    mainClass.set("qn1.CycleSearchKt")
}
