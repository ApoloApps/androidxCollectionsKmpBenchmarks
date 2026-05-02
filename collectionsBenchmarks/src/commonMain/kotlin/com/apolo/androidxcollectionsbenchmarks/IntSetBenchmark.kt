package com.apolo.androidxcollectionsbenchmarks

import androidx.collection.MutableIntSet
import kotlinx.benchmark.*

/**
 * Benchmarks `androidx.collection.MutableIntSet` against `HashSet<Int>`.
 *
 * Avoids `Int` boxing on every `add` / `contains`, which dominates the cost in the std lib path.
 */
@State(Scope.Benchmark)
@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(BenchmarkTimeUnit.MICROSECONDS)
@Warmup(iterations = 2, time = 2)
@Measurement(iterations = 4, time = 2)
open class IntSetBenchmark {

    @Param("16", "256", "4096")
    var size: Int = 0

    private lateinit var elements: IntArray

    private lateinit var primitiveFilled: MutableIntSet
    private lateinit var hashFilled: HashSet<Int>

    @Setup
    fun setup() {
        elements = IntArray(size) { it * 31 + 7 }
        primitiveFilled = MutableIntSet(size).also { s -> for (e in elements) s.add(e) }
        hashFilled = HashSet<Int>(size).also { s -> for (e in elements) s.add(e) }
    }

    @Benchmark
    fun intSetAdd(bh: Blackhole) {
        val s = MutableIntSet(size)
        for (e in elements) s.add(e)
        bh.consume(s)
    }

    @Benchmark
    fun hashSetAdd(bh: Blackhole) {
        val s = HashSet<Int>(size)
        for (e in elements) s.add(e)
        bh.consume(s)
    }

    @Benchmark
    fun intSetContains(bh: Blackhole) {
        for (e in elements) bh.consume(primitiveFilled.contains(e))
    }

    @Benchmark
    fun hashSetContains(bh: Blackhole) {
        for (e in elements) bh.consume(hashFilled.contains(e))
    }

    @Benchmark
    fun intSetIterate(bh: Blackhole) {
        var sum = 0
        primitiveFilled.forEach { sum += it }
        bh.consume(sum)
    }

    @Benchmark
    fun hashSetIterate(bh: Blackhole) {
        var sum = 0
        for (e in hashFilled) sum += e
        bh.consume(sum)
    }

    @Benchmark
    fun intSetRemove(bh: Blackhole) {
        val s = MutableIntSet(size)
        for (e in elements) s.add(e)
        for (e in elements) s.remove(e)
        bh.consume(s)
    }

    @Benchmark
    fun hashSetRemove(bh: Blackhole) {
        val s = HashSet<Int>(size)
        for (e in elements) s.add(e)
        for (e in elements) s.remove(e)
        bh.consume(s)
    }
}
