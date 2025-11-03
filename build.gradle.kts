plugins {
    id("com.possible-triangle.core")
    id("com.possible-triangle.architectury") apply false
    id("com.possible-triangle.forge") apply false
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
        repositories {
            maven {
                url = uri("https://maven.ladysnake.org/releases")
                content {
                    includeGroupAndSubgroups("dev.onyxstudios")
                }
            }
        }
    }

    upload {
        maven {
            nexus()
        }

        forEach {
            includeKotlinDependency = false

            dependencies {
                required("moonlight")
                optional("oreganized")
            }
        }
    }
}

enableSpotless()
enableSonarQube()
