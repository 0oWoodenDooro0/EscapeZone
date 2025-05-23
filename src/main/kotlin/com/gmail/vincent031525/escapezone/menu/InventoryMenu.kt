package com.gmail.vincent031525.escapezone.menu

import net.minecraft.network.FriendlyByteBuf
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.item.ItemStack

class InventoryMenu(containerId: Int, inventory: Inventory, extraData: FriendlyByteBuf?) :
    AbstractContainerMenu(ModMenuTypes.inventoryMenu.value(), containerId) {
    override fun quickMoveStack(p0: Player, p1: Int): ItemStack {
        return ItemStack.EMPTY
    }

    override fun stillValid(p0: Player): Boolean {
        return false
    }
}