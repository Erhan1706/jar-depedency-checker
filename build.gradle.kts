plugins {
    id("java")
}

group = "org.example"
version = "1.0"

subprojects {
    apply(plugin = "java")

    tasks.register("copy_io") {
        val compileClasspath =
            project.configurations.matching { it.name == "compileClasspath" }
        compileClasspath.all {
            for (dep in map { file: File -> file.absoluteFile }) {
                project.copy {
                    from(dep)
                    into("${rootProject.projectDir}/build/libs")
                }
            }
        }
    }
    tasks.withType<Jar>() {
        destinationDirectory.set(file("$rootDir/build/libs"))
        dependsOn("copy_io")
    }
}


repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":ModuleA"))
    implementation(project(":ModuleB"))
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks.test {
    useJUnitPlatform()
}