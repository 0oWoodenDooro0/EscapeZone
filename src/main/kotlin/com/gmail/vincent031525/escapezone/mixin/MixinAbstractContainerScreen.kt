package com.gmail.vincent031525.escapezone.mixin

import com.gmail.vincent031525.escapezone.component.Collectible
import com.gmail.vincent031525.escapezone.component.ModDataComponents
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.screens.Screen
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen
import net.minecraft.network.chat.Component
import net.minecraft.world.inventory.Slot
import net.minecraft.world.item.ItemStack
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions.FontContext
import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.Shadow
import org.spongepowered.asm.mixin.injection.At
import org.spongepowered.asm.mixin.injection.Inject
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo
import kotlin.math.min

@Mixin(AbstractContainerScreen::class)
abstract class MixinAbstractContainerScreen(title: Component) : Screen(title) {

    @Shadow
    private var draggingItem: ItemStack? = null

    @Inject(method = ["renderSlot"], at = [At("HEAD")], cancellable = true)
    protected fun renderSlot(guiGraphics: GuiGraphics, slot: Slot, ci: CallbackInfo) {
        ci.cancel()

        val i = slot.x
        val j = slot.y
        val itemStack = slot.item
        if (itemStack.isEmpty) return
        val collectible = itemStack.getOrDefault(ModDataComponents.collectible, Collectible())
        val maxX = i + collectible.width * 16 + (collectible.width - 1) * 2
        val maxY = j + collectible.height * 16 + (collectible.height - 1) * 2
        guiGraphics.pose().pushPose()
        guiGraphics.pose().translate(0.0, 0.0, 100.0)
        when (collectible.quality) {
            1 -> guiGraphics.fill(i, j, maxX, maxY, 0x55800080)
            2 -> guiGraphics.fill(i, j, maxX, maxY, 0x55FFD700)
            3 -> guiGraphics.fill(i, j, maxX, maxY, 0x55FF0000)
        }
        guiGraphics.pose().popPose()

        val scaleFactor = min(collectible.width, collectible.height).toFloat()
        val baseSize = 16f
        val slotCenterX = slot.x + baseSize * collectible.width / 2
        val slotCenterY = slot.y + baseSize * collectible.height / 2
        val xOffset = -(baseSize / 2f).toInt()
        val yOffset = -(baseSize / 2f).toInt()

        guiGraphics.pose().pushPose()
        guiGraphics.pose().translate(slotCenterX.toDouble(), slotCenterY.toDouble(), 150.0)
        guiGraphics.pose().scale(scaleFactor, scaleFactor, 1.0f)
        guiGraphics.renderItem(itemStack, xOffset, yOffset)
        guiGraphics.renderItemDecorations(this.font, itemStack, xOffset, yOffset, null)
        guiGraphics.pose().popPose()
    }

    @Inject(method = ["renderFloatingItem"], at = [At("HEAD")], cancellable = true)
    private fun renderFloatingItem(
        guiGraphics: GuiGraphics,
        stack: ItemStack,
        x: Int,
        y: Int,
        text: String?,
        ci: CallbackInfo
    ) {
        ci.cancel()

        val collectible = stack.getOrDefault(ModDataComponents.collectible, Collectible())
        val maxX = x + collectible.width * 16 + (collectible.width - 1) * 2
        val maxY = y + collectible.height * 16 + (collectible.height - 1) * 2

        guiGraphics.pose().pushPose()
        guiGraphics.pose().translate(0.0, 0.0, 232.0)
        when (collectible.quality) {
            1 -> guiGraphics.fill(x, y, maxX, maxY, 0x55800080)
            2 -> guiGraphics.fill(x, y, maxX, maxY, 0x55FFD700)
            3 -> guiGraphics.fill(x, y, maxX, maxY, 0x55FF0000)
        }
        guiGraphics.pose().popPose()

        val scaleFactor = min(collectible.width, collectible.height)
        val baseSize = 16f
        val slotCenterX = x + baseSize * collectible.width / 2
        val slotCenterY = y + baseSize * collectible.height / 2
        val xOffset = -(baseSize / 2f).toInt()
        val yOffset = -(baseSize / 2f).toInt()

        guiGraphics.pose().pushPose()
        guiGraphics.pose().translate(slotCenterX, slotCenterY, 235.0f)
        guiGraphics.pose().scale(scaleFactor.toFloat(), scaleFactor.toFloat(), 1.0f)
        guiGraphics.renderItem(stack, xOffset, yOffset)
        val font = IClientItemExtensions.of(stack).getFont(stack, FontContext.ITEM_COUNT)
        guiGraphics.renderItemDecorations(
            font ?: this.font,
            stack,
            xOffset,
            yOffset - (if (this.draggingItem!!.isEmpty) 0 else 8) / scaleFactor,
            text
        )
        guiGraphics.pose().popPose()
    }
}