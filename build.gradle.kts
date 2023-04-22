plugins {
 kotlin("jvm") version "1.8.20"
 application
}

repositories {
 mavenCentral()
}

dependencies {
 testImplementation(kotlin("test"))
 testImplementation("io.kotest:kotest-assertions-core:5.6.1")
}

tasks.test {
 useJUnitPlatform()
}
