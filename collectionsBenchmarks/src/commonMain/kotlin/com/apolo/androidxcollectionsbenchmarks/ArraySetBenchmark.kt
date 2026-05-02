package com.apolo.androidxcollectionsbenchmarks

import androidx.collection.ArraySet
import kotlinx.benchmark.*

/**
 * Benchmarks `androidx.collection.ArraySet<T>` against `LinkedHashSet<T>` (the closest std lib
 * counterpart that preserves insertion order).
 *
 * `ArraySet` is backed by two parallel arrays and uses binary search on hashes; it allocates far
 * less than `LinkedHashSet` and excels for **small** sets, but does linear shifting on remove and
 * binary search on contains. Sizes are chosen accordingly.
 */
@State(Scope.Benchmark)
@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(BenchmarkTimeUnit.MICROSECONDS)
@Warmup(iterations = 2, time = 2)
@Measurement(iterations = 4, time = 2)
open class ArraySetBenchmark {

    // ArraySet is intentionally tuned for small collections, but we keep a common scale
    // (16/256/4096) so all benchmarks share the same `size` axis. Larger sizes still
    // exercise the linear-search behavior of ArraySet against the hashed HashSet.
    @Param("16", "256", "4096")
    var size: Int = 0

    private lateinit var elements: Array<String>

    private lateinit var arraySetFilled: ArraySet<String>
    private lateinit var linkedFilled: LinkedHashSet<String>

    @Setup
    fun setup() {
        elements = Array(size) { "elem_$it" }
        arraySetFilled = ArraySet<String>(size).also { s -> for (e in elements) s.add(e) }
        linkedFilled = LinkedHashSet<String>(size).also { s -> for (e in elements) s.add(e) }
    }

    @Benchmark
    fun arraySetAdd(bh: Blackhole) {
        val s = ArraySet<String>(size)
        for (e in elements) s.add(e)
        bh.consume(s)
    }

    @Benchmark
    fun linkedHashSetAdd(bh: Blackhole) {
        val s = LinkedHashSet<String>(size)
        for (e in elements) s.add(e)
        bh.consume(s)
    }

    @Benchmark
    fun arraySetContains(bh: Blackhole) {
        for (e in elements) bh.consume(arraySetFilled.contains(e))
    }

    @Benchmark
    fun linkedHashSetContains(bh: Blackhole) {
        for (e in elements) bh.consume(linkedFilled.contains(e))
    }

    @Benchmark
    fun arraySetIterate(bh: Blackhole) {
        for (e in arraySetFilled) bh.consume(e)
    }

    @Benchmark
    fun linkedHashSetIterate(bh: Blackhole) {
        for (e in linkedFilled) bh.consume(e)
    }

    @Benchmark
    fun arraySetRemove(bh: Blackhole) {
        val s = ArraySet<String>(size)
        for (e in elements) s.add(e)
        for (e in elements) s.remove(e)
        bh.consume(s)
    }

    @Benchmark
    fun linkedHashSetRemove(bh: Blackhole) {
        val s = LinkedHashSet<String>(size)
        for (e in elements) s.add(e)
        for (e in elements) s.remove(e)
        bh.consume(s)
    }
}
