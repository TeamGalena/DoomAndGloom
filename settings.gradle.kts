pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenLocal()
    }
}

plugins {
    id("com.possible-triangle.helper") version ("1.3")
    id("com.possible-triangle.packwiz") version ("1.3.+")
}

include("common")
loader("forge")
loader("fabric")

fun loader(name: String) {
    include(name)
    packwiz {
        packs.create(name) {
            from = file("$name/pack")
        }
    }
}
