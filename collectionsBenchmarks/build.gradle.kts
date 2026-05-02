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
    //linuxArm64()
if(System.getProperty("os.arch")== "amd64"){
    linuxX64()
}

    sourceSets {
        commonMain.dependencies {
            implementation(libs.kotlinx.benchmark.runtime)
            implementation(libs.androidx.collection)
            implementation(libs.atomicFu)

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
    // Default config: deliberately short so CI runs finish in reasonable time.
    // Override per-class @Warmup/@Measurement still apply when set on the class.
    configurations {
        named("main") {
            warmups = 2
            iterations = 4
            iterationTime = 2
            iterationTimeUnit = "s"
            reportFormat = "json"
        }

        register("depthsortedsetbenchmarkonly") {
            warmups = 2
            iterations = 4
            iterationTime = 2
            iterationTimeUnit = "s"
            reportFormat = "json"
            include("DepthSortedSetBenchmark")
        }
    }
}
