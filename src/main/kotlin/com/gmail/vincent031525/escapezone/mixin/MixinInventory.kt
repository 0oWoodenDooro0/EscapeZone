package com.gmail.vincent031525.escapezone.mixin

import com.gmail.vincent031525.escapezone.EscapeZone
import com.gmail.vincent031525.escapezone.component.Collectible
import com.gmail.vincent031525.escapezone.component.ModDataComponents
import net.minecraft.CrashReport
import net.minecraft.ReportedException
import net.minecraft.core.NonNullList
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.nbt.ListTag
import net.minecraft.world.Container
import net.minecraft.world.ContainerHelper
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.Shadow
import org.spongepowered.asm.mixin.Unique
import org.spongepowered.asm.mixin.injection.At
import org.spongepowered.asm.mixin.injection.Inject
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
import kotlin.math.min

@Mixin(Inventory::class)
abstract class MixinInventory : Container {

    @Unique
    private val inventoryWidth = 9

    @Unique
    private val inventoryHeight = 3

    @Unique
    private lateinit var itemsGrid: NonNullList<NonNullList<ItemStack>>

    @Unique
    private val hotbar: NonNullList<ItemStack> = NonNullList.withSize(inventoryWidth, ItemStack.EMPTY)

    @Shadow
    lateinit var items: NonNullList<ItemStack>

    @Shadow
    lateinit var armor: NonNullList<ItemStack>

    @Shadow
    lateinit var offhand: NonNullList<ItemStack>

    @Shadow
    lateinit var player: Player

    @Inject(method = ["<init>(Lnet/minecraft/world/entity/player/Player;)V"], at = [At("RETURN")])
    private fun InventoryConstructed(player: Player, ci: CallbackInfo) {
        itemsGrid = NonNullList.create()
        for (i in 0..<this.inventoryHeight) {
            itemsGrid.add(NonNullList.withSize(this.inventoryWidth, ItemStack.EMPTY))
        }
    }

    @Unique
    private fun getItemCollectible(itemStack: ItemStack): Collectible {
        return itemStack.getOrDefault(ModDataComponents.collectible, Collectible())
    }

    @Unique
    private fun changeItemInGrid(index: Int, itemStack: ItemStack): Boolean {
        val collectible = getItemCollectible(itemStack)
        if (index < 9) {
            if (collectible.quality == -1) {
                hotbar[index] = itemStack
                EscapeZone.LOGGER.info(itemsGrid)
                return true
            }
            return false
        }
        if (!getItemIsEmptyInGrid(index, collectible)) return false
        val index = index - 9
        for (height in 0..<collectible.height) {
            for (width in 0..<collectible.width) {
                itemsGrid[index / inventoryWidth + height][index % inventoryWidth + width] = itemStack
            }
        }
        EscapeZone.LOGGER.info(itemsGrid)
        return true
    }

    @Unique
    private fun getFreeHotBarSlot(): Int {
        for (i in hotbar.indices) {
            if (hotbar[i].isEmpty) {
                return i
            }
        }
        return -1
    }

    @Unique
    private fun getFreeItemSlot(collectible: Collectible): Int {
        for (column in 0..<inventoryHeight - collectible.height + 1) {
            for (row in 0..<inventoryWidth - collectible.width + 1) {
                if (getItemIsEmptyInGrid(column * inventoryWidth + row + 9, collectible)) {
                    return column * inventoryWidth + row + 9
                }
            }
        }
        return -1
    }

    @Unique
    private fun getItemIsEmptyInGrid(index: Int, collectible: Collectible): Boolean {
        if (index < 9 && collectible.quality == -1) {
            return hotbar[index].isEmpty
        }
        val index = index - 9
        for (height in 0..<collectible.height) {
            for (width in 0..<collectible.width) {
                if (!itemsGrid[index / inventoryWidth + height][index % inventoryWidth + width].isEmpty) return false
            }
        }
        return true
    }

    @Inject(method = ["getFreeSlot"], at = [At("HEAD")], cancellable = true)
    fun injectGetFreeSlot(cir: CallbackInfoReturnable<Int>) {
        cir.cancel()

        for (column in itemsGrid.indices) {
            for (row in itemsGrid[column].indices) {
                if (itemsGrid[column][row].isEmpty) {
                    cir.returnValue = column * inventoryWidth + row + 9
                    return
                }
            }

            cir.returnValue = -1
        }
    }

    @Shadow
    abstract fun addResource(itemStack: ItemStack): Int

    @Inject(method = ["addResource(Lnet/minecraft/world/item/ItemStack;)I"], at = [At("HEAD")], cancellable = true)
    private fun injectAddResource(stack: ItemStack, cir: CallbackInfoReturnable<Int>) {
        var i = getSlotWithRemainingSpace(stack)
        val collectible = getItemCollectible(stack)
        if (collectible.quality == -1) {
            i = getFreeHotBarSlot()
        }
        if (i == -1) {
            i = getFreeItemSlot(collectible)
        }

        cir.returnValue = if (i == -1) stack.count else this.addResource(i, stack)
    }

