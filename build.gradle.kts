plugins {
    id("com.possible-triangle.core")
    id("com.possible-triangle.common") apply false
    id("com.possible-triangle.neoforge") apply false
    id("com.possible-triangle.fabric") apply false
}

subprojects {
    apply(plugin = "com.possible-triangle.core")

    repositories {
        maven {
            url = uri("https://maven.blamejared.com/")
            content {
                includeGroup("mezz.jei")
            }
        }
        nexus {
            content {
                includeGroup("com.possible-triangle")
                includeGroup("dev.galena")
                includeGroup("net.mehvahdjukaar")
                includeGroup("com.ninni.dye_depot")
            }
        }
        maven {
            url = uri("https://maven.teamabnormals.com/")
            content {
                includeGroup("com.teamabnormals")
            }
        }
    }

    upload {
        maven {
            nexus()
        }

        forEach {
            dependencies {
                optional("oreganized")
            }
        }

        modrinth.dependencies.required("moonlight")
        curseforge.dependencies.required("selene")
    }
}

enableSpotless()
enableSonarQube()
