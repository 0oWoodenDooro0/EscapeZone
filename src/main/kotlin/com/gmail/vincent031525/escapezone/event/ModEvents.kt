package com.gmail.vincent031525.escapezone.event

import com.gmail.vincent031525.escapezone.EscapeZone
import com.gmail.vincent031525.escapezone.component.Collectible
import com.gmail.vincent031525.escapezone.component.ModDataComponents
import com.gmail.vincent031525.escapezone.datagen.ModLanguageProvider
import com.gmail.vincent031525.escapezone.keymapping.ModKeyMappings
import com.gmail.vincent031525.escapezone.menu.ModMenuTypes
import com.gmail.vincent031525.escapezone.screen.InventoryScreen
import net.minecraft.core.component.DataComponents
import net.minecraft.world.item.Items
import net.neoforged.api.distmarker.Dist
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent
import net.neoforged.neoforge.data.event.GatherDataEvent
import net.neoforged.neoforge.event.ModifyDefaultComponentsEvent

@EventBusSubscriber(modid = EscapeZone.ID, bus = EventBusSubscriber.Bus.MOD)
object ModEvents {
    @SubscribeEvent
    fun addDataComponents(event: ModifyDefaultComponentsEvent) {
        val qualityMap = mapOf(
            Items.AMETHYST_SHARD to Collectible(0, 1, 1, 1),
            Items.BLAZE_ROD to Collectible(1, 1, 2, 1),
            Items.BONE to Collectible(0, 1, 2, 1),
            Items.BREEZE_ROD to Collectible(1, 1, 2, 1),
            Items.COAL to Collectible(0, 1, 1, 1),
            Items.DIAMOND to Collectible(1, 1, 1, 1),
            Items.DRAGON_BREATH to Collectible(2, 1, 1, 1),
            Items.ECHO_SHARD to Collectible(1, 1, 1, 1),
            Items.EGG to Collectible(0, 1, 1, 1),
            Items.EMERALD to Collectible(1, 1, 1, 1),
            Items.ENDER_EYE to Collectible(0, 1, 1, 1),
            Items.HEART_OF_THE_SEA to Collectible(2, 2, 2, 1),
            Items.LAPIS_LAZULI to Collectible(0, 1, 1, 1),
            Items.LEATHER to Collectible(0, 2, 2, 1),
            Items.MAGMA_CREAM to Collectible(0, 1, 1, 1),
            Items.NAUTILUS_SHELL to Collectible(1, 1, 1, 1),
            Items.NETHER_STAR to Collectible(3, 1, 1, 1),
            Items.PHANTOM_MEMBRANE to Collectible(0, 2, 2, 1),
            Items.RESIN_CLUMP to Collectible(1, 1, 1, 1),
            Items.SHULKER_SHELL to Collectible(1, 2, 2, 1),
            Items.SLIME_BALL to Collectible(0, 1, 1, 1),
            Items.STICK to Collectible(0, 1, 2, 1),
            Items.STRING to Collectible(0, 2, 1, 1),
            Items.TURTLE_SCUTE to Collectible(0, 2, 1, 1),
            Items.NETHERITE_INGOT to Collectible(3, 3, 2, 1),
            Items.NETHERITE_SCRAP to Collectible(2, 2, 2, 1),
            Items.NETHERITE_UPGRADE_SMITHING_TEMPLATE to Collectible(2, 1, 1, 1),
            Items.IRON_HORSE_ARMOR to Collectible(0, 3, 2, 1),
            Items.GOLDEN_HORSE_ARMOR to Collectible(1, 3, 2, 1),
            Items.DIAMOND_HORSE_ARMOR to Collectible(1, 3, 2, 1),
            Items.CLOCK to Collectible(0, 2, 2, 1),
            Items.COMPASS to Collectible(0, 2, 2, 1),
            Items.NAME_TAG to Collectible(0, 1, 1, 1),
            Items.SPYGLASS to Collectible(0, 1, 2, 1),
            Items.RECOVERY_COMPASS to Collectible(1, 2, 2, 1),
            Items.MUSIC_DISC_11 to Collectible(0, 2, 2, 1),
            Items.MUSIC_DISC_5 to Collectible(1, 2, 2, 1),
            Items.END_CRYSTAL to Collectible(1, 2, 2, 1),
            Items.SADDLE to Collectible(0, 2, 1, 1),
            Items.TRIAL_KEY to Collectible(1, 1, 1, 1),
            Items.OMINOUS_TRIAL_KEY to Collectible(2, 1, 1, 1),
            Items.HEAVY_CORE to Collectible(3, 3, 3, 1),
            Items.DRAGON_HEAD to Collectible(3, 3, 2, 1),
            Items.DRAGON_EGG to Collectible(3, 2, 2, 1),
        )
        qualityMap.forEach { (item, collectible) ->
            event.modify(item) { builder ->
                builder[ModDataComponents.collectible.get()] = collectible
                if (collectible.maxStack != -1) {
                    builder[DataComponents.MAX_STACK_SIZE] = collectible.maxStack
                }
            }
        }
    }
    @SubscribeEvent
    fun registerScreens(event: RegisterMenuScreensEvent) {
        event.register(ModMenuTypes.inventoryMenu.value(), ::InventoryScreen)
    }
}

@EventBusSubscriber(modid = EscapeZone.ID, bus = EventBusSubscriber.Bus.MOD, value = [Dist.CLIENT])
object ModClientEvent {
    @SubscribeEvent
    fun registerBindings(event: RegisterKeyMappingsEvent) {
        ModKeyMappings.register(event)
    }

    @SubscribeEvent
    fun registerDataGen(event: GatherDataEvent.Client) {
        event.createProvider { output -> ModLanguageProvider(output) }
    }
}