package com.apolo.androidxcollectionsbenchmarks

import androidx.collection.MutableObjectList
import androidx.collection.ObjectList
import kotlinx.benchmark.*

@State(Scope.Benchmark)
@Warmup(iterations = 2, time = 1, timeUnit = BenchmarkTimeUnit.SECONDS)
@Measurement(iterations = 3, time = 1, timeUnit = BenchmarkTimeUnit.SECONDS)
@OutputTimeUnit(BenchmarkTimeUnit.MICROSECONDS)
@BenchmarkMode(Mode.Throughput)
open class ObjectListBenchmarkTest {
    val ObjectCount = 100
    private val list: ObjectList<String> =
        MutableObjectList<String>(ObjectCount).also { list ->
            repeat(ObjectCount) { list += it.toString() }
        }

    private val array = Array(ObjectCount) { it.toString() }

    @Benchmark
    fun forEach() {

        @Suppress("ASSIGNED_BUT_NEVER_ACCESSED_VARIABLE") var last: String
        list.forEach { element -> last = element }

    }

    @Benchmark
    fun add() {
        val mutableList = MutableObjectList<String>(ObjectCount)

        repeat(ObjectCount) { mutableList += array[it] }
        mutableList.clear()

    }

    @Benchmark
    fun contains() {
        repeat(ObjectCount) { list.contains(array[it]) }
    }

    @Benchmark
    fun get() {
        repeat(ObjectCount) { list[it] }
    }

    @Benchmark
    fun set() {
        val mutableList = MutableObjectList<String>(ObjectCount)

        mutableList += list
        repeat(ObjectCount) { mutableList[it] = array[it] }
        mutableList.clear()

    }

    @Benchmark
    fun addAll() {
        val mutableList = MutableObjectList<String>(ObjectCount)

        mutableList += list
        mutableList.clear()

    }

    @Benchmark
    fun removeStart() {
        val mutableList = MutableObjectList<String>(ObjectCount)

        mutableList += list
        repeat(ObjectCount) { mutableList.removeAt(0) }

    }

    @Benchmark
    fun removeEnd() {
        val mutableList = MutableObjectList<String>(ObjectCount)

        mutableList += list
        for (i in ObjectCount - 1 downTo 0) {
            mutableList.removeAt(i)
        }

    }
}


@State(Scope.Benchmark)
@Warmup(iterations = 2, time = 1, timeUnit = BenchmarkTimeUnit.SECONDS)
@Measurement(iterations = 3, time = 1, timeUnit = BenchmarkTimeUnit.SECONDS)
@OutputTimeUnit(BenchmarkTimeUnit.MICROSECONDS)
@BenchmarkMode(Mode.Throughput)
open class MutableListBenchmarkTest {
    val ObjectCount = 100
    private val list: MutableList<String> =
        MutableList(ObjectCount) {
            it.toString()
        }

    private val array = Array(ObjectCount) { it.toString() }

    @Benchmark
    fun forEach() {

        @Suppress("ASSIGNED_BUT_NEVER_ACCESSED_VARIABLE") var last: String
        list.forEach { element -> last = element }

    }

    @Benchmark
    fun add() {
        val mutableList = ArrayList<String>(ObjectCount)

        repeat(ObjectCount) { mutableList += array[it] }
        mutableList.clear()

    }

    @Benchmark
    fun contains() {
        repeat(ObjectCount) { list.contains(array[it]) }
    }

    @Benchmark
    fun get() {
        repeat(ObjectCount) { list[it] }
    }

    @Benchmark
    fun set() {
        val mutableList = ArrayList<String>(ObjectCount)

        mutableList += list
        repeat(ObjectCount) { mutableList[it] = array[it] }
        mutableList.clear()

    }

    @Benchmark
    fun addAll() {
        val mutableList = ArrayList<String>(ObjectCount)

        mutableList += list
        mutableList.clear()

    }

    @Benchmark
    fun removeStart() {
        val mutableList = ArrayList<String>(ObjectCount)

        mutableList += list
        repeat(ObjectCount) { mutableList.removeAt(0) }

    }

    @Benchmark
    fun removeEnd() {
        val mutableList = ArrayList<String>(ObjectCount)

        mutableList += list
        for (i in ObjectCount - 1 downTo 0) {
            mutableList.removeAt(i)
        }

    }
}