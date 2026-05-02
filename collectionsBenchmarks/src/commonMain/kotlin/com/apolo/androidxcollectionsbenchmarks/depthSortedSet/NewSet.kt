package com.apolo.androidxcollectionsbenchmarks.depthSortedSet

import androidx.collection.mutableObjectIntMapOf
import androidx.collection.mutableObjectListOf


/**
 * Implements [SortedSet] via a min-heap (implemented via an array) and a hash-map mapping the
 * elements to their indices in the heap.
 *
 * The performance of this implementation is:
 * - [add], [remove]: O(logN), due to the heap.
 * - [first], [contains]: O(1), due to the hash map.
 */
internal  class NewSortedSet<E>(
    private val comparator: Comparator<in E>
) {

    /**
     * Compares two elements using the [comparator].
     */
    @Suppress("NOTHING_TO_INLINE")
    private inline operator fun E.compareTo(value: E): Int = comparator.compare(this, value)

    /**
     * The heap array.
     */
    private val itemTree = mutableObjectListOf<E>()

    /**
     * Returns whether the index is the root of the tree.
     */
    private inline val Int.isRootIndex get() = this == 0

    /**
     * Returns the index of the parent node.
     */
    private inline val Int.parentIndex get() = (this - 1) shr 1

    /**
     * Returns the index of the left child node.
     */
    private inline val Int.leftChildIndex get() = (this shl 1) + 1

    /**
     * Returns the index of the right child node.
     */
    private inline val Int.rightChildIndex get() = (this shl 1) + 2

    /**
     * Maps each element to its index in [itemTree].
     */
    private val indexByElement = mutableMapOf<E, Int>()

    /**
     * Inserts [element], if it's not already in the set.
     *
     * @returns whether actually inserted.
     */
    fun add(element: E): Boolean {
        if (element in indexByElement) {
            return false
        }

        // Insert the item at the rightmost leaf
        val index = itemTree.size
        itemTree.add(element)

        // Fix the heap
        heapifyUp(index, element)

        return true
    }

    /**
     * Removes [element], if it's in the set.
     *
     * @return whether actually removed.
     */
    fun remove(element: E): Boolean {
        // Get the index in the tree and remove it
        val index = indexByElement.remove(element) ?: return false

        // Remove the rightmost leaf (to move it in place of the remove element)
        val rightMostLeafElement = itemTree.removeAt(itemTree.lastIndex)

        // If the removed element is the rightmost leaf, then there's no need to move it, or to fix
        // the heap. This also takes care of the case when the set is empty after removal.
        if (index < itemTree.size) {
            // Restore min-heap invariant
            if (!index.isRootIndex && (itemTree[index.parentIndex] >= rightMostLeafElement)) {
                heapifyUp(index, rightMostLeafElement)
            } else {
                heapifyDown(index, rightMostLeafElement)
            }
        }

        return true
    }

    /**
     * Returns the smallest item in the set, according to [comparator].
     */
    fun first() = itemTree[0]

    /**
     * Returns whether the set is empty.
     */
    fun isEmpty(): Boolean = itemTree.isEmpty()

    /**
     * Returns whether the set contains the given element.
     */
    fun contains(element: E) = element in indexByElement

    /**
     * Bubbles up the element at the given index until the min-heap invariant is restored.
     */
    private fun heapifyUp(index: Int, element: E) {
        var currentIndex = index  // The index we're currently comparing to its parent

        while (!currentIndex.isRootIndex) {
            val parentIndex = currentIndex.parentIndex
            val parentElement = itemTree[parentIndex]

            // If the order is correct, stop
            if (parentElement <= element) {
                break
            }

            // Move parent down
            itemTree[currentIndex] = parentElement
            indexByElement[parentElement] = currentIndex

            // Continue with parent
            currentIndex = parentIndex
        }

        itemTree[currentIndex] = element
        indexByElement[element] = currentIndex
    }

    /**
     * Sinks down the element at the given index until the min-heap invariant is restored.
     */
    private fun heapifyDown(index: Int, element: E) {
        var currentIndex = index  // The index we're currently comparing to its children

        while (true) {
            val leftChildIndex = currentIndex.leftChildIndex
            if (leftChildIndex >= itemTree.size) {
                break
            }
            val leftChildElement = itemTree[leftChildIndex]
            val rightChildIndex = currentIndex.rightChildIndex

            val indexOfSmallerElement: Int
            val smallerElement: E
            if (rightChildIndex >= itemTree.size) {  // There's no right child
                // Look at left child
                indexOfSmallerElement = leftChildIndex
                smallerElement = leftChildElement
            } else {
                val rightChildElement = itemTree[rightChildIndex]
                // Look at the smaller child
                if (leftChildElement < rightChildElement) {
                    indexOfSmallerElement = leftChildIndex
                    smallerElement = leftChildElement
                } else {
                    indexOfSmallerElement = rightChildIndex
                    smallerElement = rightChildElement
                }
            }

            if (element <= smallerElement) {
                break
            }

            itemTree[currentIndex] = smallerElement
            indexByElement[smallerElement] = currentIndex
            currentIndex = indexOfSmallerElement
        }

        itemTree[currentIndex] = element
        indexByElement[element] = currentIndex
    }
}