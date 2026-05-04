package com.apolo.androidxcollectionsbenchmarks

import androidx.collection.MutableObjectList
import androidx.compose.runtime.collection.MutableVector
import androidx.compose.runtime.collection.mutableVectorOf
import kotlinx.benchmark.*

/**
 * Benchmarks Jetpack Compose's `MutableVector<T>` against the Kotlin std lib `ArrayList<T>` and `androidx.collection.MutableObjectList<T>`.
 */
@State(Scope.Benchmark)
@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(BenchmarkTimeUnit.MICROSECONDS)
@Warmup(iterations = 2, time = 2)
@Measurement(iterations = 4, time = 2)
open class MutableVectorBenchmark {

    @Param("16", "256", "4096")
    var size: Int = 0

    private lateinit var source: Array<String>

    private lateinit var vectorFilled: MutableVector<String>
    private lateinit var arrayFilled: ArrayList<String>
    private lateinit var objectListFilled: MutableObjectList<String>

    @Setup
    fun setup() {
        source = Array(size) { "v_$it" }
        vectorFilled = MutableVector<String>(size).also { l -> for (e in source) l.add(e) }
        arrayFilled = ArrayList<String>(size).also { l -> for (e in source) l.add(e) }
        objectListFilled = MutableObjectList<String>(size).also { l -> for (e in source) l.add(e) }
    }

    @Benchmark
    fun vectorAdd(bh: Blackhole) {
        val l = MutableVector<String>(size)
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
    fun vectorAddNoCapacity(bh: Blackhole) {
        val l = MutableVector<String>()
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
    fun vectorGet(bh: Blackhole) {
        for (i in 0 until size) bh.consume(vectorFilled[i])
    }

    @Benchmark
    fun arrayListGet(bh: Blackhole) {
        for (i in 0 until size) bh.consume(arrayFilled[i])
    }

    @Benchmark
    fun vectorForEach(bh: Blackhole) {
        vectorFilled.forEach { bh.consume(it) }
    }

    @Benchmark
    fun arrayListForEach(bh: Blackhole) {
        arrayFilled.forEach { bh.consume(it) }
    }

    @Benchmark
    fun vectorContains(bh: Blackhole) {
        // Worst case: lookup the last element so we traverse the whole list.
        bh.consume(vectorFilled.contains(source[size - 1]))
    }

    @Benchmark
    fun arrayListContains(bh: Blackhole) {
        bh.consume(arrayFilled.contains(source[size - 1]))
    }

    @Benchmark
    fun objectListAdd(bh: Blackhole) {
        val l = MutableObjectList<String>(size)
        for (e in source) l.add(e)
        bh.consume(l)
    }

    @Benchmark
    fun objectListAddNoCapacity(bh: Blackhole) {
        val l = MutableObjectList<String>()
        for (e in source) l.add(e)
        bh.consume(l)
    }

    @Benchmark
    fun objectListGet(bh: Blackhole) {
        for (i in 0 until size) bh.consume(objectListFilled[i])
    }

    @Benchmark
    fun objectListForEach(bh: Blackhole) {
        objectListFilled.forEach { bh.consume(it) }
    }

    @Benchmark
    fun objectListContains(bh: Blackhole) {
        // Worst case: lookup the last element so we traverse the whole list.
        bh.consume(objectListFilled.contains(source[size - 1]))
    }
}
