import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.kotlinx.benchmark)
}

kotlin {
    jvm()

    js {
        nodeJs()
    }

    @OptIn(ExperimentalWasmDsl::class)
    wasmJs {
        nodeJs()
    }
    linuxArm64()

    sourceSets {
        commonMain.dependencies {
            implementation(libs.kotlinx.benchmark.runtime)

        }
    }
}

benchmark {
    targets {
        register("jvm")
        register("wasmJs")
        register("js")
        register("linuxArm64")
    }
}
