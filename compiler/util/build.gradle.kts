plugins {
    kotlin("jvm")
    id("jps-compatible")
}

jvmTarget = "1.6"

dependencies {
    api(kotlinStdlib())
    api(project(":compiler:compiler.version"))

    compileOnly(intellijCoreDep()) { includeJars("intellij-core") }
    compileOnly(intellijDep()) { includeIntellijCoreJarDependencies(project) }
    compileOnly(jpsStandalone()) { includeJars("jps-model") }
}

sourceSets {
    "main" {
        projectDefault()
        resources.srcDir(File(rootDir, "resources"))
    }
    "test" {}
}