    @Shadow
    abstract fun addResource(slot: Int, itemStack: ItemStack): Int

    @Inject(method = ["addResource(ILnet/minecraft/world/item/ItemStack;)I"], at = [At("HEAD")], cancellable = true)
    private fun injectAddResource(slot: Int, stack: ItemStack, cir: CallbackInfoReturnable<Int>) {
        cir.cancel()

        var i = stack.count
        var itemstack = getItem(slot)
        if (getItemIsEmptyInGrid(slot, getItemCollectible(itemstack))) {
            itemstack = stack.copyWithCount(0)
            if (changeItemInGrid(slot, itemstack)) setItem(slot, itemstack)
        }

        val j = getMaxStackSize(itemstack) - itemstack.count
        val k = min(i.toDouble(), j.toDouble()).toInt()
        if (k == 0) {
            cir.returnValue = i
            return
        }
        i -= k
        itemstack.grow(k)
        itemstack.popTime = 5
        cir.returnValue = i
    }

    @Shadow
    abstract fun getSlotWithRemainingSpace(stack: ItemStack): Int

    @Inject(method = ["add(ILnet/minecraft/world/item/ItemStack;)Z"], at = [At("HEAD")], cancellable = true)
    fun add(slot: Int, stack: ItemStack, cir: CallbackInfoReturnable<Boolean>) {
        cir.cancel()

        var slot = slot
        if (stack.isEmpty) {
            cir.returnValue = false
            return
        } else {
            try {
                cir.returnValue = if (stack.isDamaged) {
                    if (slot == -1) {
                        val collectible = getItemCollectible(stack)
                        slot = if (collectible.quality == -1) {
                            getFreeHotBarSlot()
                        } else {
                            getFreeItemSlot(collectible)
                        }
                    }

                    if (slot >= 0) {
                        if (changeItemInGrid(slot, stack.copyAndClear())) {
                            items[slot] = stack.copyAndClear()
                            items[slot].popTime = 5
                            true
                        } else {
                            throw Exception("No space in inventory")
                        }
                    } else if (player.hasInfiniteMaterials()) {
                        stack.count = 0
                        true
                    } else {
                        false
                    }
                } else {
                    var i: Int
                    do {
                        i = stack.count
                        if (slot == -1) {
                            stack.count = addResource(stack)
                        } else {
                            stack.count = addResource(slot, stack)
                        }
                    } while (!stack.isEmpty && stack.count < i)

                    if (stack.count == i && player.hasInfiniteMaterials()) {
                        stack.count = 0
                        true
                    } else {
                        stack.count < i
                    }
                }
            } catch (throwable: Throwable) {
                val crashreport = CrashReport.forThrowable(throwable, "Adding item to inventory")
                val crashreportcategory = crashreport.addCategory("Item being added")
                crashreportcategory.setDetail(
                    "Registry Name"
                ) { BuiltInRegistries.ITEM.getKey(stack.item).toString() }
                crashreportcategory.setDetail("Item Class") { stack.item.javaClass.name }
                crashreportcategory.setDetail("Item ID", Item.getId(stack.item))
                crashreportcategory.setDetail("Item data", stack.damageValue)
                crashreportcategory.setDetail("Item name") { stack.hoverName.string }
                throw ReportedException(crashreport)
            }
        }
    }

    @Inject(method = ["removeItem"], at = [At("HEAD")], cancellable = true)
    fun removeItem(index: Int, count: Int, cir: CallbackInfoReturnable<ItemStack>) {
        cir.cancel()

        var index = index
        if (index < items.size) {
            if (items[index].isEmpty) {
                cir.returnValue = ItemStack.EMPTY
                return
            }
            val itemStack = ContainerHelper.removeItem(items, index, count)
            changeItemInGrid(index, itemStack)
            cir.returnValue = itemStack
            return
        }
        index -= items.size
        if (index < armor.size) {
            if (armor[index].isEmpty) {
                cir.returnValue = ItemStack.EMPTY
                return
            }
            val itemStack = ContainerHelper.removeItem(armor, index, count)
            cir.returnValue = itemStack
            return
        }
        index -= armor.size
        if (index < offhand.size) {
            if (offhand[index].isEmpty) {
                cir.returnValue = ItemStack.EMPTY
                return
            }
            val itemStack = ContainerHelper.removeItem(offhand, index, count)
            cir.returnValue = itemStack
            return
        }
        cir.returnValue = ItemStack.EMPTY
        return

    }

