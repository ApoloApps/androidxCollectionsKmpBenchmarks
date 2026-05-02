package com.apolo.androidxcollectionsbenchmarks

import androidx.collection.CircularArray
import kotlinx.benchmark.*

@State(Scope.Benchmark)
@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(BenchmarkTimeUnit.MICROSECONDS)
@Warmup(iterations = 2, time = 2)
@Measurement(iterations = 4, time = 2)
open class ArrayDequeBenchmark {

    @Param("16", "256", "4096")
    var size: Int = 0

    // Pre-defined lambda to avoid measuring lambda creation time
    private val action: () -> Unit = {
        // Minimal work to prevent the call from being optimized away
        var x = 1
        x += 1
    }

    // --- Lambda Performance (Add + Call + Pop) ---

    @Benchmark
    fun arrayDequeLambdaInvoke(bh: Blackhole) {
        val deque = ArrayDeque<() -> Unit>(size) // Pre-allocated to measure logic
        for (i in 0 until size) {
            deque.addLast(action)
        }
        while (deque.isNotEmpty()) {
            val func = deque.removeFirst()
            func.invoke() // Measure the function call overhead
            bh.consume(func)
        }
    }

    @Benchmark
    fun circularArrayLambdaInvoke(bh: Blackhole) {
        val arr = CircularArray<() -> Unit>()
        for (i in 0 until size) {
            arr.addLast(action)
        }
        while (!arr.isEmpty()) {
            val func = arr.popFirst()
            func.invoke()
            bh.consume(func)
        }
    }
}

