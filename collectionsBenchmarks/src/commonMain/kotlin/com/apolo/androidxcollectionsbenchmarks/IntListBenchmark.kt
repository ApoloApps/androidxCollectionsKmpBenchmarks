package com.apolo.androidxcollectionsbenchmarks

import androidx.collection.MutableIntList
import kotlinx.benchmark.*

/**
 * Benchmarks `androidx.collection.MutableIntList` against the Kotlin std lib `ArrayList<Int>`.
 *
 * `MutableIntList` stores primitive ints in an `IntArray`, avoiding the boxing that
 * `ArrayList<Int>` incurs on every `add`/`get`. The biggest gap should appear in iteration and
 * sum-style workloads, where boxed reads dominate.
 */
@State(Scope.Benchmark)
@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(BenchmarkTimeUnit.MICROSECONDS)
@Warmup(iterations = 3, time = 2)
@Measurement(iterations = 5, time = 2)
open class IntListBenchmark {

    @Param("16", "256", "4096")
    var size: Int = 0

    private lateinit var source: IntArray

    private lateinit var intFilled: MutableIntList
    private lateinit var arrayFilled: ArrayList<Int>

    @Setup
    fun setup() {
        source = IntArray(size) { it }
        intFilled = MutableIntList(size).also { l -> for (i in 0 until size) l.add(source[i]) }
        arrayFilled = ArrayList<Int>(size).also { l -> for (i in 0 until size) l.add(source[i]) }
    }

    @Benchmark
    fun intListAdd(bh: Blackhole) {
        val l = MutableIntList(size)
        for (i in 0 until size) l.add(source[i])
        bh.consume(l)
    }

    @Benchmark
    fun arrayListAdd(bh: Blackhole) {
        val l = ArrayList<Int>(size)
        for (i in 0 until size) l.add(source[i])
        bh.consume(l)
    }

    @Benchmark
    fun intListGet(bh: Blackhole) {
        var sum = 0
        for (i in 0 until size) sum += intFilled[i]
        bh.consume(sum)
    }

    @Benchmark
    fun arrayListGet(bh: Blackhole) {
        var sum = 0
        for (i in 0 until size) sum += arrayFilled[i]
        bh.consume(sum)
    }

    @Benchmark
    fun intListForEach(bh: Blackhole) {
        var sum = 0
        intFilled.forEach { sum += it }
        bh.consume(sum)
    }

    @Benchmark
    fun arrayListForEach(bh: Blackhole) {
        var sum = 0
        arrayFilled.forEach { sum += it }
        bh.consume(sum)
    }

    @Benchmark
    fun intListContains(bh: Blackhole) {
        bh.consume(intFilled.contains(size - 1))
    }

    @Benchmark
    fun arrayListContains(bh: Blackhole) {
        bh.consume(arrayFilled.contains(size - 1))
    }
}
