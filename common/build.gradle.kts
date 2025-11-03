plugins {
    id("com.possible-triangle.architectury")
}

common {
    accessWidener()
}

dependencies {
    modApi(libs.moonlight.lib.common) {
        isTransitive = false
    }

    modImplementation(pack.forge.modrinth.amendments)
}
