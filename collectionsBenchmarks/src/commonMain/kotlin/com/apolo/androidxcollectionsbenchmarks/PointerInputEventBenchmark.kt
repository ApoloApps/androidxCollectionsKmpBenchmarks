package com.apolo.androidxcollectionsbenchmarks

import androidx.collection.LongLongMap
import androidx.collection.buildLongLongMap
import kotlin.jvm.JvmInline
import kotlinx.benchmark.*

/**
 * Micro-benchmark comparing two implementations of `PointerInputEvent.isSamePosition(previousEvent)`:
 *
 *  1. `mapImpl`        — current implementation using a boxed `Map<PointerId, Offset>` built via
 *                        `Iterable.associate { ... }`.
 *  2. `longLongMapImpl`— alternative implementation using `androidx.collection.LongLongMap` built
 *                        via `buildLongLongMap { put(id.value, position.packedValue) }`.
 *
 * Mocked Compose UI types (`PointerInputEvent`, `PointerInputEventData`, `PointerId`, `Offset`)
 * are defined below with the minimal surface needed by the function under test, mirroring the
 * shape of the real Compose UI classes (value-class IDs/offsets backed by `Long`).
 *
 * Two scenarios are exercised per pointer count to capture both branches of the loop:
 *  - `same`     — every pointer has the same position in both events (worst case: every entry is
 *                 looked up AND compared).
 *  - `moved`    — the last pointer's position changed (first miss happens late so cost is similar
 *                 to `same`, but tests the unequal branch).
 *
 * Run only this benchmark with:
 *   `./gradlew :collectionsBenchmarks:jvmPointerinputeventbenchmarkonlyBenchmark`
 * (or the equivalent platform task — the configuration is registered in `build.gradle.kts`).
 */
@State(Scope.Benchmark)
@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(BenchmarkTimeUnit.MICROSECONDS)
@Warmup(iterations = 2, time = 2)
@Measurement(iterations = 4, time = 2)
open class PointerInputEventBenchmark {

    /** Realistic multi-touch pointer counts plus larger sizes to amplify per-entry overhead. */
    @Param("1", "2", "5", "10")
    var pointers: Int = 0

    private lateinit var current: PointerInputEvent
    private lateinit var sameAsPrevious: PointerInputEvent
    private lateinit var movedPrevious: PointerInputEvent

    @Setup
    fun setup() {
        // Spread the ids/positions so packed Long keys are not all equal — keeps hashing realistic.
        val data = List(pointers) { i ->
            PointerInputEventData(
                id = PointerId((i * 0x9E37_79B9L) or 1L),
                position = Offset(packFloats(i.toFloat() * 13f, i.toFloat() * 17f))
            )
        }
        current = PointerInputEvent(data)
        sameAsPrevious = PointerInputEvent(data.map { it.copy() })

        // movedPrevious: identical except the LAST pointer's position differs.
        val moved = data.toMutableList()
        if (moved.isNotEmpty()) {
            val last = moved.last()
            moved[moved.lastIndex] = last.copy(
                position = Offset(packFloats(-1f, -1f))
            )
        }
        movedPrevious = PointerInputEvent(moved)
    }

    // ----------------------------- Map<PointerId, Offset> ----------------------------------

    @Benchmark
    fun mapImpl_same(bh: Blackhole) {
        bh.consume(current.isSamePositionMap(sameAsPrevious))
    }

    @Benchmark
    fun mapImpl_moved(bh: Blackhole) {
        bh.consume(current.isSamePositionMap(movedPrevious))
    }

    @Benchmark
    fun mapImpl_nullPrevious(bh: Blackhole) {
        bh.consume(current.isSamePositionMap(null))
    }

    // ----------------------------- LongLongMap (primitive) ---------------------------------

    @Benchmark
    fun longLongMapImpl_same(bh: Blackhole) {
        bh.consume(current.isSamePositionLongLong(sameAsPrevious))
    }

    @Benchmark
    fun longLongMapImpl_moved(bh: Blackhole) {
        bh.consume(current.isSamePositionLongLong(movedPrevious))
    }

    @Benchmark
    fun longLongMapImpl_nullPrevious(bh: Blackhole) {
        bh.consume(current.isSamePositionLongLong(null))
    }
}

// ====================================================================================
// Implementations under test
// ====================================================================================

/** Original implementation: boxed `Map<PointerId, Offset>` built with `associate`. */
private fun PointerInputEvent.isSamePositionMap(previousEvent: PointerInputEvent?): Boolean {
    val previousIdToPosition = previousEvent?.pointers?.associate { it.id to it.position }
    return pointers.fastAll {
        val previousPosition = previousIdToPosition?.get(it.id)
        previousPosition == null || it.position == previousPosition
    }
}

/** Alternative implementation: primitive `LongLongMap` built with `buildLongLongMap`. */
private fun PointerInputEvent.isSamePositionLongLong(previousEvent: PointerInputEvent?): Boolean {
    val previousIdToPosition: LongLongMap? = previousEvent?.pointers?.let { ptrs ->
        buildLongLongMap(ptrs.size) {
            ptrs.fastForEach { ptr ->
                put(ptr.id.value, ptr.position.packedValue)
            }
        }
    }
    return pointers.fastAll {
         if (previousIdToPosition == null) return@fastAll true

        val previousPosition = previousIdToPosition.getOrDefault(it.id.value, MISSING)
        previousPosition == MISSING || it.position.packedValue == previousPosition
    }
}

private const val MISSING: Long = Long.MIN_VALUE

// ====================================================================================
// Mocked Compose UI types (minimal surface needed by the function under test)
// ====================================================================================

@JvmInline
internal value class PointerId(val value: Long)

@JvmInline
internal value class Offset(val packedValue: Long) {
    override fun toString(): String = "Offset($packedValue)"
}

internal data class PointerInputEventData(
    val id: PointerId,
    val position: Offset,
)

internal class PointerInputEvent(val pointers: List<PointerInputEventData>)

// ====================================================================================
// Compose-style `fast*` helpers (kept private so they don't clash if the project ever
// pulls in `androidx.compose.ui.util.*`).
// ====================================================================================

private inline fun <T> List<T>.fastAll(predicate: (T) -> Boolean): Boolean {
    for (i in indices) {
        if (!predicate(this[i])) return false
    }
    return true
}

private inline fun <T> List<T>.fastForEach(action: (T) -> Unit) {
    for (i in indices) action(this[i])
}

/** Pack two `Float`s into a single `Long` the same way `androidx.compose.ui.geometry.Offset` does. */
inline fun packFloats(val1: Float, val2: Float): Long {
    val v1 = val1.toRawBits().toLong()
    val v2 = val2.toRawBits().toLong()
    return (v1 shl 32) or (v2 and 0xFFFFFFFF)
}
