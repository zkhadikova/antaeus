plugins {
    kotlin("jvm")
}

kotlinProject()

dataLibs()

dependencies {
    implementation(project(":pleo-antaeus-models"))
    compile("joda-time:joda-time:2.10")
}
