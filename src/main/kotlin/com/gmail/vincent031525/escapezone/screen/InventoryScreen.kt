package com.gmail.vincent031525.escapezone.screen

import com.gmail.vincent031525.escapezone.EscapeZone
import com.gmail.vincent031525.escapezone.menu.InventoryMenu
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen
import net.minecraft.client.gui.screens.inventory.InventoryScreen
import net.minecraft.client.renderer.RenderType
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.player.Inventory

class InventoryScreen(menu: InventoryMenu, inventory: Inventory, title: Component) :
    AbstractContainerScreen<InventoryMenu>(menu, inventory, title) {

    private val inventoryLocation =
        ResourceLocation.fromNamespaceAndPath(EscapeZone.ID, "textures/gui/inventory.png")
    private var xMouse: Float = 0f
    private var yMouse: Float = 0f

    override fun render(guiGraphics: GuiGraphics, p1: Int, p2: Int, p3: Float) {
        super.render(guiGraphics, p1, p2, p3)
        xMouse = p1.toFloat()
        yMouse = p2.toFloat()
    }

    override fun renderBg(guiGraphics: GuiGraphics, p1: Float, p2: Int, p3: Int) {
        val i = leftPos
        val j = topPos
        guiGraphics.blit(
            RenderType::guiTextured,
            inventoryLocation,
            i,
            j,
            0.0F,
            0.0F,
            imageWidth,
            imageHeight,
            imageWidth,
            imageHeight
        )
        minecraft!!.player?.let {
            InventoryScreen.renderEntityInInventoryFollowsMouse(
                guiGraphics, i + 26, j + 8, i + 74, j + 78, 30, 0.0625F, xMouse, yMouse, it
            )
        }
    }

    override fun renderLabels(guiGraphics: GuiGraphics, mouseX: Int, mouseY: Int) {}
}