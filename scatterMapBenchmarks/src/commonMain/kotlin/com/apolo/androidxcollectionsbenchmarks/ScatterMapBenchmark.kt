package com.apolo.androidxcollectionsbenchmarks

import androidx.collection.MutableScatterMap
import kotlinx.benchmark.*

@State(Scope.Benchmark)
@Warmup(iterations = 2, time = 1, timeUnit = BenchmarkTimeUnit.SECONDS)
@Measurement(iterations = 3, time = 1, timeUnit = BenchmarkTimeUnit.SECONDS)
@OutputTimeUnit(BenchmarkTimeUnit.MICROSECONDS)
@BenchmarkMode(Mode.Throughput)
open class MutableScatterMapBenchmarkTest {
    private val objectCount = 100
    private val keys = Array(objectCount) { it.toString() }
    private val values = Array(objectCount) { "value-$it" }

    private val map = MutableScatterMap<String, String>(objectCount).also { target ->
        repeat(objectCount) { index ->
            target[keys[index]] = values[index]
        }
    }

    @Benchmark
    fun put() {
        val mutableMap = MutableScatterMap<String, String>(objectCount)

        repeat(objectCount) { index ->
            mutableMap[keys[index]] = values[index]
        }
        mutableMap.clear()
    }

    @Benchmark
    fun getHit() {
        repeat(objectCount) { index -> map[keys[index]] }
    }

    @Benchmark
    fun getMiss() {
        repeat(objectCount) { index -> map["missing-$index"] }
    }

    @Benchmark
    fun containsKey() {
        repeat(objectCount) { index -> map.containsKey(keys[index]) }
    }

    @Benchmark
    fun updateExisting() {
        val mutableMap = MutableScatterMap<String, String>(objectCount)

        repeat(objectCount) { index ->
            mutableMap[keys[index]] = values[index]
        }
        repeat(objectCount) { index ->
            mutableMap[keys[index]] = "updated-$index"
        }
        mutableMap.clear()
    }

    @Benchmark
    fun removeExisting() {
        val mutableMap = MutableScatterMap<String, String>(objectCount)

        repeat(objectCount) { index ->
            mutableMap[keys[index]] = values[index]
        }
        repeat(objectCount) { index ->
            mutableMap.remove(keys[index])
        }
    }

    @Benchmark
    fun iterateEntries() {
        @Suppress("ASSIGNED_BUT_NEVER_ACCESSED_VARIABLE") var last: String?
        map.forEach { key, value ->
            last = "$key:$value"
        }
    }
}

@State(Scope.Benchmark)
@Warmup(iterations = 2, time = 1, timeUnit = BenchmarkTimeUnit.SECONDS)
@Measurement(iterations = 3, time = 1, timeUnit = BenchmarkTimeUnit.SECONDS)
@OutputTimeUnit(BenchmarkTimeUnit.MICROSECONDS)
@BenchmarkMode(Mode.Throughput)
open class MutableMapBenchmarkTest {
    private val objectCount = 100
    private val keys = Array(objectCount) { it.toString() }
    private val values = Array(objectCount) { "value-$it" }

    private val map: MutableMap<String, String> = HashMap<String, String>(objectCount).also { target ->
        repeat(objectCount) { index ->
            target[keys[index]] = values[index]
        }
    }

    @Benchmark
    fun put() {
        val mutableMap = HashMap<String, String>(objectCount)

        repeat(objectCount) { index ->
            mutableMap[keys[index]] = values[index]
        }
        mutableMap.clear()
    }

    @Benchmark
    fun getHit() {
        repeat(objectCount) { index -> map[keys[index]] }
    }

    @Benchmark
    fun getMiss() {
        repeat(objectCount) { index -> map["missing-$index"] }
    }

    @Benchmark
    fun containsKey() {
        repeat(objectCount) { index -> map.containsKey(keys[index]) }
    }

    @Benchmark
    fun updateExisting() {
        val mutableMap = HashMap<String, String>(objectCount)

        repeat(objectCount) { index ->
            mutableMap[keys[index]] = values[index]
        }
        repeat(objectCount) { index ->
            mutableMap[keys[index]] = "updated-$index"
        }
        mutableMap.clear()
    }

    @Benchmark
    fun removeExisting() {
        val mutableMap = HashMap<String, String>(objectCount)

        repeat(objectCount) { index ->
            mutableMap[keys[index]] = values[index]
        }
        repeat(objectCount) { index ->
            mutableMap.remove(keys[index])
        }
    }

    @Benchmark
    fun iterateEntries() {
        @Suppress("ASSIGNED_BUT_NEVER_ACCESSED_VARIABLE") var last: String?
        map.forEach { (key, value) ->
            last = "$key:$value"
        }

    }
}