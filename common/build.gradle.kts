plugins {
    id("com.possible-triangle.architectury")
}

loom {
    // TODO auto-detect by gradle plugin
    accessWidenerPath = file("src/main/resources/${mod.id.get()}.accesswidener")
}

dependencies {
    // TODO switch out with actual common module
    modApi(pack.forge.modrinth.moonlight)
    modApi(libs.forge.config.api.common)
}
