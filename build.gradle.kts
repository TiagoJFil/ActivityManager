plugins {
    kotlin("jvm") version "1.6.10"
    kotlin("plugin.serialization") version "1.6.10"
    id("org.jlleitschuh.gradle.ktlint") version "10.2.1"
    id("application")
}
ext {
    set("projectMainClass", "pt.isel.ls.Sports_serverKt")
}

application {
    mainClass.set(project.ext.get("projectMainClass") as String)
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(group = "org.postgresql", name = "postgresql", version = "42.+")
    implementation(group = "org.http4k", name = "http4k-core", version = "4.20.2.0")
    implementation(group = "org.http4k", name = "http4k-server-jetty", version = "4.20.2.0")
    implementation(group = "org.jetbrains.kotlinx", name = "kotlinx-serialization-json", version = "1.3.2")
    implementation(group = "org.jetbrains.kotlinx", name = "kotlinx-datetime", version = "0.3.2")
    implementation(group = "org.slf4j", name = "slf4j-api", version = "2.0.0-alpha5")
    runtimeOnly(group = "org.slf4j", name = "slf4j-simple", version = "2.0.0-alpha5")
    testImplementation(kotlin("test"))
}

tasks.named<Jar>("jar") {
    dependsOn("copyRuntimeDependencies")
    manifest {
        attributes["Main-Class"] = project.ext.get("projectMainClass") as String
        attributes["Class-Path"] = configurations.runtimeClasspath.get().joinToString(" ") { it.name }
    }
}

tasks.register<Copy>("copyRuntimeDependencies") {
    from(configurations.runtimeClasspath)
    into("$buildDir/libs")
}

tasks.test {
    environment(
        mapOf(
            "APP_ENV_TYPE" to "TEST"
        )
    )
}

tasks.register("docker-build") {
    group = "docker"
    // depends on gradle build
    dependsOn("build")
    dependsOn("docker-clean")

    // docker build
    val outFile = File("gradle_docker_build_output.txt")
    val dockerBuildCommand = "docker build -t sports-server:latest $projectDir"
    executeLast(dockerBuildCommand, outFile)
}

tasks.register("docker-clean") {
    group = "docker"
    val dockerRmCommand = "docker rmi -f sports-server"
    val outFile = File("gradle_docker_clean_output.txt")

    executeLast(dockerRmCommand, outFile)
}

tasks.register("docker-run") {
    group = "docker"
    dependsOn("docker-build")
    dependsOn("docker-clean")

    val outFile = File("gradle_docker_run_output.txt")
    val dockerRunCommand = "docker run -p 9000:9000 -d sports-server:latest"
    executeLast(dockerRunCommand, outFile)
}

fun Task.executeLast(command: String, outputFile: File) {
    doLast {
        println("Executing: $command")
        ProcessBuilder(command.split(" "))
            .inheritIO() // inherit sys envs
            .redirectOutput(outputFile)
            .redirectError(outputFile)
            .start()
            .waitFor()
    }
}
