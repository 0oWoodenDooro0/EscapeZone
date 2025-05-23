package com.gmail.vincent031525.escapezone.core

import net.minecraft.core.NonNullList
import net.minecraft.world.item.ItemStack

class GridSlot(private val gridHeight: Int, private val gridWidth: Int) {
    private val itemsGrid: NonNullList<NonNullList<ItemStack>> = NonNullList.create<NonNullList<ItemStack>>().apply {
        for (h in 0..<gridHeight) {
            add(NonNullList.withSize(gridWidth, ItemStack.EMPTY))
        }
    }

    private fun indexToGridPos(index: Int, column: Int = 0, row: Int = 0): Pair<Int, Int> {
        val x = index % gridWidth + row
        val y = index / gridWidth + column
        return Pair(y, x)
    }

    private fun gridPosToIndex(column: Int, row: Int): Int {
        val index = column * gridWidth + row
        return index
    }

    fun getFreeSlot(height: Int, width: Int): Int {
        if (height > gridHeight || width > gridWidth || height < 0 || width < 0) {
            return -1
        }
        for (column in 0..gridHeight - height) {
            for (row in 0..gridWidth - width) {
                val index = gridPosToIndex(column, row)
                if (isEmpty(index, height, width)) {
                    return index
                }
            }
        }
        return -1
    }

    fun setItem(index: Int, itemStack: ItemStack, itemHeight: Int, itemWidth: Int) {
        for (c in 0..<itemHeight) {
            for (r in 0..<itemWidth) {
                val (column, row) = indexToGridPos(index, c, r)
                itemsGrid[column][row] = itemStack
            }
        }
    }

    fun removeItem(index: Int, itemHeight: Int, itemWidth: Int) {
        for (c in 0..<itemHeight) {
            for (r in 0..<itemWidth) {
                val (column, row) = indexToGridPos(index, c, r)
                itemsGrid[column][row] = ItemStack.EMPTY
            }
        }
    }

    private fun isEmpty(index: Int, itemHeight: Int, itemWidth: Int): Boolean {
        for (c in 0..<itemHeight) {
            for (r in 0..<itemWidth) {
                val (column, row) = indexToGridPos(index, c, r)
                if (!itemsGrid[column][row].isEmpty) return false
            }
        }
        return true
    }

    fun clear() {
        for (column in 0..<gridHeight) {
            for (row in 0..<gridWidth) {
                itemsGrid[column][row] = ItemStack.EMPTY
            }
        }
    }

    fun isAllEmpty(): Boolean {
        for (column in 0..<gridHeight) {
            for (row in 0..<gridWidth) {
                if (!itemsGrid[column][row].isEmpty) return false
            }
        }
        return true
    }
}