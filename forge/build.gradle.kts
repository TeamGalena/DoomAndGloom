import org.spongepowered.asm.gradle.plugins.MixinExtension

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
    accessWidener(project(":common"))

    dataGen {
        existing("blueprint")
        splitSourceSet()
    }
}

tasks.compileJava {
    dependsOn(tasks.getByName("transformAccessWidener"))
}

configure<MixinExtension> {
    config("${mod.id.get()}.forge.mixins.json")
}

dependencies {
    modImplementation(libs.moonlight.lib.forge) {
        isTransitive = false
    }

    modImplementation(pack.forge.modrinth.farmers.delight)
    modImplementation(pack.forge.modrinth.supplementaries)
    modImplementation(pack.forge.modrinth.amendments)

    modImplementation(libs.multikulti.core)
    modImplementation(libs.multikulti.datagen)

    if (!env.isCI) {
        modRuntimeOnly(libs.oreganized)
        // this should be included transient with oreganized on 1.21 neoforge (just not possible with how forge works)
        modRuntimeOnly(libs.blueprint)
        modRuntimeOnly(libs.dye.depot)
        modRuntimeOnly(libs.jei.forge)
    }
}
