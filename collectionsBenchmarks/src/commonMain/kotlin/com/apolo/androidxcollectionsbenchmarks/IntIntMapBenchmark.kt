package com.apolo.androidxcollectionsbenchmarks

import androidx.collection.MutableIntIntMap
import kotlinx.benchmark.*

/**
 * Benchmarks `androidx.collection.MutableIntIntMap` against `HashMap<Int, Int>`.
 *
 * `MutableIntIntMap` stores both keys and values as primitives, eliminating two layers of boxing
 * (key + value) plus per-entry node allocations. This benchmark exercises insert / lookup /
 * iterate / remove using deterministic but spread-out keys to avoid collisions hiding the gap.
 */
@State(Scope.Benchmark)
@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(BenchmarkTimeUnit.MICROSECONDS)
@Warmup(iterations = 2, time = 2)
@Measurement(iterations = 4, time = 2)
open class IntIntMapBenchmark {

    @Param("16", "256", "4096")
    var size: Int = 0

    private lateinit var keys: IntArray
    private lateinit var values: IntArray

    private lateinit var primitiveFilled: MutableIntIntMap
    private lateinit var hashFilled: HashMap<Int, Int>

    @Setup
    fun setup() {
        keys = IntArray(size) { it * 31 + 7 }
        values = IntArray(size) { it }
        primitiveFilled = MutableIntIntMap(size).also { m ->
            for (i in 0 until size) m[keys[i]] = values[i]
        }
        hashFilled = HashMap<Int, Int>(size).also { m ->
            for (i in 0 until size) m[keys[i]] = values[i]
        }
    }

    @Benchmark
    fun intIntMapInsert(bh: Blackhole) {
        val m = MutableIntIntMap(size)
        for (i in 0 until size) m[keys[i]] = values[i]
        bh.consume(m)
    }

    @Benchmark
    fun hashMapInsert(bh: Blackhole) {
        val m = HashMap<Int, Int>(size)
        for (i in 0 until size) m[keys[i]] = values[i]
        bh.consume(m)
    }

    @Benchmark
    fun intIntMapLookup(bh: Blackhole) {
        var sum = 0
        for (i in 0 until size) sum += primitiveFilled[keys[i]]
        bh.consume(sum)
    }

    @Benchmark
    fun hashMapLookup(bh: Blackhole) {
        var sum = 0
        for (i in 0 until size) sum += hashFilled[keys[i]]!!
        bh.consume(sum)
    }

    @Benchmark
    fun intIntMapIterate(bh: Blackhole) {
        primitiveFilled.forEach { k, v ->
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

    @Benchmark
    fun intIntMapRemove(bh: Blackhole) {
        val m = MutableIntIntMap(size)
        for (i in 0 until size) m[keys[i]] = values[i]
        for (i in 0 until size) m.remove(keys[i])
        bh.consume(m)
    }

    @Benchmark
    fun hashMapRemove(bh: Blackhole) {
        val m = HashMap<Int, Int>(size)
        for (i in 0 until size) m[keys[i]] = values[i]
        for (i in 0 until size) m.remove(keys[i])
        bh.consume(m)
    }
}
