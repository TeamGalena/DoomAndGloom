plugins {
    id("com.possible-triangle.fabric")
}

mod {
    mods.include(libs.forge.config.api.fabric)
}

fabric {
    dependOn(project(":common"))
}

loom {
    // TODO auto-detect by gradle plugin
    accessWidenerPath = project(":common").file("src/main/resources/${mod.id.get()}.accesswidener")
}

dependencies {
    modApi(libs.moonlight.lib.fabric)

    if (!env.isCI) {
        modRuntimeOnly(libs.jei.fabric)
    }
}
