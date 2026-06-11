import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.kotlinx.benchmark)
}

// --------------------------------------------------------------------------------------
// Host detection.
//
// `kotlinx-benchmark` (and Kotlin/Native's `HostManager` under the hood) only knows about
// a fixed list of host machines: Linux x86_64, Windows x86_64, macOS x86_64 and macOS arm64.
// On any other host (notably Linux aarch64) every Gradle invocation — including pure
// `jvm` / `js` / `wasmJs` tasks — fails during project configuration with:
//
//   > Failed to notify project evaluation listener.
//   > Unknown host target: linux aarch64
//
// because the benchmark plugin eagerly resolves `HostManager.host` as soon as any native
// benchmark target is registered. To keep JS/Wasm/JVM working on such hosts we must skip
// every native Kotlin target AND every native benchmark `register(...)` call there.
// --------------------------------------------------------------------------------------
val hostOsName: String = System.getProperty("os.name").lowercase()
val hostArch: String = System.getProperty("os.arch").lowercase()

val isLinux: Boolean = hostOsName.startsWith("linux")
val isMac: Boolean = hostOsName.contains("mac") || hostOsName.contains("darwin")
val isWindows: Boolean = hostOsName.startsWith("windows")

val isAmd64: Boolean = hostArch == "amd64" || hostArch == "x86_64"
val isArm64: Boolean = hostArch == "aarch64" || hostArch == "arm64"

// Native targets we declare conditionally. Each one is only enabled when the current host
// is actually supported by Kotlin/Native + kotlinx-benchmark as a host machine.
val enableLinuxX64: Boolean = isLinux && isAmd64
val enableMacosArm64: Boolean = isMac && isArm64
val enableIosSimulatorArm64: Boolean = isMac // any macOS host can build iosSimulatorArm64
val enableMingwX64: Boolean = isWindows && isAmd64

kotlin {
    jvm()

    @OptIn(ExperimentalWasmDsl::class)
    wasmJs {
        nodejs()
    }
    js {
        nodejs()
    }

    if (enableLinuxX64) linuxX64()
    if (enableMacosArm64) macosArm64()
    if (enableIosSimulatorArm64) iosSimulatorArm64()

    sourceSets {
        commonMain.dependencies {
            implementation(libs.kotlinx.benchmark.runtime)
            implementation(libs.androidx.collection)
            implementation(libs.atomicFu)
            implementation(libs.compose.runtime)

        }
    }
}

benchmark {
    targets {
        register("jvm")
        register("wasmJs")
        register("js")
        // Only register native benchmark targets whose Kotlin target is declared above.
        // Otherwise kotlinx-benchmark calls `HostManager.host` and blows up on hosts it
        // doesn't recognise (e.g. Linux aarch64), breaking even pure jvm/js/wasm builds.
        if (enableLinuxX64) register("linuxX64")
        if (enableMacosArm64) register("macosArm64")
        if (enableIosSimulatorArm64) register("iosSimulatorArm64")
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

        register("mutablevectorbenchmarkonly") {
            warmups = 2
            iterations = 4
            iterationTime = 2
            iterationTimeUnit = "s"
            reportFormat = "json"
            include("MutableVectorBenchmark")
        }

        register("pointerinputeventbenchmarkonly") {
            warmups = 2
            iterations = 4
            iterationTime = 2
            iterationTimeUnit = "s"
            reportFormat = "json"
            include("PointerInputEventBenchmark")
        }

        register("flushcoroutinedispatcherbenchmarkonly") {
            warmups = 2
            iterations = 4
            iterationTime = 2
            iterationTimeUnit = "s"
            reportFormat = "json"
            include("FlushCoroutineDispatcherBenchmark")
        }

        register("scatterMapBenchOnly") {
            warmups = 2
            iterations = 4
            iterationTime = 2
            iterationTimeUnit = "s"
            reportFormat = "json"
            include("ScatterMapBenchmark")
        }

        register("scatterSetBenchOnly") {
            warmups = 2
            iterations = 4
            iterationTime = 2
            iterationTimeUnit = "s"
            reportFormat = "json"
            include("ScatterSetBenchmark")
        }
    }
}
