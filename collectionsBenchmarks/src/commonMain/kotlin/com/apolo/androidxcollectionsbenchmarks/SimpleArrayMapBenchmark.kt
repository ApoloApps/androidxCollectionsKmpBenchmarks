package com.apolo.androidxcollectionsbenchmarks

import androidx.collection.SimpleArrayMap
import kotlinx.benchmark.*

/**
 * Benchmarks `androidx.collection.SimpleArrayMap<K, V>` against `HashMap<K, V>`.
 *
 * `SimpleArrayMap` is the lighter-weight cousin of `ArrayMap` (no `MutableMap` interface, no
 * iterator allocations). It's a typical fit for tiny maps inside hot UI paths. We test small
 * sizes only because performance falls off above ~256 entries by design.
 */
@State(Scope.Benchmark)
@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(BenchmarkTimeUnit.MICROSECONDS)
@Warmup(iterations = 3, time = 2)
@Measurement(iterations = 5, time = 2)
open class SimpleArrayMapBenchmark {

    // SimpleArrayMap is intentionally tuned for small collections (linear-scan keys + values
    // arrays). We still benchmark at the common 16/256/4096 axis so all collection benches
    // align on `size`; the worst-case lookup at size=4096 highlights why callers should
    // prefer hashed maps for large keysets.
    @Param("16", "256", "4096")
    var size: Int = 0

    private lateinit var keys: Array<String>
    private lateinit var values: Array<String>

    private lateinit var simpleFilled: SimpleArrayMap<String, String>
    private lateinit var hashFilled: HashMap<String, String>

    @Setup
    fun setup() {
        keys = Array(size) { "k_$it" }
        values = Array(size) { "v_$it" }
        simpleFilled = SimpleArrayMap<String, String>(size).also { m ->
            for (i in 0 until size) m.put(keys[i], values[i])
        }
        hashFilled = HashMap<String, String>(size).also { m ->
            for (i in 0 until size) m[keys[i]] = values[i]
        }
    }

    @Benchmark
    fun simpleArrayMapInsert(bh: Blackhole) {
        val m = SimpleArrayMap<String, String>(size)
        for (i in 0 until size) m.put(keys[i], values[i])
        bh.consume(m)
    }

    @Benchmark
    fun hashMapInsert(bh: Blackhole) {
        val m = HashMap<String, String>(size)
        for (i in 0 until size) m[keys[i]] = values[i]
        bh.consume(m)
    }

    @Benchmark
    fun simpleArrayMapLookup(bh: Blackhole) {
        for (i in 0 until size) bh.consume(simpleFilled.get(keys[i]))
    }

    @Benchmark
    fun hashMapLookup(bh: Blackhole) {
        for (i in 0 until size) bh.consume(hashFilled[keys[i]])
    }

    @Benchmark
    fun simpleArrayMapIterateByIndex(bh: Blackhole) {
        for (i in 0 until simpleFilled.size()) {
            bh.consume(simpleFilled.keyAt(i))
            bh.consume(simpleFilled.valueAt(i))
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
    fun simpleArrayMapRemove(bh: Blackhole) {
        val m = SimpleArrayMap<String, String>(size)
        for (i in 0 until size) m.put(keys[i], values[i])
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
