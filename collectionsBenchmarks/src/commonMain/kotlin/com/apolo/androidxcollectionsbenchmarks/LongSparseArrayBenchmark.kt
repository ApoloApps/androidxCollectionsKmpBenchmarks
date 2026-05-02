package com.apolo.androidxcollectionsbenchmarks

import androidx.collection.LongSparseArray
import kotlinx.benchmark.*

/**
 * Benchmarks `androidx.collection.LongSparseArray<V>` against `HashMap<Long, V>`.
 *
 * `LongSparseArray` is a binary-search backed structure on a sorted `LongArray` of keys. It tends
 * to be faster than `HashMap<Long, V>` for **small** collections (no key boxing, no per-entry
 * allocations) but loses on inserts at large sizes (O(n) shifting). The benchmark covers small
 * (16) up to medium (1024) sizes which is the realistic operating range.
 */
@State(Scope.Benchmark)
@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(BenchmarkTimeUnit.MICROSECONDS)
@Warmup(iterations = 3, time = 2)
@Measurement(iterations = 5, time = 2)
open class LongSparseArrayBenchmark {

    @Param("16", "256", "4096")
    var size: Int = 0

    private lateinit var keys: LongArray
    private lateinit var values: Array<String>

    private lateinit var sparseFilled: LongSparseArray<String>
    private lateinit var hashFilled: HashMap<Long, String>

    @Setup
    fun setup() {
        // Append-friendly key layout (already sorted) — best case for LongSparseArray.
        keys = LongArray(size) { (it * 7L) + 1L }
        values = Array(size) { "v_$it" }
        sparseFilled = LongSparseArray<String>(size).also { s ->
            for (i in 0 until size) s.put(keys[i], values[i])
        }
        hashFilled = HashMap<Long, String>(size).also { m ->
            for (i in 0 until size) m[keys[i]] = values[i]
        }
    }

    @Benchmark
    fun longSparseArrayInsert(bh: Blackhole) {
        val s = LongSparseArray<String>(size)
        for (i in 0 until size) s.put(keys[i], values[i])
        bh.consume(s)
    }

    @Benchmark
    fun hashMapInsert(bh: Blackhole) {
        val m = HashMap<Long, String>(size)
        for (i in 0 until size) m[keys[i]] = values[i]
        bh.consume(m)
    }

    @Benchmark
    fun longSparseArrayLookup(bh: Blackhole) {
        for (i in 0 until size) bh.consume(sparseFilled.get(keys[i]))
    }

    @Benchmark
    fun hashMapLookup(bh: Blackhole) {
        for (i in 0 until size) bh.consume(hashFilled[keys[i]])
    }

    @Benchmark
    fun longSparseArrayIterate(bh: Blackhole) {
        for (i in 0 until sparseFilled.size()) {
            bh.consume(sparseFilled.keyAt(i))
            bh.consume(sparseFilled.valueAt(i))
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
    fun longSparseArrayRemove(bh: Blackhole) {
        val s = LongSparseArray<String>(size)
        for (i in 0 until size) s.put(keys[i], values[i])
        for (i in 0 until size) s.remove(keys[i])
        bh.consume(s)
    }

    @Benchmark
    fun hashMapRemove(bh: Blackhole) {
        val m = HashMap<Long, String>(size)
        for (i in 0 until size) m[keys[i]] = values[i]
        for (i in 0 until size) m.remove(keys[i])
        bh.consume(m)
    }
}
