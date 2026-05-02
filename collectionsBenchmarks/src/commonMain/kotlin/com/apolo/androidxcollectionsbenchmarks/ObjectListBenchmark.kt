package com.apolo.androidxcollectionsbenchmarks

import androidx.collection.MutableObjectList
import kotlinx.benchmark.*

/**
 * Benchmarks `androidx.collection.MutableObjectList<T>` against the Kotlin std lib `ArrayList<T>`.
 *
 * `MutableObjectList` keeps elements in a single contiguous array (no boxing of indices, no
 * delegated `Iterator`) so it should win on append, indexed read and `forEach` iteration. We do
 * not include random-access index lookup that requires creating a `ListIterator`, since both
 * collections perform identically for that case.
 */
@State(Scope.Benchmark)
@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(BenchmarkTimeUnit.MICROSECONDS)
@Warmup(iterations = 3, time = 2)
@Measurement(iterations = 5, time = 2)
open class ObjectListBenchmark {

    @Param("16", "256", "4096")
    var size: Int = 0

    private lateinit var source: Array<String>

    private lateinit var objectFilled: MutableObjectList<String>
    private lateinit var arrayFilled: ArrayList<String>

    @Setup
    fun setup() {
        source = Array(size) { "v_$it" }
        objectFilled = MutableObjectList<String>(size).also { l -> for (e in source) l.add(e) }
        arrayFilled = ArrayList<String>(size).also { l -> for (e in source) l.add(e) }
    }

    @Benchmark
    fun objectListAdd(bh: Blackhole) {
        val l = MutableObjectList<String>(size)
        for (e in source) l.add(e)
        bh.consume(l)
    }

    @Benchmark
    fun arrayListAdd(bh: Blackhole) {
        val l = ArrayList<String>(size)
        for (e in source) l.add(e)
        bh.consume(l)
    }

    /** Append without a pre-sized buffer to measure realistic growth cost. */
    @Benchmark
    fun objectListAddNoCapacity(bh: Blackhole) {
        val l = MutableObjectList<String>()
        for (e in source) l.add(e)
        bh.consume(l)
    }

    @Benchmark
    fun arrayListAddNoCapacity(bh: Blackhole) {
        val l = ArrayList<String>()
        for (e in source) l.add(e)
        bh.consume(l)
    }

    @Benchmark
    fun objectListGet(bh: Blackhole) {
        for (i in 0 until size) bh.consume(objectFilled[i])
    }

    @Benchmark
    fun arrayListGet(bh: Blackhole) {
        for (i in 0 until size) bh.consume(arrayFilled[i])
    }

    @Benchmark
    fun objectListForEach(bh: Blackhole) {
        objectFilled.forEach { bh.consume(it) }
    }

    @Benchmark
    fun arrayListForEach(bh: Blackhole) {
        arrayFilled.forEach { bh.consume(it) }
    }

    @Benchmark
    fun objectListContains(bh: Blackhole) {
        // Worst case: lookup the last element so we traverse the whole list.
        bh.consume(objectFilled.contains(source[size - 1]))
    }

    @Benchmark
    fun arrayListContains(bh: Blackhole) {
        bh.consume(arrayFilled.contains(source[size - 1]))
    }
}
