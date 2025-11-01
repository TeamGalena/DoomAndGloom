plugins {
    id("com.possible-triangle.architectury")
}

dependencies {
    // TODO switch out with actual common module
    modApi(pack.forge.modrinth.moonlight)
    modApi(libs.forge.config.api.common)
}
