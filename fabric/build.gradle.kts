plugins {
    id("com.possible-triangle.fabric")
}

mod {
    mods.include(libs.forge.config.api.fabric)
}

fabric {
    dependOn(project(":common"))
}

dependencies {
    modApi(pack.fabric.modrinth.moonlight)

    if (!env.isCI) {
        modRuntimeOnly(libs.jei.fabric)
    }
}
