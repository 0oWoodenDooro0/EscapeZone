package com.gmail.vincent031525.escapezone.component

import com.gmail.vincent031525.escapezone.EscapeZone
import net.minecraft.core.component.DataComponentType
import net.minecraft.core.registries.Registries
import net.neoforged.bus.api.IEventBus
import net.neoforged.neoforge.registries.DeferredHolder
import net.neoforged.neoforge.registries.DeferredRegister

object ModDataComponents {
    private val dataComponentsRegistrar =
        DeferredRegister.createDataComponents(Registries.DATA_COMPONENT_TYPE, EscapeZone.ID)

    val collectible: DeferredHolder<DataComponentType<*>, DataComponentType<Collectible>> =
        dataComponentsRegistrar.registerComponentType("quality") { builder ->
            builder.persistent(Collectible.codec).networkSynchronized(Collectible.streamCodec)
        }

    fun register(eventBus: IEventBus) {
        dataComponentsRegistrar.register(eventBus)
    }
}