    @Inject(method = ["removeItem(Lnet/minecraft/world/item/ItemStack;)V"], at = [At("HEAD")], cancellable = true)
    fun removeItem(stack: ItemStack, ci: CallbackInfo) {
        ci.cancel()

        for (i in items.indices) {
            if (items[i] == stack) {
                items[i] = ItemStack.EMPTY
                changeItemInGrid(i, ItemStack.EMPTY)
                return
            }
        }
        for (i in armor.indices) {
            if (armor[i] == stack) {
                armor[i] = ItemStack.EMPTY
                return
            }
        }
        for (i in offhand.indices) {
            if (offhand[i] == stack) {
                offhand[i] = ItemStack.EMPTY
                return
            }
        }
    }

    @Inject(method = ["removeItemNoUpdate"], at = [At("HEAD")], cancellable = true)
    fun removeItemNoUpdate(index: Int, cir: CallbackInfoReturnable<ItemStack>) {
        cir.cancel()

        var index = index

        if (index < items.size) {
            if (items[index].isEmpty) {
                cir.returnValue = ItemStack.EMPTY
                return
            }
            items[index] = ItemStack.EMPTY
            changeItemInGrid(index, ItemStack.EMPTY)
            cir.returnValue = items[index]
            return
        }
        index -= items.size
        if (index < armor.size) {
            if (armor[index].isEmpty) {
                cir.returnValue = ItemStack.EMPTY
                return
            }
            armor[index] = ItemStack.EMPTY
            cir.returnValue = armor[index]
            return
        }
        index -= armor.size
        if (index < offhand.size) {
            if (offhand[index].isEmpty) {
                cir.returnValue = ItemStack.EMPTY
                return
            }
            offhand[index] = ItemStack.EMPTY
            cir.returnValue = offhand[index]
            return
        }
        cir.returnValue = ItemStack.EMPTY
        return
    }

    @Inject(method = ["setItem"], at = [At("HEAD")], cancellable = true)
    fun setItem(index: Int, stack: ItemStack, ci: CallbackInfo) {
        ci.cancel()

        var index = index

        if (index < items.size) {
            if (changeItemInGrid(index, stack)) items[index] = stack
            return
        }
        index -= items.size
        if (index < armor.size) {
            armor[index] = stack
            return
        }
        index -= armor.size
        if (index < offhand.size) {
            offhand[index] = stack
        }
    }

    @Inject(method = ["load"], at = [At("HEAD")], cancellable = true)
    fun load(listTag: ListTag, ci: CallbackInfo) {
        ci.cancel()

        items.clear()
        armor.clear()
        offhand.clear()
        for (row in itemsGrid) row.clear()


        for (i in listTag.indices) {
            val compoundtag = listTag.getCompound(i)
            val j = compoundtag.getByte("Slot").toInt() and 255
            val itemstack = ItemStack.parse(player.registryAccess(), compoundtag).orElse(ItemStack.EMPTY) as ItemStack
            when {
                j >= 0 && j < items.size -> {
                    items[j] = itemstack
                    changeItemInGrid(j, itemstack)
                }

                j >= 100 && j < armor.size + 100 -> armor[j - 100] = itemstack
                j >= 150 && j < offhand.size + 150 -> offhand[j - 150] = itemstack
            }
        }
    }

    @Inject(method = ["isEmpty"], at = [At("HEAD")], cancellable = true)
    fun isEmpty(cir: CallbackInfoReturnable<Boolean>) {
        cir.cancel()

        for (row in itemsGrid) {
            for (itemStack in row) {
                if (!itemStack.isEmpty) {
                    cir.returnValue = false
                    return
                }
            }
        }

        for (itemstack1 in armor) {
            if (!itemstack1.isEmpty) {
                cir.returnValue = false
                return
            }
        }

        for (itemstack2 in offhand) {
            if (!itemstack2.isEmpty) {
                cir.returnValue = false
                return
            }
        }

        cir.returnValue = true
        return
    }

    @Inject(method = ["dropAll"], at = [At("HEAD")], cancellable = true)
    fun dropAll(ci: CallbackInfo) {
        ci.cancel()

        for (i in items.indices) {
            val itemstack = items[i]
            if (!itemstack.isEmpty) {
                this.player.drop(itemstack, true, false)
                items[i] = ItemStack.EMPTY
                changeItemInGrid(i, ItemStack.EMPTY)
            }
        }

        for (i in armor.indices) {
            val itemstack = armor[i]
            if (!itemstack.isEmpty) {
                this.player.drop(itemstack, true, false)
                armor[i] = ItemStack.EMPTY
            }
        }

        for (i in offhand.indices) {
            val itemstack = offhand[i]
            if (!itemstack.isEmpty) {
                this.player.drop(itemstack, true, false)
                offhand[i] = ItemStack.EMPTY
            }
        }
    }
}