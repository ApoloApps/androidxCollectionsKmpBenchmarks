import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.kotlinx.benchmark)
}

kotlin {
    jvm()

    @OptIn(ExperimentalWasmDsl::class)
    wasmJs {
        nodejs()
    }
    js{
        nodejs()
    }
    linuxArm64()
    linuxX64()

    sourceSets {
        commonMain.dependencies {
            implementation(libs.kotlinx.benchmark.runtime)
            implementation(libs.androidx.collection)

        }
    }
}

benchmark {
    targets {
        register("jvm")
        register("wasmJs")
        register("js")
        register("linuxX64")
    }
}
