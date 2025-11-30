plugins {
    `java-library`
    `maven-publish`
    kotlin("jvm") version "2.1.0"
    id("org.jetbrains.kotlin.plugin.compose") version "2.1.0"
    id("org.jetbrains.compose") version "1.8.0"
    id("application")
}

repositories {
    google()
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
}

dependencies {
    // Compose Desktop
    implementation(compose.desktop.currentOs)
    implementation(compose.material3)
    implementation(compose.materialIconsExtended)
    implementation("org.jetbrains.androidx.navigation:navigation-compose:2.9.1")

    // Koin DI
    implementation("io.insert-koin:koin-core:4.1.0")
    implementation("io.insert-koin:koin-compose:4.1.0")

    // Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-swing:1.10.2")

    // Spring Boot (versão estável)
    implementation("org.springframework.boot:spring-boot-starter:4.0.0")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa:4.0.0")
    implementation("org.springframework.boot:spring-boot-starter-actuator:4.0.0")
    implementation("org.springframework.security:spring-security-crypto:6.5.7")
    implementation("me.paulschwarz:spring-dotenv:4.0.0")

    // Database
    runtimeOnly("org.hsqldb:hsqldb:2.7.4")

    // Testing
    testImplementation("org.springframework.boot:spring-boot-starter-test:4.0.0")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

group = "org.quijava"
version = "1.0-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_21

application {
    mainClass.set("org.quijava.quijava.compose.MainComposeKt")
}

kotlin {
    jvmToolchain(21)
}

publishing {
    publications.create<MavenPublication>("maven") {
        from(components["java"])
    }
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

tasks.withType<Javadoc> {
    options.encoding = "UTF-8"
}

tasks.withType<Test> {
    useJUnitPlatform()
    jvmArgs("--enable-native-access=ALL-UNNAMED")
}

tasks.withType<JavaExec> {
    jvmArgs("--enable-native-access=ALL-UNNAMED")
}
