plugins {
    id("com.possible-triangle.neoforge")
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
    modInclude(libs.galena.hats.neoforge)

    modApi(libs.moonlight.lib.neoforge)
    accessTransformers(libs.moonlight.lib.neoforge)
    modImplementation(libs.amendments.neoforge)

    modImplementation(pack.forge.modrinth.farmers.delight)
    modImplementation(pack.forge.modrinth.supplementaries)
    modImplementation(libs.oreganized)

    modImplementation(libs.multikulti.core)
    modImplementation(libs.multikulti.datagen)
    modImplementation(libs.dye.depot.neoforge)

    if (!env.isCI) {
        modRuntimeOnly(libs.jei.neoforge)
        modRuntimeOnly(libs.pathfinding.debug.neoforge)
    }
}
