package com.gmail.vincent031525.escapezone.mixin

import com.gmail.vincent031525.escapezone.EscapeZone
import com.gmail.vincent031525.escapezone.GridSlot
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
    private val hotbarGrid = GridSlot(1, 9)

    @Unique
    private val itemsGrid = GridSlot(3, 9)

    @Shadow
    lateinit var items: NonNullList<ItemStack>

    @Shadow
    lateinit var armor: NonNullList<ItemStack>

    @Shadow
    lateinit var offhand: NonNullList<ItemStack>

    @Shadow
    lateinit var player: Player

    @Unique
    private fun getItemCollectible(itemStack: ItemStack): Collectible {
        return itemStack.getOrDefault(ModDataComponents.collectible, Collectible())
    }

    @Unique
    private fun getGridSlotIsEmpty(index: Int, collectible: Collectible): Boolean {
        if (index < 9) {
            return hotbarGrid.isEmpty(index, collectible.height, collectible.width)
        }
        return itemsGrid.isEmpty(index - 9, collectible.height, collectible.width)
    }

    @Unique
    private fun setGridSlot(index: Int, itemStack: ItemStack, itemHeight: Int, itemWidth: Int) {
        if (index < 9) {
            hotbarGrid.setItem(index, itemStack, itemHeight, itemWidth)
        } else {
            itemsGrid.setItem(index - 9, itemStack, itemHeight, itemWidth)
        }
    }

    @Unique
    private fun removeGridSlot(index: Int, itemHeight: Int, itemWidth: Int) {
        if (index < 9) {
            hotbarGrid.setItem(index, ItemStack.EMPTY, itemHeight, itemWidth)
        } else {
            itemsGrid.setItem(index - 9, ItemStack.EMPTY, itemHeight, itemWidth)
        }
    }

    @Unique
    private fun getFreeGridSlot(itemHeight: Int, itemWidth: Int): Int {
        val slot = hotbarGrid.getFreeSlot(itemHeight, itemWidth)
        if (slot != -1) return slot
        return itemsGrid.getFreeSlot(itemHeight, itemWidth) + 9
    }

    @Unique
    private fun clearGridSlot() {
        hotbarGrid.clear()
        itemsGrid.clear()
    }

    @Unique
    private fun isAllEmptyGridSlot(): Boolean {
        return hotbarGrid.isAllEmpty() && itemsGrid.isAllEmpty()
    }

    @Shadow
    abstract fun addResource(itemStack: ItemStack): Int

    @Inject(method = ["addResource(Lnet/minecraft/world/item/ItemStack;)I"], at = [At("HEAD")], cancellable = true)
    private fun injectAddResource(stack: ItemStack, cir: CallbackInfoReturnable<Int>) {
        var i = getSlotWithRemainingSpace(stack)
        EscapeZone.LOGGER.info(i)
        val collectible = getItemCollectible(stack)
        if (i == -1) i = getFreeGridSlot(collectible.height, collectible.width)
        EscapeZone.LOGGER.info(i)

        cir.returnValue = if (i == -1) stack.count else this.addResource(i, stack)
    }

    @Shadow
    abstract fun addResource(slot: Int, itemStack: ItemStack): Int

    @Inject(method = ["addResource(ILnet/minecraft/world/item/ItemStack;)I"], at = [At("HEAD")], cancellable = true)
    private fun injectAddResource(slot: Int, stack: ItemStack, cir: CallbackInfoReturnable<Int>) {
        cir.cancel()

        var i = stack.count
        var itemstack = getItem(slot)
        val collectible = getItemCollectible(itemstack)
        if (getGridSlotIsEmpty(slot, collectible)) {
            itemstack = stack.copyWithCount(0)
            setItem(slot, itemstack)
            setGridSlot(slot, itemstack, collectible.height, collectible.width)
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
                    val collectible = getItemCollectible(stack)
                    if (slot == -1) {
                        slot = getFreeGridSlot(collectible.height, collectible.width)
                    }

                    if (slot >= 0) {
                        val itemStack = stack.copyAndClear()
                        items[slot] = itemStack
                        items[slot].popTime = 5
                        setGridSlot(slot, itemStack, collectible.height, collectible.width)
                        true
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
            val collectible = getItemCollectible(itemStack)
            removeGridSlot(index, collectible.height, collectible.width)
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
                val collectible = getItemCollectible(stack)
                removeGridSlot(i, collectible.height, collectible.width)
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
            val collectible = getItemCollectible(items[index])
            removeGridSlot(index, collectible.height, collectible.width)
            items[index] = ItemStack.EMPTY
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
            items[index] = stack
            val collectible = getItemCollectible(stack)
            setGridSlot(index, stack, collectible.height, collectible.width)
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
        clearGridSlot()

        for (i in listTag.indices) {
            val compoundtag = listTag.getCompound(i)
            val j = compoundtag.getByte("Slot").toInt() and 255
            val itemstack = ItemStack.parse(player.registryAccess(), compoundtag).orElse(ItemStack.EMPTY) as ItemStack
            when {
                j >= 0 && j < items.size -> {
                    items[j] = itemstack
                    val collectible = getItemCollectible(itemstack)
                    setGridSlot(j, itemstack, collectible.height, collectible.width)
                }

                j >= 100 && j < armor.size + 100 -> armor[j - 100] = itemstack
                j >= 150 && j < offhand.size + 150 -> offhand[j - 150] = itemstack
            }
        }
    }

    @Inject(method = ["isEmpty"], at = [At("HEAD")], cancellable = true)
    fun isEmpty(cir: CallbackInfoReturnable<Boolean>) {
        cir.cancel()

        if (!isAllEmptyGridSlot()) {
            cir.returnValue = false
            return
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
                val collectible = getItemCollectible(itemstack)
                removeGridSlot(i, collectible.height, collectible.width)
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