plugins {
    id("com.possible-triangle.forge")
}

forge {
    dependOn(project(":common"))

    enableMixins()
    accessWidener(project(":common"))

    dataGen {
        existing("blueprint")
        splitSourceSet()
    }
}

mixin {
    config("${mod.id.get()}.forge.mixins.json")
}

dependencies {
    modInclude(libs.galena.hats.forge)
    modInclude(libs.multikulti.datagen.fix)

    modImplementation(libs.moonlight.lib.forge) {
        isTransitive = false
    }

    modImplementation(pack.forge.modrinth.farmers.delight)
    modImplementation(pack.forge.modrinth.supplementaries)
    modImplementation(pack.forge.modrinth.amendments)
    modImplementation(libs.oreganized)

    modImplementation(libs.multikulti.core)
    modImplementation(libs.multikulti.datagen)
    modImplementation(libs.dye.depot.forge)

    if (!env.isCI) {
        // this should be included transient with oreganized on 1.21 neoforge (just not possible with how forge works)
        modRuntimeOnly(libs.blueprint)
        modRuntimeOnly(libs.jei.forge)
        modRuntimeOnly(libs.pathfinding.debug.forge)
    }
}
