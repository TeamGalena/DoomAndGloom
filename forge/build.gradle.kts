plugins {
    id("com.possible-triangle.neoforge")
}

mod {
    mods.include(libs.galena.hats.neoforge)
}

neoforge {
    dependOn(project(":common"))

    accessWidener(project(":common"))

    dataGen {
        existing("blueprint")
        splitSourceSet()
    }
}

dependencies {
    modImplementation(libs.moonlight.lib.neoforge) {
        isTransitive = false
    }

    modImplementation(pack.forge.modrinth.farmers.delight)
    modImplementation(pack.forge.modrinth.supplementaries)
    modImplementation(pack.forge.modrinth.amendments)
    modImplementation(libs.oreganized)

    modImplementation(libs.multikulti.core)
    modImplementation(libs.multikulti.datagen)
    modImplementation(libs.dye.depot.neoforge)

    if (!env.isCI) {
        modRuntimeOnly(libs.jei.neoforge)
        modRuntimeOnly(libs.pathfinding.debug.neoforge)
    }
}
