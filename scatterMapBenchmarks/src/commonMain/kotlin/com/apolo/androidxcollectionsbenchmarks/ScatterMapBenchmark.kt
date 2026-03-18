package com.apolo.androidxcollectionsbenchmarks

import androidx.collection.MutableIntSet
import kotlinx.benchmark.*

@State(Scope.Benchmark)
@Warmup(iterations = 2, time = 1, timeUnit = BenchmarkTimeUnit.SECONDS)
@Measurement(iterations = 3, time = 1, timeUnit = BenchmarkTimeUnit.SECONDS)
@OutputTimeUnit(BenchmarkTimeUnit.MICROSECONDS)
@BenchmarkMode(Mode.Throughput)
open class MutableIntSetBenchmarkTest {
    private val objectCount = 100
    private val values = IntArray(objectCount) { it }

    private val set = MutableIntSet(objectCount).also { target ->
        repeat(objectCount) { index ->
            target.add(values[index])
        }
    }

    @Benchmark
    fun add() {
        val mutableSet = MutableIntSet(objectCount)

        repeat(objectCount) { index ->
            mutableSet.add(values[index])
        }
        mutableSet.clear()
    }

    @Benchmark
    fun containsHit() {
        repeat(objectCount) { index -> set.contains(values[index]) }
    }

    @Benchmark
    fun containsMiss() {
        repeat(objectCount) { index -> set.contains(objectCount + index) }
    }

    @Benchmark
    fun addExisting() {
        val mutableSet = MutableIntSet(objectCount)

        repeat(objectCount) { index ->
            mutableSet.add(values[index])
        }
        repeat(objectCount) { index ->
            mutableSet.add(values[index])
        }
        mutableSet.clear()
    }

    @Benchmark
    fun removeExisting() {
        val mutableSet = MutableIntSet(objectCount)

        repeat(objectCount) { index ->
            mutableSet.add(values[index])
        }
        repeat(objectCount) { index ->
            mutableSet.remove(values[index])
        }
    }

    @Benchmark
    fun iterateElements() {
        @Suppress("ASSIGNED_BUT_NEVER_ACCESSED_VARIABLE") var last = -1
        set.forEach { value ->
            last = value
        }
    }
}

@State(Scope.Benchmark)
@Warmup(iterations = 2, time = 1, timeUnit = BenchmarkTimeUnit.SECONDS)
@Measurement(iterations = 3, time = 1, timeUnit = BenchmarkTimeUnit.SECONDS)
@OutputTimeUnit(BenchmarkTimeUnit.MICROSECONDS)
@BenchmarkMode(Mode.Throughput)
open class MutableSetOfIntBenchmarkTest {
    private val objectCount = 100
    private val values = IntArray(objectCount) { it }

    private val set: MutableSet<Int> = mutableSetOf<Int>().also { target ->
        repeat(objectCount) { index ->
            target.add(values[index])
        }
    }

    @Benchmark
    fun add() {
        val mutableSet = mutableSetOf<Int>()

        repeat(objectCount) { index ->
            mutableSet.add(values[index])
        }
        mutableSet.clear()
    }

    @Benchmark
    fun containsHit() {
        repeat(objectCount) { index -> set.contains(values[index]) }
    }

    @Benchmark
    fun containsMiss() {
        repeat(objectCount) { index -> set.contains(objectCount + index) }
    }

    @Benchmark
    fun addExisting() {
        val mutableSet = mutableSetOf<Int>()

        repeat(objectCount) { index ->
            mutableSet.add(values[index])
        }
        repeat(objectCount) { index ->
            mutableSet.add(values[index])
        }
        mutableSet.clear()
    }

    @Benchmark
    fun removeExisting() {
        val mutableSet = mutableSetOf<Int>()

        repeat(objectCount) { index ->
            mutableSet.add(values[index])
        }
        repeat(objectCount) { index ->
            mutableSet.remove(values[index])
        }
    }

    @Benchmark
    fun iterateElements() {
        @Suppress("ASSIGNED_BUT_NEVER_ACCESSED_VARIABLE") var last = -1
        set.forEach { value ->
            last = value
        }

    }
}