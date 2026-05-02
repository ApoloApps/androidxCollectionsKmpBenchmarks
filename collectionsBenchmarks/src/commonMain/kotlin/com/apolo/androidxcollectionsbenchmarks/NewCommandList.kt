package com.apolo.androidxcollectionsbenchmarks

import androidx.collection.mutableObjectListOf
import kotlinx.atomicfu.locks.SynchronizedObject
import kotlinx.atomicfu.locks.synchronized


internal class NewCommandList(
    private var onNewCommand: () -> Unit
) {
    private val lock = SynchronizedObject()
    private val list = mutableObjectListOf<() -> Unit>()
    private val listCopy = mutableObjectListOf<() -> Unit>()

    /**
     * true if there are any commands added.
     *
     * Can be called concurrently from multiple threads.
     */
    val hasCommands: Boolean
        get() = synchronized(lock) {
            list.isNotEmpty()
        }

    /**
     * Add command to the list, and notify observer via [onNewCommand].
     *
     * Can be called concurrently from multiple threads.
     */
    fun add(command: () -> Unit) {
        synchronized(lock) {
            list.add(command)
        }
        onNewCommand()
    }

    /**
     * Clear added commands and perform them.
     *
     * Doesn't support multiple [perform]'s from different threads. But does support concurrent [perform]
     * and concurrent [add].
     */
    fun perform() {
        synchronized(lock) {
            listCopy.addAll(list)
            list.clear()
        }
        listCopy.forEach { it.invoke() }
        listCopy.clear()
    }
}