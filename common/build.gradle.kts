plugins {
    id("com.possible-triangle.architectury")
}

loom {
    // TODO auto-detect by gradle plugin
    accessWidenerPath = file("src/main/resources/${mod.id.get()}.accesswidener")
}

dependencies {
    modApi(libs.moonlight.lib.common)
    modApi(libs.forge.config.api.common)
}
