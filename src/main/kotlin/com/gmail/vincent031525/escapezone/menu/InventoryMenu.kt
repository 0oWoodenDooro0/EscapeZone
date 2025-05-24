package com.gmail.vincent031525.escapezone.menu

import net.minecraft.network.FriendlyByteBuf
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.inventory.Slot
import net.minecraft.world.item.ItemStack

class InventoryMenu(containerId: Int, private val inventory: Inventory, extraData: FriendlyByteBuf?) :
    AbstractContainerMenu(ModMenuTypes.inventoryMenu.value(), containerId) {

    init {
        addSlot(Slot(inventory, 0, 8, 86))
        addSlot(Slot(inventory, 1, 30, 86))
        addSlot(Slot(inventory, 36, 8, 8))
        addSlot(Slot(inventory, 37, 8, 26))
        addSlot(Slot(inventory, 38, 8, 44))
        addSlot(Slot(inventory, 39, 8, 62))
        addSlot(Slot(inventory, 40, 54, 85))
        addGridSlot(4, 5, 41, 80, 8)
        addGridSlot(4, 5, 61, 80, 86)
    }

    fun addGridSlot(height: Int, width: Int, indexOffset: Int, xOffset: Int, yOffset: Int) {
        for (column in IntRange(0, height - 1)) {
            for (row in IntRange(0, width - 1)) {
                addSlot(Slot(inventory, indexOffset + row + column * 5, xOffset + row * 18, yOffset + column * 18))
            }
        }
    }

    override fun quickMoveStack(p0: Player, p1: Int): ItemStack {
        return ItemStack.EMPTY
    }

    override fun stillValid(p0: Player): Boolean {
        return true
    }
}