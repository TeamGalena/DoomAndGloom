plugins {
    id("com.possible-triangle.forge")
    alias(libs.plugins.parchment)
}

mod {
    mods.include(libs.galena.hats)
    mods.include(libs.multikulti.datagen.fix)
}

forge {
    dependOn(project(":common"))

    mappingChannel = "parchment"
    mappingVersion = "2023.09.03-1.20.1"

    enableMixins()

    dataGen {
        existing("blueprint")
    }
}

dependencies {
    // Compatibilities
    modImplementation(pack.forge.modrinth.moonlight)
    modImplementation(pack.forge.modrinth.supplementaries)
    modImplementation(pack.forge.modrinth.amendments)

    if (!env.isCI) {
        modRuntimeOnly(libs.oreganized)
        modRuntimeOnly(libs.dye.depot)
        modRuntimeOnly(libs.jei.forge)
    }
}
