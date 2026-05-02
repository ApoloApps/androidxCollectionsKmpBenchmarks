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
            warmups = 3
            iterations = 5
            iterationTime = 2
            iterationTimeUnit = "s"
            reportFormat = "json"
        }
        // Tiny configuration for verifying the harness end-to-end (used by `*BenchmarkSmoke`).
        register("smoke") {
            warmups = 1
            iterations = 1
            iterationTime = 1
            iterationTimeUnit = "s"
            reportFormat = "json"
            // Run a single, cheap benchmark from each family so the smoke run finishes fast.
            include("IntListBenchmark\\.intListGet")
            include("ScatterMapBenchmark\\.scatterMapLookup")
            include("ArraySetBenchmark\\.arraySetAdd")
        }
        // Reduced-budget configuration suitable for CI / local full runs that still want
        // statistically reasonable numbers without spending tens of minutes per platform.
        // We pin `size=256` so each benchmark runs once instead of once per `@Param` value;
        // 256 is large enough to expose the cost gap between androidx primitive collections
        // and the boxing-based stdlib counterparts, while staying small enough for fast runs.
        register("ci") {
            warmups = 1
            iterations = 2
            iterationTime = 1
            iterationTimeUnit = "s"
            reportFormat = "json"
            param("size", "256")
        }
        register("depthsortedsetbenchmarkonly") {
            warmups = 3
            iterations = 5
            iterationTime = 2
            iterationTimeUnit = "s"
            reportFormat = "json"
            include("DepthSortedSetBenchmark")
        }
    }
}
