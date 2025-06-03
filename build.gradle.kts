import net.darkhax.curseforgegradle.Constants
import net.darkhax.curseforgegradle.TaskPublishCurseForge
import java.time.LocalDateTime

val mod_id: String by extra
val mod_name: String by extra
val mod_author: String by extra
val mod_version: String by extra
val release_type: String by extra

val repository: String by extra
val maven_group: String by extra
val modrinth_project: String by extra
val curseforge_project: String by extra

val minecraft_version: String by extra
val forge_version: String by extra
val blueprint_version: String by extra
val mixin_version: String by extra
val mixin_extras_version: String by extra
val supplementaries_version: String by extra
val amendments_version: String by extra
val moonlight_lib_version: String by extra
val oreganized_version: String by extra
val dye_depot_version: String by extra
val jei_version: String by extra
val galena_hats_version: String by extra

plugins {
    java
    `maven-publish`
    id("net.minecraftforge.gradle") version "[6.0,6.2)"
    id("org.spongepowered.mixin") version "0.7-SNAPSHOT"
    id("org.parchmentmc.librarian.forgegradle") version "1.+"
    id("com.diffplug.spotless") version "7.0.4"
    id("org.sonarqube") version "6.2.0.5505"
    id("com.modrinth.minotaur") version "2.+"
    id("net.darkhax.curseforgegradle") version "1.1.15"
}

base {
    archivesName.set("$mod_name $minecraft_version")
}

mixin {
    add(sourceSets.main.get(), "${mod_id}.refmap.json")
    config("${mod_id}.mixins.json")
}

java {
    toolchain.languageVersion = JavaLanguageVersion.of(17)
    withSourcesJar()
}

minecraft {
    mappings("parchment", "2023.09.03-1.20.1")

    accessTransformer(file("src/main/resources/META-INF/accesstransformer.cfg"))

    runs {
        create("client") {
            taskName("Client")
        }

        create("server") {
            taskName("Server")
        }

        create("data") {
            args(
                "--mod",
                mod_id,
                "--all",
                "--output",
                project.file("src/generated/resources/"),
                "--existing",
                project.file("src/main/resources/"),
                "--existing-mod",
                "blueprint"
            )
            taskName("Data")
        }

        forEach {
            it.workingDirectory(project.file("run"))
            it.args("-mixin.config=${mod_id}.mixins.json")
            it.mods.create(mod_id) {
                source(sourceSets.main.get())
            }
        }
    }
}

sourceSets.main.get().resources {
    srcDir("src/generated/resources")
}

repositories {
    maven {
        // location of the maven that hosts Blueprint
        url = uri("https://maven.jaackson.me")
    }
    maven {
        // location of the maven that hosts JEI files
        url = uri("https://dvs1.progwml6.com/files/maven/")
    }
    maven {
        // location of a maven mirror for JEI files, as a fallback
        url = uri("https://modmaven.dev")
    }
    maven {
        url = uri("https://www.cursemaven.com")
        content {
            includeGroup("curse.maven")
        }
    }
    maven {
        url = uri("https://api.modrinth.com/maven")
        content {
            includeGroup("maven.modrinth")
        }
    }
    maven {
        url = uri("https://registry.somethingcatchy.net/repository/maven-releases/")
        content {
            includeGroup("dev.galena")
        }
    }
}

dependencies {
    minecraft("net.minecraftforge:forge:${minecraft_version}-${forge_version}")
    implementation(fg.deobf("com.teamabnormals:blueprint:${minecraft_version}-${blueprint_version}"))
    annotationProcessor("org.spongepowered:mixin:${mixin_version}:processor")

    compileOnly(annotationProcessor("io.github.llamalad7:mixinextras-common:${mixin_extras_version}")!!)
    implementation(jarJar("io.github.llamalad7:mixinextras-forge:${mixin_extras_version}")) {
        jarJar.ranged(this, "[${mixin_extras_version},)")
    }

    val hatsVersion = "${minecraft_version}-${galena_hats_version}"
    implementation(fg.deobf(jarJar("dev.galena:hats-forge:${hatsVersion}") {
        version {
            strictly("[${hatsVersion},)")
            prefer(hatsVersion)
        }
    }))

    // Compatibilities
    implementation(fg.deobf("maven.modrinth:supplementaries:${supplementaries_version}"))
    implementation(fg.deobf("maven.modrinth:amendments:${amendments_version}"))
    implementation(fg.deobf("maven.modrinth:moonlight:${moonlight_lib_version}"))

    // For dev testing
    runtimeOnly(fg.deobf("maven.modrinth:oreganized:${oreganized_version}"))
    runtimeOnly(fg.deobf("maven.modrinth:dye-depot:${dye_depot_version}"))

    /// Utilities for the development environment
    //runtimeOnly fg.deobf("curse.maven:jade-324717:${jade_version}")
    // compile against the JEI API but do not include it at runtime
    compileOnly(fg.deobf("mezz.jei:jei-${minecraft_version}-common-api:${jei_version}"))
    compileOnly(fg.deobf("mezz.jei:jei-${minecraft_version}-forge-api:${jei_version}"))
    // at runtime, use the full JEI jar for Forge
    runtimeOnly(fg.deobf("mezz.jei:jei-${minecraft_version}-forge:${jei_version}"))
}


