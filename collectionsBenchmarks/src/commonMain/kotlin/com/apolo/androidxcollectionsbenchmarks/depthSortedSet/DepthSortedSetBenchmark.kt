package com.apolo.androidxcollectionsbenchmarks.depthSortedSet

import kotlinx.benchmark.*
import kotlin.random.Random

@State(Scope.Benchmark)
@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(BenchmarkTimeUnit.MICROSECONDS)
@Warmup(iterations = 2, time = 2)
@Measurement(iterations = 4, time = 2)
open class DepthSortedSetBenchmark {

    @Param("16", "256", "4096")
    var size: Int = 0

    private lateinit var source: IntArray

    private lateinit var newSetFilled: NewSortedSet<Int>
    private lateinit var oldSetFilled: OldSortedSet<Int>
    
    private val comparator = Comparator<Int> { a, b -> a.compareTo(b) }

    @Setup
    fun setup() {
        source = IntArray(size) { it }
        source.shuffle(Random(42))

        newSetFilled = NewSortedSet(comparator)
        oldSetFilled = OldSortedSet(comparator)
        
        for (i in 0 until size) {
            newSetFilled.add(source[i])
            oldSetFilled.add(source[i])
        }
    }

    @Benchmark
    fun newSetAdd(bh: Blackhole) {
        val s = NewSortedSet(comparator)
        for (i in 0 until size) s.add(source[i])
        bh.consume(s)
    }

    @Benchmark
    fun oldSetAdd(bh: Blackhole) {
        val s = OldSortedSet(comparator)
        for (i in 0 until size) s.add(source[i])
        bh.consume(s)
    }

    @Benchmark
    fun newSetRemove(bh: Blackhole) {
        val s = NewSortedSet(comparator)
        for (i in 0 until size) s.add(source[i])
        for (i in 0 until size) s.remove(source[i])
        bh.consume(s)
    }

    @Benchmark
    fun oldSetRemove(bh: Blackhole) {
        val s = OldSortedSet(comparator)
        for (i in 0 until size) s.add(source[i])
        for (i in 0 until size) s.remove(source[i])
        bh.consume(s)
    }

    @Benchmark
    fun newSetContains(bh: Blackhole) {
        bh.consume(newSetFilled.contains(source[size - 1]))
    }

    @Benchmark
    fun oldSetContains(bh: Blackhole) {
        bh.consume(oldSetFilled.contains(source[size - 1]))
    }

    @Benchmark
    fun newSetFirst(bh: Blackhole) {
        bh.consume(newSetFilled.first())
    }

    @Benchmark
    fun oldSetFirst(bh: Blackhole) {
        bh.consume(oldSetFilled.first())
    }
}

