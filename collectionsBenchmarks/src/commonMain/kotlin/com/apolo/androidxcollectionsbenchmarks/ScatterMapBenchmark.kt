package com.apolo.androidxcollectionsbenchmarks

import androidx.collection.mutableScatterMapOf
import kotlinx.benchmark.Benchmark
import kotlinx.benchmark.BenchmarkMode
import kotlinx.benchmark.BenchmarkTimeUnit
import kotlinx.benchmark.Measurement
import kotlinx.benchmark.Mode
import kotlinx.benchmark.OutputTimeUnit
import kotlinx.benchmark.Param
import kotlinx.benchmark.Scope
import kotlinx.benchmark.Setup
import kotlinx.benchmark.State
import kotlinx.benchmark.Warmup

private data class BadHashKey(val value: Int) {
    override fun hashCode(): Int = 0
}

@State(Scope.Benchmark)
@Warmup(iterations = 2, time = 1, timeUnit = BenchmarkTimeUnit.SECONDS)
@Measurement(iterations = 3, time = 1, timeUnit = BenchmarkTimeUnit.SECONDS)
@OutputTimeUnit(BenchmarkTimeUnit.MICROSECONDS)
@BenchmarkMode(Mode.Throughput)
open class ScatterMapBenchmarkTest {
    @Param("10", "100", "1000", "16000")
    var objectCount: Int = 100

    private lateinit var sourceSet: Array<String>
    private lateinit var badHashSourceSet: Array<BadHashKey>

    @Setup
    fun setup() {
        sourceSet = createDataSet(objectCount)
        badHashSourceSet = createBadHashDataSet(objectCount)
    }

    @Benchmark
    fun insert() {
        val map = mutableScatterMapOf<String, String>()
        for (testValue in sourceSet) {
            map[testValue] = testValue
        }
    }

    @Benchmark
    fun insertBadHash() {
        val map = mutableScatterMapOf<BadHashKey, BadHashKey>()
        for (testValue in badHashSourceSet) {
            map[testValue] = testValue
        }
    }

    @Benchmark
    fun remove() {
        val map = mutableScatterMapOf<String, String>()
        for (testValue in sourceSet) {
            map[testValue] = testValue
        }
        for (testValue in sourceSet) {
            map.remove(testValue)
        }
    }

    @Benchmark
    fun read() {
        val map = mutableScatterMapOf<String, String>()
        for (testValue in sourceSet) {
            map[testValue] = testValue
        }
        for (testValue in sourceSet) {
            map[testValue]
        }
    }

    @Benchmark
    fun readBadHash() {
        val map = mutableScatterMapOf<BadHashKey, BadHashKey>()
        for (testValue in badHashSourceSet) {
            map[testValue] = testValue
        }
        for (testValue in badHashSourceSet) {
            map[testValue]
        }
    }

    @Benchmark
    fun forEach() {
        val map = mutableScatterMapOf<String, String>()
        for (testValue in sourceSet) {
            map[testValue] = testValue
        }
        map.forEach { key, value ->
            @Suppress("UNUSED_EXPRESSION")
            key == value
        }
    }

    @Benchmark
    fun compute() {
        val map = mutableScatterMapOf<String, String>()
        for (testValue in sourceSet) {
            map[testValue] = testValue
        }
        for (testValue in sourceSet) {
            map[testValue] = map[testValue] ?: testValue
        }
    }

    @Benchmark
    fun insertRemove() {
        val map = mutableScatterMapOf<BadHashKey, BadHashKey>()
        for (testValue in badHashSourceSet) {
            map[testValue] = testValue
            map.remove(testValue)
        }
    }
}

@State(Scope.Benchmark)
@Warmup(iterations = 2, time = 1, timeUnit = BenchmarkTimeUnit.SECONDS)
@Measurement(iterations = 3, time = 1, timeUnit = BenchmarkTimeUnit.SECONDS)
@OutputTimeUnit(BenchmarkTimeUnit.MICROSECONDS)
@BenchmarkMode(Mode.Throughput)
open class MutableMapBenchmarkTest {
    @Param("10", "100", "1000", "16000")
    var objectCount: Int = 100

    private lateinit var sourceSet: Array<String>
    private lateinit var badHashSourceSet: Array<BadHashKey>

    @Setup
    fun setup() {
        sourceSet = createDataSet(objectCount)
        badHashSourceSet = createBadHashDataSet(objectCount)
    }

    @Benchmark
    fun insert() {
        val map = mutableMapOf<String, String>()
        for (testValue in sourceSet) {
            map[testValue] = testValue
        }
    }

    @Benchmark
    fun insertBadHash() {
        val map = mutableMapOf<BadHashKey, BadHashKey>()
        for (testValue in badHashSourceSet) {
            map[testValue] = testValue
        }
    }

    @Benchmark
    fun remove() {
        val map = mutableMapOf<String, String>()
        for (testValue in sourceSet) {
            map[testValue] = testValue
        }
        for (testValue in sourceSet) {
            map.remove(testValue)
        }
    }

    @Benchmark
    fun read() {
        val map = mutableMapOf<String, String>()
        for (testValue in sourceSet) {
            map[testValue] = testValue
        }
        for (testValue in sourceSet) {
            map[testValue]
        }
    }

    @Benchmark
    fun readBadHash() {
        val map = mutableMapOf<BadHashKey, BadHashKey>()
        for (testValue in badHashSourceSet) {
            map[testValue] = testValue
        }
        for (testValue in badHashSourceSet) {
            map[testValue]
        }
    }

    @Benchmark
    fun forEach() {
        val map = mutableMapOf<String, String>()
        for (testValue in sourceSet) {
            map[testValue] = testValue
        }
        map.forEach { entry ->
            @Suppress("UNUSED_EXPRESSION")
            entry.key == entry.value
        }
    }

    @Benchmark
    fun compute() {
        val map = mutableMapOf<String, String>()
        for (testValue in sourceSet) {
            map[testValue] = testValue
        }
        for (testValue in sourceSet) {
            map[testValue] = map[testValue] ?: testValue
        }
    }

    @Benchmark
    fun insertRemove() {
        val map = mutableMapOf<BadHashKey, BadHashKey>()
        for (testValue in badHashSourceSet) {
            map[testValue] = testValue
            map.remove(testValue)
        }
    }
}

private fun createDataSet(size: Int): Array<String> = Array(size) { index -> index.toString() }

private fun createBadHashDataSet(size: Int): Array<BadHashKey> = Array(size) { BadHashKey(it) }

