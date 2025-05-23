package com.gmail.vincent031525.escapezone.menu

import com.gmail.vincent031525.escapezone.EscapeZone
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.inventory.MenuType
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension
import net.neoforged.neoforge.network.IContainerFactory
import net.neoforged.neoforge.registries.DeferredHolder
import net.neoforged.neoforge.registries.DeferredRegister

object ModMenuTypes {

    val menus: DeferredRegister<MenuType<*>> = DeferredRegister.create(BuiltInRegistries.MENU, EscapeZone.ID)

    val inventoryMenu: DeferredHolder<MenuType<*>, MenuType<InventoryMenu>> =
        registerMenuType("inventory", ::InventoryMenu)

    private fun <T : AbstractContainerMenu> registerMenuType(
        name: String,
        factory: IContainerFactory<T>
    ): DeferredHolder<MenuType<*>, MenuType<T>> = menus.register(name) { -> IMenuTypeExtension.create(factory) }
}