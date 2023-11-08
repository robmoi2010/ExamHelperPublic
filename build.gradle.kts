plugins {
    id("java")
}

group = "com.goglotek"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("com.github.kwhat:jnativehook:2.2.2")
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.15.0")
    implementation("com.pkslow:google-bard:0.3.6")
    implementation("net.sourceforge.tess4j:tess4j:5.8.0")
    implementation("com.hexadevlabs:gpt4all-java-binding:1.1.5")
    implementation("org.slf4j:slf4j-api:2.0.9")
    implementation("net.sf.sociaal:freetts:1.2.2")
    testImplementation("ch.qos.logback:logback-classic:1.4.11")
    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}