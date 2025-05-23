package com.gmail.vincent031525.escapezone.mixin

import com.gmail.vincent031525.escapezone.menu.ModMenuTypes
import com.gmail.vincent031525.escapezone.screen.InventoryScreen
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.screens.Screen
import net.minecraft.client.player.LocalPlayer
import net.minecraft.network.chat.Component
import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.Shadow
import org.spongepowered.asm.mixin.injection.At
import org.spongepowered.asm.mixin.injection.Inject
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo

@Mixin(Minecraft::class)
abstract class MixinMinecraft {
    @Shadow
    var player: LocalPlayer? = null

    @Shadow
    abstract fun setScreen(guiScreen: Screen?)

    @Inject(
        method = ["handleKeybinds"],
        at = [At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/tutorial/Tutorial;onOpenInventory()V", // Target this method call
            shift = At.Shift.AFTER
        )],
        cancellable = true
    )
    private fun handleKeybinds(ci: CallbackInfo) {
        player?.let { localPlayer ->
            val inventoryMenuType = ModMenuTypes.inventoryMenu.value()
            val windowId = localPlayer.inventoryMenu.containerId
            val inventory = localPlayer.inventory
            val inventoryMenu = inventoryMenuType.create(windowId, inventory)
            setScreen(InventoryScreen(inventoryMenu, inventory, Component.empty()))
            ci.cancel()
        }
    }
}