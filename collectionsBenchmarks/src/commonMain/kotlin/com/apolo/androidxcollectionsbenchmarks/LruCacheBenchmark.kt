package com.apolo.androidxcollectionsbenchmarks

import androidx.collection.LruCache
import kotlinx.benchmark.*

/**
 * Benchmarks `androidx.collection.LruCache<K, V>` against a Kotlin std-lib LRU built on top of
 * `LinkedHashMap`.
 *
 * Kotlin's common-stdlib `LinkedHashMap` does not expose `accessOrder` / `removeEldestEntry`, so
 * the std-lib counterpart [StdLibLruCache] reorders manually with `remove + put` on access and
 * trims the eldest entry when capacity is exceeded. This is the typical KMP workaround when
 * `androidx.collection` is not available.
 *
 * The benchmark exercises three realistic patterns:
 *  - cold puts that fill the cache
 *  - hot lookups (all hits) that reorder entries
 *  - mixed put/get traffic with eviction (working set ≥ capacity)
 */
@State(Scope.Benchmark)
@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(BenchmarkTimeUnit.MICROSECONDS)
@Warmup(iterations = 3, time = 2)
@Measurement(iterations = 5, time = 2)
open class LruCacheBenchmark {

    @Param("16", "256", "4096")
    var size: Int = 0

    private lateinit var keys: Array<String>
    private lateinit var values: Array<String>

    private lateinit var androidxFilled: LruCache<String, String>
    private lateinit var stdlibFilled: StdLibLruCache<String, String>

    @Setup
    fun setup() {
        keys = Array(size) { "k_$it" }
        values = Array(size) { "v_$it" }
        androidxFilled = LruCache<String, String>(size).also { c ->
            for (i in 0 until size) c.put(keys[i], values[i])
        }
        stdlibFilled = StdLibLruCache<String, String>(size).also { c ->
            for (i in 0 until size) c.put(keys[i], values[i])
        }
    }

    // ---- COLD PUTS ----

    @Benchmark
    fun androidxLruPut(bh: Blackhole) {
        val c = LruCache<String, String>(size)
        for (i in 0 until size) c.put(keys[i], values[i])
        bh.consume(c)
    }

    @Benchmark
    fun stdlibLruPut(bh: Blackhole) {
        val c = StdLibLruCache<String, String>(size)
        for (i in 0 until size) c.put(keys[i], values[i])
        bh.consume(c)
    }

    // ---- HOT GET (all hits, triggers LRU reorder) ----

    @Benchmark
    fun androidxLruGet(bh: Blackhole) {
        for (i in 0 until size) bh.consume(androidxFilled.get(keys[i]))
    }

    @Benchmark
    fun stdlibLruGet(bh: Blackhole) {
        for (i in 0 until size) bh.consume(stdlibFilled.get(keys[i]))
    }

    // ---- MIXED PUT + GET WITH EVICTION ----
    // Working set is 2x the cache size, simulating realistic eviction churn.

    @Benchmark
    fun androidxLruPutGetEvict(bh: Blackhole) {
        val cap = size
        val c = LruCache<String, String>(cap)
        val workingSet = cap * 2
        for (i in 0 until workingSet) {
            val key = "wk_$i"
            c.put(key, "v")
        }
        for (i in 0 until workingSet) bh.consume(c.get("wk_$i"))
    }

    @Benchmark
    fun stdlibLruPutGetEvict(bh: Blackhole) {
        val cap = size
        val c = StdLibLruCache<String, String>(cap)
        val workingSet = cap * 2
        for (i in 0 until workingSet) {
            val key = "wk_$i"
            c.put(key, "v")
        }
        for (i in 0 until workingSet) bh.consume(c.get("wk_$i"))
    }
}

/**
 * Minimal common-multiplatform LRU built on top of `LinkedHashMap`, mimicking the public surface
 * of `LruCache` we exercise in benchmarks. Kept in this file because it is only used by the
 * benchmark; not intended as a production-grade implementation.
 */
class StdLibLruCache<K, V>(private val maxSize: Int) {
    private val map = LinkedHashMap<K, V>(maxSize, 0.75f)

    fun put(key: K, value: V): V? {
        // Move-to-end on update.
        map.remove(key)
        val previous = map.put(key, value)
        if (map.size > maxSize) {
            val eldest = map.keys.iterator().next()
            map.remove(eldest)
        }
        return previous
    }

    fun get(key: K): V? {
        val value = map.remove(key) ?: return null
        // Reinsert to move to the most-recently-used position.
        map[key] = value
        return value
    }

    fun size(): Int = map.size
}
