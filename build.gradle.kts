
plugins {
    java
    id("io.papermc.paperweight.userdev") version "2.0.0-beta.18"
    id("xyz.jpenilla.run-paper") version "2.3.1"
}

version = "1.0"

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

tasks.withType<Test> {
    systemProperty("file.encoding", "UTF-8")
}

tasks.withType<Javadoc>{
    options.encoding = "UTF-8"
}

tasks{
    assemble {
        dependsOn(reobfJar)
    }
}

repositories {
    mavenCentral()
}

dependencies {
    paperweight.paperDevBundle("1.21.4-R0.1-SNAPSHOT")
    compileOnly(fileTree("libs"){
        include("*.jar")
    })
    compileOnly(files(
    ))
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))

    }
}













