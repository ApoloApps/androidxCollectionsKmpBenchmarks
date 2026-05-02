package com.apolo.androidxcollectionsbenchmarks

import androidx.collection.MutableScatterMap
import kotlinx.benchmark.*

/**
 * Benchmarks comparing `androidx.collection.MutableScatterMap<K, V>` with the Kotlin standard
 * library `HashMap<K, V>`.
 *
 * `MutableScatterMap` is an open-addressing hash map that avoids the per-entry node allocation
 * required by `HashMap`. To make the comparison realistic we measure the four most common map
 * operations (insert, lookup, iterate, remove) using a deterministic keyset that is shared
 * across both implementations.
 */
@State(Scope.Benchmark)
@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(BenchmarkTimeUnit.MICROSECONDS)
@Warmup(iterations = 2, time = 2)
@Measurement(iterations = 4, time = 2)
open class ScatterMapBenchmark {

    @Param("16", "256", "4096")
    var size: Int = 0

    private lateinit var keys: Array<String>
    private lateinit var values: Array<String>

    // Pre-populated maps used by lookup / iterate / remove benchmarks.
    private lateinit var scatterFilled: MutableScatterMap<String, String>
    private lateinit var hashFilled: HashMap<String, String>

    @Setup
    fun setup() {
        keys = Array(size) { "key_$it" }
        values = Array(size) { "value_$it" }
        scatterFilled = MutableScatterMap<String, String>(size).also { m ->
            for (i in 0 until size) m[keys[i]] = values[i]
        }
        hashFilled = HashMap<String, String>(size).also { m ->
            for (i in 0 until size) m[keys[i]] = values[i]
        }
    }

    // ---- INSERT ----

    @Benchmark
    fun scatterMapInsert(bh: Blackhole) {
        val m = MutableScatterMap<String, String>(size)
        for (i in 0 until size) m[keys[i]] = values[i]
        bh.consume(m)
    }

    @Benchmark
    fun hashMapInsert(bh: Blackhole) {
        val m = HashMap<String, String>(size)
        for (i in 0 until size) m[keys[i]] = values[i]
        bh.consume(m)
    }

    // ---- LOOKUP (hit) ----

    @Benchmark
    fun scatterMapLookup(bh: Blackhole) {
        for (i in 0 until size) bh.consume(scatterFilled[keys[i]])
    }

    @Benchmark
    fun hashMapLookup(bh: Blackhole) {
        for (i in 0 until size) bh.consume(hashFilled[keys[i]])
    }

    // ---- ITERATE ----

    @Benchmark
    fun scatterMapIterate(bh: Blackhole) {
        scatterFilled.forEach { k, v ->
            bh.consume(k)
            bh.consume(v)
        }
    }

    @Benchmark
    fun hashMapIterate(bh: Blackhole) {
        for ((k, v) in hashFilled) {
            bh.consume(k)
            bh.consume(v)
        }
    }

    // ---- REMOVE ----

    @Benchmark
    fun scatterMapRemove(bh: Blackhole) {
        val m = MutableScatterMap<String, String>(size)
        for (i in 0 until size) m[keys[i]] = values[i]
        for (i in 0 until size) m.remove(keys[i])
        bh.consume(m)
    }

    @Benchmark
    fun hashMapRemove(bh: Blackhole) {
        val m = HashMap<String, String>(size)
        for (i in 0 until size) m[keys[i]] = values[i]
        for (i in 0 until size) m.remove(keys[i])
        bh.consume(m)
    }
}
