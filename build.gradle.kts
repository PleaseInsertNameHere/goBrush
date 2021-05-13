import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.cadixdev.gradle.licenser.LicenseExtension
import org.ajoberstar.grgit.Grgit

plugins {
    id("java")
    id("java-library")
    id("com.github.johnrengelman.shadow") version "7.0.0"
    id("org.cadixdev.licenser") version "0.6.0"
    id("org.ajoberstar.grgit") version "4.1.0"
}

configure<JavaPluginConvention> {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = sourceCompatibility
}

repositories {
    mavenCentral()
    maven { url = uri("https://hub.spigotmc.org/nexus/content/repositories/snapshots/") }
    maven { url = uri("https://oss.sonatype.org/content/repositories/snapshots") }
    maven { url = uri("https://libraries.minecraft.net/") }
    maven { url = uri("https://mvn.intellectualsites.com/content/repositories/releases/") }
    maven { url = uri("https://mvn.intellectualsites.com/content/repositories/thirdparty/") }
    maven { url = uri("https://papermc.io/repo/repository/maven-public/") }
}

dependencies {
    compileOnlyApi("org.spigotmc:spigot-api:1.16.5-R0.1-SNAPSHOT")
    compileOnly("net.md-5:bungeecord-api:1.16-R0.4")
    compileOnlyApi("com.mojang:authlib:1.5.25")
    compileOnlyApi("com.intellectualsites.fawe:FAWE-Bukkit:1.16-682")
    implementation("net.lingala.zip4j:zip4j:2.7.0")
    implementation("org.incendo.serverlib:ServerLib:2.2.0")
    implementation("org.bstats:bstats-bukkit:2.2.1")
    implementation("org.bstats:bstats-base:2.2.1")
    implementation("io.papermc:paperlib:1.0.6")
}

var rootVersion by extra("3.7.2")
var buildNumber by extra("")
ext {
    val git: Grgit = Grgit.open {
        dir = File("$rootDir/.git")
    }
    val commit: String? = git.head().abbreviatedId
    buildNumber = if (project.hasProperty("buildnumber")) {
        project.properties["buildnumber"] as String
    } else {
        commit.toString()
    }
}

version = String.format("%s-%s", rootVersion, buildNumber)

configure<LicenseExtension> {
    header.set(resources.text.fromFile(file("HEADER.txt")))
    include("**/*.java")
    exclude("**/XMaterial.java")
    newLine.set(false)
}

tasks.named<Copy>("processResources") {
    filesMatching("plugin.yml") {
        expand("version" to project.version)
    }
}

tasks.named<ShadowJar>("shadowJar") {
    archiveClassifier.set(null as String?)
    dependencies {
        relocate("net.lingala.zip4j", "com.arcaniax.zip4j") {
            include(dependency("net.lingala.zip4j:zip4j"))
        }
        relocate("org.incendo.serverlib", "com.arcaniax.gobrush.serverlib") {
            include(dependency("org.incendo.serverlib:ServerLib:2.2.0"))
        }
        relocate("org.bstats", "com.arcaniax.gobrush.metrics") {
            include(dependency("org.bstats:bstats-base"))
            include(dependency("org.bstats:bstats-bukkit"))
        }
        relocate("io.papermc.lib", "com.arcaniax.gobrush.paperlib") {
            include(dependency("io.papermc:paperlib:1.0.6"))
        }
    }
    minimize()
}

tasks.named("build").configure {
    dependsOn("shadowJar")
}

