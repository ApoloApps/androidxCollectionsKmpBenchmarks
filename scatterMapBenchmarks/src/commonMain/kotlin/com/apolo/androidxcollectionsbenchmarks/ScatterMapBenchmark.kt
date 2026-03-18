package com.apolo.androidxcollectionsbenchmarks

import androidx.collection.MutableIntSet
import androidx.collection.MutableScatterSet
import kotlinx.benchmark.*

//@State(Scope.Benchmark)
//@Warmup(iterations = 2, time = 1, timeUnit = BenchmarkTimeUnit.SECONDS)
//@Measurement(iterations = 3, time = 1, timeUnit = BenchmarkTimeUnit.SECONDS)
//@OutputTimeUnit(BenchmarkTimeUnit.MICROSECONDS)
//@BenchmarkMode(Mode.Throughput)
//open class MutableIntSetBenchmarkTest {
//    private val objectCount = 100
//    private val values = IntArray(objectCount) { index -> index * 17 + 3 }
//    private val misses = IntArray(objectCount) { index -> index * 17 + 1_000_003 }
//
//    private val set = MutableIntSet(objectCount).also { target ->
//        repeat(objectCount) { index ->
//            target.add(values[index])
//        }
//    }
//
//    @Benchmark
//    fun forEach() {
//        @Suppress("ASSIGNED_BUT_NEVER_ACCESSED_VARIABLE") var last = -1
//        set.forEach { value ->
//            last = value
//        }
//    }
//
//    @Benchmark
//    fun add() {
//        val mutableSet = MutableIntSet(objectCount)
//
//        repeat(objectCount) { index ->
//            mutableSet.add(values[index])
//        }
//        mutableSet.clear()
//    }
//
//    @Benchmark
//    fun contains() {
//        repeat(objectCount) { index -> set.contains(values[index]) }
//    }
//
//    @Benchmark
//    fun addAll() {
//        val mutableSet = MutableIntSet(objectCount * 2)
//
//        repeat(objectCount) { index ->
//            mutableSet.add(values[index])
//        }
//        set.forEach { value ->
//            mutableSet.add(value)
//        }
//        mutableSet.clear()
//    }
//
//    @Benchmark
//    fun removeExisting() {
//        val mutableSet = MutableIntSet(objectCount)
//
//        repeat(objectCount) { index ->
//            mutableSet.add(values[index])
//        }
//        repeat(objectCount) { index ->
//            mutableSet.remove(values[index])
//        }
//        mutableSet.clear()
//    }
//
//    @Benchmark
//    fun removeMissing() {
//        val mutableSet = MutableIntSet(objectCount)
//
//        repeat(objectCount) { index ->
//            mutableSet.add(values[index])
//        }
//        repeat(objectCount) { index ->
//            mutableSet.remove(misses[index])
//        }
//        mutableSet.clear()
//    }
//
//    @Benchmark
//    fun containsMisses() {
//        repeat(objectCount) { index -> set.contains(misses[index]) }
//    }
//
//    @Benchmark
//    fun clear() {
//        val mutableSet = MutableIntSet(objectCount)
//
//        repeat(objectCount) { index ->
//            mutableSet.add(values[index])
//        }
//        mutableSet.clear()
//    }
//}
//
//@State(Scope.Benchmark)
//@Warmup(iterations = 2, time = 1, timeUnit = BenchmarkTimeUnit.SECONDS)
//@Measurement(iterations = 3, time = 1, timeUnit = BenchmarkTimeUnit.SECONDS)
//@OutputTimeUnit(BenchmarkTimeUnit.MICROSECONDS)
//@BenchmarkMode(Mode.Throughput)
//open class MutableSetOfIntBenchmarkTest {
//    private val objectCount = 100
//    private val values = IntArray(objectCount) { index -> index * 17 + 3 }
//    private val misses = IntArray(objectCount) { index -> index * 17 + 1_000_003 }
//
//    private val set: MutableSet<Int> = mutableSetOf<Int>().also { target ->
//        repeat(objectCount) { index ->
//            target.add(values[index])
//        }
//    }
//
//    @Benchmark
//    fun forEach() {
//        @Suppress("ASSIGNED_BUT_NEVER_ACCESSED_VARIABLE") var last = -1
//        set.forEach { value ->
//            last = value
//        }
//    }
//
//    @Benchmark
//    fun add() {
//        val mutableSet = mutableSetOf<Int>()
//
//        repeat(objectCount) { index ->
//            mutableSet.add(values[index])
//        }
//        mutableSet.clear()
//    }
//
//    @Benchmark
//    fun contains() {
//        repeat(objectCount) { index -> set.contains(values[index]) }
//    }
//
//    @Benchmark
//    fun addAll() {
//        val mutableSet = mutableSetOf<Int>()
//
//        repeat(objectCount) { index ->
//            mutableSet.add(values[index])
//        }
//        mutableSet.addAll(set)
//        mutableSet.clear()
//    }
//
//    @Benchmark
//    fun removeExisting() {
//        val mutableSet = mutableSetOf<Int>()
//
//        repeat(objectCount) { index ->
//            mutableSet.add(values[index])
//        }
//        repeat(objectCount) { index ->
//            mutableSet.remove(values[index])
//        }
//        mutableSet.clear()
//    }
//
//    @Benchmark
//    fun removeMissing() {
//        val mutableSet = mutableSetOf<Int>()
//
//        repeat(objectCount) { index ->
//            mutableSet.add(values[index])
//        }
//        repeat(objectCount) { index ->
//            mutableSet.remove(misses[index])
//        }
//        mutableSet.clear()
//    }
//
//    @Benchmark
//    fun containsMisses() {
//        repeat(objectCount) { index -> set.contains(misses[index]) }
//    }
//
//    @Benchmark
//    fun clear() {
//        val mutableSet = mutableSetOf<Int>()
//
//        repeat(objectCount) { index ->
//            mutableSet.add(values[index])
//        }
//        mutableSet.clear()
//    }
//}

