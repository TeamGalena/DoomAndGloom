plugins {
    id("com.possible-triangle.fabric")
}

fabric {
    dependOn(project(":common"))
    accessWidener(project(":common"))
}

repositories {
    maven {
        url = uri("https://mvn.devos.one/releases/")
        content {
            includeGroup("io.github.fabricators_of_create.Porting-Lib")
        }
    }
    nexus("jitpack") {
        content {
            includeGroup("com.github.Chocohead")
        }
    }
    maven {
        url = uri("https://maven.jamieswhiteshirt.com/libs-release")
        content {
            includeGroup("com.jamieswhiteshirt")
        }
    }
    maven {
        url = uri("https://maven.greenhouse.lgbt/releases/")
        content {
            includeGroup("vectorwing")
        }
    }
}

dependencies {
    modInclude(libs.galena.hats.fabric)

    modApi(libs.moonlight.lib.fabric)
    modImplementation(libs.amendments.fabric)

    modImplementation(libs.farmers.delight.fabric) {
        exclude(group = "net.fabricmc")
    }

    modImplementation(pack.fabric.modrinth.supplementaries)

    modImplementation(libs.dye.depot.fabric)

    if (!env.isCI) {
        modRuntimeOnly(libs.jei.fabric)
        modRuntimeOnly(libs.pathfinding.debug.fabric)
    }
}
