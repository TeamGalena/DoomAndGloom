plugins {
    id("com.possible-triangle.fabric")
}

fabric {
    dependOn(project(":common"))
    accessWidener(project(":common"))
}

dependencies {
    modApi(libs.moonlight.lib.fabric)

    if (!env.isCI) {
        modRuntimeOnly(libs.jei.fabric)
    }
}