@State(Scope.Benchmark)
@Warmup(iterations = 2, time = 1, timeUnit = BenchmarkTimeUnit.SECONDS)
@Measurement(iterations = 3, time = 1, timeUnit = BenchmarkTimeUnit.SECONDS)
@OutputTimeUnit(BenchmarkTimeUnit.MICROSECONDS)
@BenchmarkMode(Mode.Throughput)
open class MutableScatterSetBenchmarkTest {
	private val objectCount = 100
	private val values = IntArray(objectCount) { index -> index * 17 + 3 }
	private val misses = IntArray(objectCount) { index -> index * 17 + 1_000_003 }

	private val set = MutableScatterSet<Int>(objectCount).also { target ->
		repeat(objectCount) { index ->
			target.add(values[index])
		}
	}

	@Benchmark
	fun forEach() {
		@Suppress("ASSIGNED_BUT_NEVER_ACCESSED_VARIABLE") var last = -1
		set.forEach { element -> last = element }
	}

	@Benchmark
	fun add() {
		val mutableSet = MutableScatterSet<Int>(objectCount)

		repeat(objectCount) { mutableSet.add(values[it]) }
		mutableSet.clear()
	}

	@Benchmark
	fun contains() {
		repeat(objectCount) { set.contains(values[it]) }
	}

	@Benchmark
	fun containsMisses() {
		repeat(objectCount) { set.contains(misses[it]) }
	}

	@Benchmark
	fun addAll() {
		val mutableSet = MutableScatterSet<Int>(objectCount)

		set.forEach { mutableSet.add(it) }
		mutableSet.clear()
	}

	@Benchmark
	fun removeExisting() {
		val mutableSet = MutableScatterSet<Int>(objectCount)

		set.forEach { mutableSet.add(it) }
		repeat(objectCount) { mutableSet.remove(values[it]) }
		mutableSet.clear()
	}

	@Benchmark
	fun removeMissing() {
		val mutableSet = MutableScatterSet<Int>(objectCount)

		set.forEach { mutableSet.add(it) }
		repeat(objectCount) { mutableSet.remove(misses[it]) }
		mutableSet.clear()
	}

	@Benchmark
	fun clear() {
		val mutableSet = MutableScatterSet<Int>(objectCount)

		set.forEach { mutableSet.add(it) }
		mutableSet.clear()
	}
}

@State(Scope.Benchmark)
@Warmup(iterations = 2, time = 1, timeUnit = BenchmarkTimeUnit.SECONDS)
@Measurement(iterations = 3, time = 1, timeUnit = BenchmarkTimeUnit.SECONDS)
@OutputTimeUnit(BenchmarkTimeUnit.MICROSECONDS)
@BenchmarkMode(Mode.Throughput)
open class MutableSetBenchmarkTest {
	private val objectCount = 100
	private val values = IntArray(objectCount) { index -> index * 17 + 3 }
	private val misses = IntArray(objectCount) { index -> index * 17 + 1_000_003 }

	private val set: MutableSet<Int> = mutableSetOf<Int>().also { target ->
		repeat(objectCount) { index ->
			target.add(values[index])
		}
	}

	@Benchmark
	fun forEach() {
		@Suppress("ASSIGNED_BUT_NEVER_ACCESSED_VARIABLE") var last = -1
		set.forEach { element -> last = element }
	}

	@Benchmark
	fun add() {
		val mutableSet = mutableSetOf<Int>()

		repeat(objectCount) { mutableSet.add(values[it]) }
		mutableSet.clear()
	}

	@Benchmark
	fun contains() {
		repeat(objectCount) { set.contains(values[it]) }
	}

	@Benchmark
	fun containsMisses() {
		repeat(objectCount) { set.contains(misses[it]) }
	}

	@Benchmark
	fun addAll() {
		val mutableSet = mutableSetOf<Int>()

		set.forEach { mutableSet.add(it) }
		mutableSet.clear()
	}

	@Benchmark
	fun removeExisting() {
		val mutableSet = mutableSetOf<Int>()

		set.forEach { mutableSet.add(it) }
		repeat(objectCount) { mutableSet.remove(values[it]) }
		mutableSet.clear()
	}

	@Benchmark
	fun removeMissing() {
		val mutableSet = mutableSetOf<Int>()

		set.forEach { mutableSet.add(it) }
		repeat(objectCount) { mutableSet.remove(misses[it]) }
		mutableSet.clear()
	}

	@Benchmark
	fun clear() {
		val mutableSet = mutableSetOf<Int>()

		set.forEach { mutableSet.add(it) }
		mutableSet.clear()
	}
}