package com.apolo.androidxcollectionsbenchmarks

import androidx.collection.ArrayMap
import kotlinx.benchmark.*

/**
 * Benchmarks `androidx.collection.ArrayMap<K, V>` against `LinkedHashMap<K, V>`.
 *
 * `ArrayMap` is the map sibling of `ArraySet`: two parallel arrays + binary search on hashes.
 * Optimal for small maps (≤ a few hundred entries). We use small-to-medium sizes that match
 * realistic UI / view-model scenarios where this collection is preferred.
 */
@State(Scope.Benchmark)
@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(BenchmarkTimeUnit.MICROSECONDS)
@Warmup(iterations = 2, time = 2)
@Measurement(iterations = 4, time = 2)
open class ArrayMapBenchmark {

    @Param("16", "256", "4096")
    var size: Int = 0

    private lateinit var keys: Array<String>
    private lateinit var values: Array<String>

    private lateinit var arrayMapFilled: ArrayMap<String, String>
    private lateinit var linkedFilled: LinkedHashMap<String, String>

    @Setup
    fun setup() {
        keys = Array(size) { "k_$it" }
        values = Array(size) { "v_$it" }
        arrayMapFilled = ArrayMap<String, String>(size).also { m ->
            for (i in 0 until size) m[keys[i]] = values[i]
        }
        linkedFilled = LinkedHashMap<String, String>(size).also { m ->
            for (i in 0 until size) m[keys[i]] = values[i]
        }
    }

    @Benchmark
    fun arrayMapInsert(bh: Blackhole) {
        val m = ArrayMap<String, String>(size)
        for (i in 0 until size) m[keys[i]] = values[i]
        bh.consume(m)
    }

    @Benchmark
    fun linkedHashMapInsert(bh: Blackhole) {
        val m = LinkedHashMap<String, String>(size)
        for (i in 0 until size) m[keys[i]] = values[i]
        bh.consume(m)
    }

    @Benchmark
    fun arrayMapLookup(bh: Blackhole) {
        for (i in 0 until size) bh.consume(arrayMapFilled[keys[i]])
    }

    @Benchmark
    fun linkedHashMapLookup(bh: Blackhole) {
        for (i in 0 until size) bh.consume(linkedFilled[keys[i]])
    }

    @Benchmark
    fun arrayMapIterate(bh: Blackhole) {
        for ((k, v) in arrayMapFilled) {
            bh.consume(k)
            bh.consume(v)
        }
    }

    @Benchmark
    fun linkedHashMapIterate(bh: Blackhole) {
        for ((k, v) in linkedFilled) {
            bh.consume(k)
            bh.consume(v)
        }
    }

    @Benchmark
    fun arrayMapRemove(bh: Blackhole) {
        val m = ArrayMap<String, String>(size)
        for (i in 0 until size) m[keys[i]] = values[i]
        for (i in 0 until size) m.remove(keys[i])
        bh.consume(m)
    }

    @Benchmark
    fun linkedHashMapRemove(bh: Blackhole) {
        val m = LinkedHashMap<String, String>(size)
        for (i in 0 until size) m[keys[i]] = values[i]
        for (i in 0 until size) m.remove(keys[i])
        bh.consume(m)
    }
}
