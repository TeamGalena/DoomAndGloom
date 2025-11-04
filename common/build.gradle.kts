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

    // TODO replace with common module
    modImplementation(pack.forge.modrinth.amendments)
}
