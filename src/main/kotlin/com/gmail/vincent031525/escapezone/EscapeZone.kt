package com.gmail.vincent031525.escapezone

import com.gmail.vincent031525.escapezone.component.ModDataComponents
import com.gmail.vincent031525.escapezone.event.ModClientEvent
import com.gmail.vincent031525.escapezone.event.ModEvents
import com.gmail.vincent031525.escapezone.menu.ModMenuTypes
import net.minecraft.client.Minecraft
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.fml.common.Mod
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent
import net.neoforged.fml.event.lifecycle.FMLDedicatedServerSetupEvent
import org.apache.logging.log4j.Level
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import thedarkcolour.kotlinforforge.neoforge.forge.MOD_BUS
import thedarkcolour.kotlinforforge.neoforge.forge.runForDist

@Mod(EscapeZone.ID)
@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD)
object EscapeZone {
    const val ID = "escapezone"

    val LOGGER: Logger = LogManager.getLogger(ID)

    init {
        LOGGER.log(Level.INFO, "Hello world!")

        ModDataComponents.register(MOD_BUS)
        ModMenuTypes.menus.register(MOD_BUS)

        MOD_BUS.register(ModEvents)
        MOD_BUS.register(ModClientEvent)

        val obj = runForDist(clientTarget = {
            MOD_BUS.addListener(::onClientSetup)
            Minecraft.getInstance()
        }, serverTarget = {
            MOD_BUS.addListener(::onServerSetup)
            "test"
        })

        println(obj)
    }

    private fun onClientSetup(event: FMLClientSetupEvent) {
        LOGGER.log(Level.INFO, "Initializing client...")
    }

    private fun onServerSetup(event: FMLDedicatedServerSetupEvent) {
        LOGGER.log(Level.INFO, "Server starting...")
    }

    @SubscribeEvent
    fun onCommonSetup(event: FMLCommonSetupEvent) {
        LOGGER.log(Level.INFO, "Hello! This is working!")
    }
}
