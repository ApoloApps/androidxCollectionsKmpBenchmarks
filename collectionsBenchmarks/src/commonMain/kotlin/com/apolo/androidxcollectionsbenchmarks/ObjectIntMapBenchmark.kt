package com.apolo.androidxcollectionsbenchmarks

import androidx.collection.MutableObjectIntMap
import kotlinx.benchmark.*

/**
 * Benchmarks `androidx.collection.MutableObjectIntMap<K>` against `HashMap<K, Int>`.
 *
 * Use case: counters / index maps where the value is a primitive int. The androidx variant avoids
 * boxing the value on every `put`/`get`. Keys are unique strings so the contains/lookup pattern
 * always hits.
 */
@State(Scope.Benchmark)
@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(BenchmarkTimeUnit.MICROSECONDS)
@Warmup(iterations = 3, time = 2)
@Measurement(iterations = 5, time = 2)
open class ObjectIntMapBenchmark {

    @Param("16", "256", "4096")
    var size: Int = 0

    private lateinit var keys: Array<String>
    private lateinit var values: IntArray

    private lateinit var primitiveFilled: MutableObjectIntMap<String>
    private lateinit var hashFilled: HashMap<String, Int>

    @Setup
    fun setup() {
        keys = Array(size) { "k_$it" }
        values = IntArray(size) { it }
        primitiveFilled = MutableObjectIntMap<String>(size).also { m ->
            for (i in 0 until size) m[keys[i]] = values[i]
        }
        hashFilled = HashMap<String, Int>(size).also { m ->
            for (i in 0 until size) m[keys[i]] = values[i]
        }
    }

    @Benchmark
    fun objectIntMapInsert(bh: Blackhole) {
        val m = MutableObjectIntMap<String>(size)
        for (i in 0 until size) m[keys[i]] = values[i]
        bh.consume(m)
    }

    @Benchmark
    fun hashMapInsert(bh: Blackhole) {
        val m = HashMap<String, Int>(size)
        for (i in 0 until size) m[keys[i]] = values[i]
        bh.consume(m)
    }

    @Benchmark
    fun objectIntMapLookup(bh: Blackhole) {
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
    fun objectIntMapIterate(bh: Blackhole) {
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
    fun objectIntMapRemove(bh: Blackhole) {
        val m = MutableObjectIntMap<String>(size)
        for (i in 0 until size) m[keys[i]] = values[i]
        for (i in 0 until size) m.remove(keys[i])
        bh.consume(m)
    }

    @Benchmark
    fun hashMapRemove(bh: Blackhole) {
        val m = HashMap<String, Int>(size)
        for (i in 0 until size) m[keys[i]] = values[i]
        for (i in 0 until size) m.remove(keys[i])
        bh.consume(m)
    }
}
