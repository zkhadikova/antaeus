plugins {
    kotlin("jvm")
}

kotlinProject()

dependencies {
	implementation("khttp:khttp:0.1.0")
	implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.9.8")
    implementation(project(":pleo-antaeus-data"))
    compile(project(":pleo-antaeus-models"))
}