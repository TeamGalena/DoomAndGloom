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

    dataGen {
        existing("blueprint")
    }
}

minecraft {
    // move this to be automatically detected by gradle plugin
    // or even better, be generated from the access widener
    accessTransformer(file("src/main/resources/META-INF/accesstransformer.cfg"))
}

configure<MixinExtension> {
    config("${mod.id.get()}.forge.mixins.json")
}

dependencies {
    // Compatibilities
    modApi(libs.moonlight.lib.forge)

    modImplementation(pack.forge.modrinth.supplementaries)
    modImplementation(pack.forge.modrinth.amendments)

    // TODO this is currently only needed for oreganized, but will also be used here soon
    // side-node, this should not be required to run datagen when depending on oreganized, fix this in oreganized
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
