package com.apolo.androidxcollectionsbenchmarks

import androidx.collection.MutableIntObjectMap
import kotlinx.benchmark.*

/**
 * Benchmarks `androidx.collection.MutableIntObjectMap<V>` against `HashMap<Int, V>`.
 *
 * Avoiding key boxing is the main win here: `HashMap<Int, V>` boxes every primitive key on `put`
 * and `get`, while `MutableIntObjectMap` keeps keys in a primitive `IntArray`.
 */
@State(Scope.Benchmark)
@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(BenchmarkTimeUnit.MICROSECONDS)
@Warmup(iterations = 2, time = 2)
@Measurement(iterations = 4, time = 2)
open class IntObjectMapBenchmark {

    @Param("16", "256", "4096")
    var size: Int = 0

    private lateinit var keys: IntArray
    private lateinit var values: Array<String>

    private lateinit var primitiveFilled: MutableIntObjectMap<String>
    private lateinit var hashFilled: HashMap<Int, String>

    @Setup
    fun setup() {
        keys = IntArray(size) { it * 31 + 7 }
        values = Array(size) { "v_$it" }
        primitiveFilled = MutableIntObjectMap<String>(size).also { m ->
            for (i in 0 until size) m[keys[i]] = values[i]
        }
        hashFilled = HashMap<Int, String>(size).also { m ->
            for (i in 0 until size) m[keys[i]] = values[i]
        }
    }

    @Benchmark
    fun intObjectMapInsert(bh: Blackhole) {
        val m = MutableIntObjectMap<String>(size)
        for (i in 0 until size) m[keys[i]] = values[i]
        bh.consume(m)
    }

    @Benchmark
    fun hashMapInsert(bh: Blackhole) {
        val m = HashMap<Int, String>(size)
        for (i in 0 until size) m[keys[i]] = values[i]
        bh.consume(m)
    }

    @Benchmark
    fun intObjectMapLookup(bh: Blackhole) {
        for (i in 0 until size) bh.consume(primitiveFilled[keys[i]])
    }

    @Benchmark
    fun hashMapLookup(bh: Blackhole) {
        for (i in 0 until size) bh.consume(hashFilled[keys[i]])
    }

    @Benchmark
    fun intObjectMapIterate(bh: Blackhole) {
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
    fun intObjectMapRemove(bh: Blackhole) {
        val m = MutableIntObjectMap<String>(size)
        for (i in 0 until size) m[keys[i]] = values[i]
        for (i in 0 until size) m.remove(keys[i])
        bh.consume(m)
    }

    @Benchmark
    fun hashMapRemove(bh: Blackhole) {
        val m = HashMap<Int, String>(size)
        for (i in 0 until size) m[keys[i]] = values[i]
        for (i in 0 until size) m.remove(keys[i])
        bh.consume(m)
    }
}
