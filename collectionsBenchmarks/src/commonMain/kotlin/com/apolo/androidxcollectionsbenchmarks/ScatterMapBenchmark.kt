package com.apolo.androidxcollectionsbenchmarks

import androidx.collection.mutableObjectIntMapOf
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
open class ObjectIntMapBenchmarkTest {
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
        val map = mutableObjectIntMapOf<String>()
        for (index in sourceSet.indices) {
            map[sourceSet[index]] = index
        }
    }

    @Benchmark
    fun insertBadHash() {
        val map = mutableObjectIntMapOf<BadHashKey>()
        for (testValue in badHashSourceSet) {
            map[testValue] = testValue.value
        }
    }

    @Benchmark
    fun remove() {
        val map = mutableObjectIntMapOf<String>()
        for (index in sourceSet.indices) {
            map[sourceSet[index]] = index
        }
        for (testValue in sourceSet) {
            map.remove(testValue)
        }
    }

    @Benchmark
    fun read() {
        val map = mutableObjectIntMapOf<String>()
        for (index in sourceSet.indices) {
            map[sourceSet[index]] = index
        }
        for (testValue in sourceSet) {
            map[testValue]
        }
    }

    @Benchmark
    fun readBadHash() {
        val map = mutableObjectIntMapOf<BadHashKey>()
        for (testValue in badHashSourceSet) {
            map[testValue] = testValue.value
        }
        for (testValue in badHashSourceSet) {
            map[testValue]
        }
    }

    @Benchmark
    fun forEach() {
        val map = mutableObjectIntMapOf<String>()
        for (index in sourceSet.indices) {
            map[sourceSet[index]] = index
        }
        map.forEach { key, value ->
            @Suppress("UNUSED_EXPRESSION")
            key.isNotEmpty() || value >= 0
        }
    }

    @Benchmark
    fun compute() {
        val map = mutableObjectIntMapOf<String>()
        for (index in sourceSet.indices) {
            map[sourceSet[index]] = index
        }
        for (testValue in sourceSet) {
            if (map.containsKey(testValue)) {
                map[testValue] = map[testValue] + 1
            } else {
                map[testValue] = 1
            }
        }
    }

    @Benchmark
    fun insertRemove() {
        val map = mutableObjectIntMapOf<BadHashKey>()
        for (testValue in badHashSourceSet) {
            map[testValue] = testValue.value
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
        val map = mutableMapOf<String, Int>()
        for (index in sourceSet.indices) {
            map[sourceSet[index]] = index
        }
    }

    @Benchmark
    fun insertBadHash() {
        val map = mutableMapOf<BadHashKey, Int>()
        for (testValue in badHashSourceSet) {
            map[testValue] = testValue.value
        }
    }

    @Benchmark
    fun remove() {
        val map = mutableMapOf<String, Int>()
        for (index in sourceSet.indices) {
            map[sourceSet[index]] = index
        }
        for (testValue in sourceSet) {
            map.remove(testValue)
        }
    }

    @Benchmark
    fun read() {
        val map = mutableMapOf<String, Int>()
        for (index in sourceSet.indices) {
            map[sourceSet[index]] = index
        }
        for (testValue in sourceSet) {
            map[testValue]
        }
    }

    @Benchmark
    fun readBadHash() {
        val map = mutableMapOf<BadHashKey, Int>()
        for (testValue in badHashSourceSet) {
            map[testValue] = testValue.value
        }
        for (testValue in badHashSourceSet) {
            map[testValue]
        }
    }

    @Benchmark
    fun forEach() {
        val map = mutableMapOf<String, Int>()
        for (index in sourceSet.indices) {
            map[sourceSet[index]] = index
        }
        map.forEach { entry ->
            @Suppress("UNUSED_EXPRESSION")
            entry.key.isNotEmpty() || entry.value >= 0
        }
    }

    @Benchmark
    fun compute() {
        val map = mutableMapOf<String, Int>()
        for (index in sourceSet.indices) {
            map[sourceSet[index]] = index
        }
        for (testValue in sourceSet) {
            if (map.containsKey(testValue)) {
                map[testValue] = (map[testValue] ?: 0) + 1
            } else {
                map[testValue] = 1
            }
        }
    }

    @Benchmark
    fun insertRemove() {
        val map = mutableMapOf<BadHashKey, Int>()
        for (testValue in badHashSourceSet) {
            map[testValue] = testValue.value
            map.remove(testValue)
        }
    }
}

private fun createDataSet(size: Int): Array<String> = Array(size) { index -> index.toString() }

private fun createBadHashDataSet(size: Int): Array<BadHashKey> = Array(size) { BadHashKey(it) }