tasks.jar {
    archiveClassifier.set("raw")
    finalizedBy("reobfJar")

    val now = LocalDateTime.now().toString()

    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    from(rootProject.file("LICENSE")) {
        rename { "${it}_${mod_name}" }
    }

    manifest {
        attributes(
            mapOf(
                "Specification-Title" to mod_name,
                "Specification-Vendor" to mod_author,
                "Specification-Version" to mod_version,
                "Implementation-Title" to mod_name,
                "Implementation-Version" to mod_version,
                "Implementation-Vendor" to mod_author,
                "Implementation-Timestamp" to now,
            )
        )
    }
}

tasks.withType<ProcessResources> {
    // this will ensure that this task is redone when the versions change.
    inputs.property("version", mod_version)

    filesMatching(
        listOf(
            "META-INF/mods.toml",
            "META-INF/neoforge.mods.toml",
            "pack.mcmeta",
            "fabric.mod.json",
            "${mod_id}*.mixins.json",
        )
    ) {
        expand(
            mapOf(
                "mod_version" to mod_version,
                "mod_name" to mod_name,
                "mod_id" to mod_id,
                "mod_author" to mod_author,
                "repository" to repository,
            )
        )
    }
}


jarJar.enable()
tasks.jarJar {
    archiveClassifier.set("")
}

tasks.jar {
    archiveClassifier.set("raw")
    finalizedBy("reobfJar")
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = maven_group
            artifactId = mod_id
            version = mod_version

            from(components["java"])

            pom.withXml {
                val node = asNode()
                val list = node.get("dependencies") as groovy.util.NodeList
                list.forEach { node.remove(it as groovy.util.Node) }
            }
        }
    }
    repositories {
        mavenLocal()

        val nexusToken = System.getenv("NEXUS_TOKEN")
        val nexusUser = System.getenv("NEXUS_USER")
        if (nexusToken != null && nexusUser != null) {
            maven {
                url = uri("https://registry.somethingcatchy.net/repository/maven-releases/")
                credentials {
                    username = nexusUser
                    password = nexusToken
                }
            }
        }
    }
}

spotless {
    java {
        importOrder()
        removeUnusedImports()
    }

    kotlinGradle {
        ktlint()
        suppressLintsFor { shortCode = "standard:property-naming" }
    }

    json {
        target("src/main/**/*.json")
        gson().indentWithSpaces(2)
    }
}

sonar {
    properties {
        property("sonar.projectKey", mod_id)
        property("sonar.gradle.skipCompile", "true")
        property("sonar.links.scm", "https://github.com/${repository}")
    }
}

val outputJar = tasks.jarJar.get().archiveFile.get()
val changelogMarkdown = System.getenv("CHANGELOG")

modrinth {
    token = System.getenv("MODRINTH_TOKEN")
    projectId = modrinth_project
    versionNumber = mod_version
    versionType = release_type
    versionName = "$mod_name $mod_version"
    uploadFile = outputJar
    gameVersions = listOf(minecraft_version)
    loaders = listOf("forge")
    changelog = changelogMarkdown

    dependencies {
        required.project("blueprint")
        optional.project("oreganized")
    }
}

tasks.register<TaskPublishCurseForge>("curseforge") {
    group = "publishing"

    apiToken = System.getenv("CURSEFORGE_TOKEN")
    upload(curseforge_project, outputJar).apply {
        changelogType = Constants.CHANGELOG_MARKDOWN
        changelog = changelogMarkdown
        releaseType = release_type
        addModLoader("forge")
        addGameVersion(minecraft_version)
        displayName = "$mod_name $mod_version"

        addRelation("blueprint", Constants.RELATION_REQUIRED)
        addRelation("oreganized", Constants.RELATION_OPTIONAL)
    }
}