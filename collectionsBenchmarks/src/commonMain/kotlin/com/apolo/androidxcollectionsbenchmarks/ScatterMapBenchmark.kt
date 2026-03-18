package com.apolo.androidxcollectionsbenchmarks

import androidx.collection.mutableScatterMapOf
import kotlinx.benchmark.Benchmark
import kotlinx.benchmark.BenchmarkMode
import kotlinx.benchmark.BenchmarkTimeUnit
import kotlinx.benchmark.Measurement
import kotlinx.benchmark.Mode
import kotlinx.benchmark.OutputTimeUnit
import kotlinx.benchmark.Scope
import kotlinx.benchmark.State
import kotlinx.benchmark.Warmup

@State(Scope.Benchmark)
@Warmup(iterations = 2, time = 1, timeUnit = BenchmarkTimeUnit.SECONDS)
@Measurement(iterations = 3, time = 1, timeUnit = BenchmarkTimeUnit.SECONDS)
@OutputTimeUnit(BenchmarkTimeUnit.MICROSECONDS)
@BenchmarkMode(Mode.Throughput)
open class ScatterMapBenchmarkTest {
    val objectCount = 100

    private val map = mutableScatterMapOf<String, String>().also { target ->
        repeat(objectCount) { index ->
            target[index.toString()] = "value$index"
        }
    }

    private val keys = Array(objectCount) { it.toString() }
    private val values = Array(objectCount) { "value$it" }
    private val sourceMap: Map<String, String> = mutableMapOf<String, String>().also { source ->
        repeat(objectCount) { index ->
            source["source$index"] = "sourceValue$index"
        }
    }

    @Benchmark
    fun forEach() {
        @Suppress("ASSIGNED_BUT_NEVER_ACCESSED_VARIABLE") var last: String
        map.forEach { (_, value) -> last = value }
    }

    @Benchmark
    fun put() {
        val mutableMap = mutableScatterMapOf<String, String>()

        repeat(objectCount) { index ->
            mutableMap[keys[index]] = values[index]
        }
        mutableMap.clear()
    }

    @Benchmark
    fun containsKey() {
        repeat(objectCount) { index ->
            map.containsKey(keys[index])
        }
    }

    @Benchmark
    fun get() {
        repeat(objectCount) { index ->
            map[keys[index]]
        }
    }

    @Benchmark
    fun putAll() {
        val mutableMap = mutableScatterMapOf<String, String>()

        mutableMap.putAll(sourceMap)
        mutableMap.clear()
    }

    @Benchmark
    fun remove() {
        val mutableMap = mutableScatterMapOf<String, String>()

        repeat(objectCount) { index ->
            mutableMap[keys[index]] = values[index]
        }
        repeat(objectCount) { index ->
            mutableMap.remove(keys[index])
        }
    }
}

@State(Scope.Benchmark)
@Warmup(iterations = 2, time = 1, timeUnit = BenchmarkTimeUnit.SECONDS)
@Measurement(iterations = 3, time = 1, timeUnit = BenchmarkTimeUnit.SECONDS)
@OutputTimeUnit(BenchmarkTimeUnit.MICROSECONDS)
@BenchmarkMode(Mode.Throughput)
open class MutableMapBenchmarkTest {
    val objectCount = 100

    private val map: MutableMap<String, String> = mutableMapOf<String, String>().also { target ->
        repeat(objectCount) { index ->
            target[index.toString()] = "value$index"
        }
    }

    private val keys = Array(objectCount) { it.toString() }
    private val values = Array(objectCount) { "value$it" }
    private val sourceMap: Map<String, String> = mutableMapOf<String, String>().also { source ->
        repeat(objectCount) { index ->
            source["source$index"] = "sourceValue$index"
        }
    }

    @Benchmark
    fun forEach() {
        @Suppress("ASSIGNED_BUT_NEVER_ACCESSED_VARIABLE") var last: String
        map.forEach { _, value -> last = value }
    }

    @Benchmark
    fun put() {
        val mutableMap = mutableMapOf<String, String>()

        repeat(objectCount) { index ->
            mutableMap[keys[index]] = values[index]
        }
        mutableMap.clear()
    }

    @Benchmark
    fun containsKey() {
        repeat(objectCount) { index ->
            map.containsKey(keys[index])
        }
    }

    @Benchmark
    fun get() {
        repeat(objectCount) { index ->
            map[keys[index]]
        }
    }

    @Benchmark
    fun putAll() {
        val mutableMap = mutableMapOf<String, String>()

        mutableMap.putAll(sourceMap)
        mutableMap.clear()
    }

    @Benchmark
    fun remove() {
        val mutableMap = mutableMapOf<String, String>()

        repeat(objectCount) { index ->
            mutableMap[keys[index]] = values[index]
        }
        repeat(objectCount) { index ->
            mutableMap.remove(keys[index])
        }
    }
}

