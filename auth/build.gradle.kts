import org.gradle.jvm.tasks.Jar

plugins {
    application
    kotlin("jvm")
}

application {
    mainClassName = "io.github.servb.eShop.auth.ApplicationKt"
}

repositories {
    mavenCentral()
    jcenter()
    maven { setUrl("https://kotlin.bintray.com/ktor") }
    maven { setUrl("https://kotlin.bintray.com/kotlin-js-wrappers") }
    maven { setUrl("https://jitpack.io") }
}

val exposedVersion: String by project
val grpcVersion: String by project
val gsonVersion: String by project
val kotestVersion: String by project
val kotlinVersion: String by project
val ktorVersion: String by project
val ktorOpenApiGeneratorVersion: String by project
val logbackVersion: String by project
val postrgesqlVersion: String by project
val jsonPathVersion: String by project
val testcontainersVersion: String by project

dependencies {
    implementation(project(":server-util"))
    implementation(project(":auth-grpc-protocol"))
    implementation(kotlin("stdlib-jdk8", kotlinVersion))
    implementation("io.ktor:ktor-server-netty:$ktorVersion")
    implementation("ch.qos.logback:logback-classic:$logbackVersion")
    implementation("io.ktor:ktor-server-core:$ktorVersion")
    implementation("io.ktor:ktor-jackson:$ktorVersion")
    implementation("com.github.papsign:Ktor-OpenAPI-Generator:$ktorOpenApiGeneratorVersion")
    implementation("org.jetbrains.exposed:exposed-core:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-dao:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-jdbc:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-java-time:$exposedVersion")
    runtimeOnly("org.postgresql:postgresql:$postrgesqlVersion")
    testImplementation(project(":server-util-test"))
    testImplementation("io.ktor:ktor-server-tests:$ktorVersion")
    testImplementation("io.kotest:kotest-runner-junit5-jvm:$kotestVersion")
    testImplementation("io.kotest:kotest-assertions-core-jvm:$kotestVersion")
    testImplementation("io.kotest:kotest-assertions-json:$kotestVersion")
    testImplementation("com.jayway.jsonpath:json-path:$jsonPathVersion")
    testImplementation("org.testcontainers:testcontainers:$testcontainersVersion")
    testImplementation("org.testcontainers:junit-jupiter:$testcontainersVersion")
    testImplementation("org.testcontainers:postgresql:$testcontainersVersion")
}

fun inline(provider: Provider<Configuration>) = provider.get().map { if (it.isDirectory) it else zipTree(it) }

val fatJar = task("fatJar", type = Jar::class) {
    baseName = "${project.name}-fat"
    manifest {
        attributes(
            "Main-Class" to application.mainClassName
        )
    }
    from(inline(configurations.runtimeClasspath))
    with(tasks.jar.get() as CopySpec)
}

tasks {
    "build" {
        dependsOn(fatJar)
    }

    "compileKotlin" {
        dependsOn(project(":auth-grpc-protocol").tasks["generateProto"])
    }
}
