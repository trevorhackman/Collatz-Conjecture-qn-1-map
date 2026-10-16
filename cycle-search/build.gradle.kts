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

dependencies {
    testImplementation(kotlin("test"))
}

application {
    mainClass.set("qn1.CycleSearchKt")
}

tasks.withType<Test>().configureEach {
    useJUnitPlatform()
}

tasks.withType<JavaExec>().configureEach {
    // Lengths 6–9 keep one hash table of residues per height, several heights at once.
    maxHeapSize = "24g"
}
