plugins {
    id("com.possible-triangle.common")
}

common {
    accessWidener()
}

dependencies {
    modImplementation(libs.moonlight.lib.common)
    modImplementation(libs.moonlight.lib.common)
    modImplementation(libs.amendments.common)

    accessTransformers(libs.multikulti.core.common)
}
