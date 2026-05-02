package com.apolo.androidxcollectionsbenchmarks

import androidx.collection.MutableScatterSet
import kotlinx.benchmark.*

/**
 * Benchmarks `androidx.collection.MutableScatterSet<T>` against the Kotlin std lib `HashSet<T>`.
 *
 * Open-addressing sets typically win on contains/iterate due to better cache locality and lack of
 * Entry allocations. We exercise add, contains, iterate and remove to surface where each shape
 * differs.
 */
@State(Scope.Benchmark)
@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(BenchmarkTimeUnit.MICROSECONDS)
@Warmup(iterations = 3, time = 2)
@Measurement(iterations = 5, time = 2)
open class ScatterSetBenchmark {

    @Param("16", "256", "4096")
    var size: Int = 0

    private lateinit var elements: Array<String>

    private lateinit var scatterFilled: MutableScatterSet<String>
    private lateinit var hashFilled: HashSet<String>

    @Setup
    fun setup() {
        elements = Array(size) { "elem_$it" }
        scatterFilled = MutableScatterSet<String>(size).also { s -> for (e in elements) s.add(e) }
        hashFilled = HashSet<String>(size).also { s -> for (e in elements) s.add(e) }
    }

    @Benchmark
    fun scatterSetAdd(bh: Blackhole) {
        val s = MutableScatterSet<String>(size)
        for (e in elements) s.add(e)
        bh.consume(s)
    }

    @Benchmark
    fun hashSetAdd(bh: Blackhole) {
        val s = HashSet<String>(size)
        for (e in elements) s.add(e)
        bh.consume(s)
    }

    @Benchmark
    fun scatterSetContains(bh: Blackhole) {
        for (e in elements) bh.consume(scatterFilled.contains(e))
    }

    @Benchmark
    fun hashSetContains(bh: Blackhole) {
        for (e in elements) bh.consume(hashFilled.contains(e))
    }

    @Benchmark
    fun scatterSetIterate(bh: Blackhole) {
        scatterFilled.forEach { bh.consume(it) }
    }

    @Benchmark
    fun hashSetIterate(bh: Blackhole) {
        for (e in hashFilled) bh.consume(e)
    }

    @Benchmark
    fun scatterSetRemove(bh: Blackhole) {
        val s = MutableScatterSet<String>(size)
        for (e in elements) s.add(e)
        for (e in elements) s.remove(e)
        bh.consume(s)
    }

    @Benchmark
    fun hashSetRemove(bh: Blackhole) {
        val s = HashSet<String>(size)
        for (e in elements) s.add(e)
        for (e in elements) s.remove(e)
        bh.consume(s)
    }
}
