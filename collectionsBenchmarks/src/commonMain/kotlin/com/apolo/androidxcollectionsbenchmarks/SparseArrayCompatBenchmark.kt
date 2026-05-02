package com.apolo.androidxcollectionsbenchmarks

import androidx.collection.SparseArrayCompat
import kotlinx.benchmark.*

/**
 * Benchmarks `androidx.collection.SparseArrayCompat<V>` against `HashMap<Int, V>`.
 *
 * Same trade-off as `LongSparseArray` but with `Int` keys: better cache behaviour for small N,
 * worse for inserts that scatter keys (here we use sorted-append keys = best case to compare
 * peak performance fairly).
 */
@State(Scope.Benchmark)
@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(BenchmarkTimeUnit.MICROSECONDS)
@Warmup(iterations = 3, time = 2)
@Measurement(iterations = 5, time = 2)
open class SparseArrayCompatBenchmark {

    @Param("16", "256", "4096")
    var size: Int = 0

    private lateinit var keys: IntArray
    private lateinit var values: Array<String>

    private lateinit var sparseFilled: SparseArrayCompat<String>
    private lateinit var hashFilled: HashMap<Int, String>

    @Setup
    fun setup() {
        keys = IntArray(size) { it * 7 + 1 }
        values = Array(size) { "v_$it" }
        sparseFilled = SparseArrayCompat<String>(size).also { s ->
            for (i in 0 until size) s.put(keys[i], values[i])
        }
        hashFilled = HashMap<Int, String>(size).also { m ->
            for (i in 0 until size) m[keys[i]] = values[i]
        }
    }

    @Benchmark
    fun sparseArrayCompatInsert(bh: Blackhole) {
        val s = SparseArrayCompat<String>(size)
        for (i in 0 until size) s.put(keys[i], values[i])
        bh.consume(s)
    }

    @Benchmark
    fun hashMapInsert(bh: Blackhole) {
        val m = HashMap<Int, String>(size)
        for (i in 0 until size) m[keys[i]] = values[i]
        bh.consume(m)
    }

    @Benchmark
    fun sparseArrayCompatLookup(bh: Blackhole) {
        for (i in 0 until size) bh.consume(sparseFilled.get(keys[i]))
    }

    @Benchmark
    fun hashMapLookup(bh: Blackhole) {
        for (i in 0 until size) bh.consume(hashFilled[keys[i]])
    }

    @Benchmark
    fun sparseArrayCompatIterate(bh: Blackhole) {
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
    fun sparseArrayCompatRemove(bh: Blackhole) {
        val s = SparseArrayCompat<String>(size)
        for (i in 0 until size) s.put(keys[i], values[i])
        for (i in 0 until size) s.remove(keys[i])
        bh.consume(s)
    }

    @Benchmark
    fun hashMapRemove(bh: Blackhole) {
        val m = HashMap<Int, String>(size)
        for (i in 0 until size) m[keys[i]] = values[i]
        for (i in 0 until size) m.remove(keys[i])
        bh.consume(m)
    }
}
