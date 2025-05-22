package com.gmail.vincent031525.escapezone.mixin;

import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(CreativeModeTabs.class)
public class MixinCreativeModeTabs {

    @Shadow
    @Final
    public static ResourceKey<CreativeModeTab> BUILDING_BLOCKS;

    @Inject(method = {"bootstrap"}, at = {@At("HEAD")}, cancellable = true)
    private static void bootstrap(Registry<CreativeModeTab> registry, CallbackInfoReturnable<CreativeModeTab> cir) {
        cir.cancel();

        cir.setReturnValue(Registry.register(
                registry,
                BUILDING_BLOCKS,
                CreativeModeTab.builder()
                        .title(Component.literal("Escape Zone Items"))
                        .icon(() -> new ItemStack(Items.NETHER_STAR))
                        .displayItems((params, output) -> {
                            output.accept(new ItemStack(Items.AMETHYST_SHARD));
                            output.accept(new ItemStack(Items.BLAZE_ROD));
                            output.accept(new ItemStack(Items.BONE));
                            output.accept(new ItemStack(Items.BREEZE_ROD));
                            output.accept(new ItemStack(Items.COAL));
                            output.accept(new ItemStack(Items.DIAMOND));
                            output.accept(new ItemStack(Items.DRAGON_BREATH));
                            output.accept(new ItemStack(Items.ECHO_SHARD));
                            output.accept(new ItemStack(Items.EGG));
                            output.accept(new ItemStack(Items.EMERALD));
                            output.accept(new ItemStack(Items.ENDER_EYE));
                            output.accept(new ItemStack(Items.HEART_OF_THE_SEA));
                            output.accept(new ItemStack(Items.LAPIS_LAZULI));
                            output.accept(new ItemStack(Items.LEATHER));
                            output.accept(new ItemStack(Items.MAGMA_CREAM));
                            output.accept(new ItemStack(Items.NAUTILUS_SHELL));
                            output.accept(new ItemStack(Items.NETHER_STAR));
                            output.accept(new ItemStack(Items.PHANTOM_MEMBRANE));
                            output.accept(new ItemStack(Items.RESIN_CLUMP));
                            output.accept(new ItemStack(Items.SHULKER_SHELL));
                            output.accept(new ItemStack(Items.SLIME_BALL));
                            output.accept(new ItemStack(Items.STICK));
                            output.accept(new ItemStack(Items.STRING));
                            output.accept(new ItemStack(Items.TURTLE_SCUTE));
                            output.accept(new ItemStack(Items.NETHERITE_INGOT));
                            output.accept(new ItemStack(Items.NETHERITE_SCRAP));
                            output.accept(new ItemStack(Items.NETHERITE_UPGRADE_SMITHING_TEMPLATE));
                            output.accept(new ItemStack(Items.IRON_HORSE_ARMOR));
                            output.accept(new ItemStack(Items.GOLDEN_HORSE_ARMOR));
                            output.accept(new ItemStack(Items.DIAMOND_HORSE_ARMOR));
                            output.accept(new ItemStack(Items.CLOCK));
                            output.accept(new ItemStack(Items.COMPASS));
                            output.accept(new ItemStack(Items.NAME_TAG));
                            output.accept(new ItemStack(Items.SPYGLASS));
                            output.accept(new ItemStack(Items.RECOVERY_COMPASS));
                            output.accept(new ItemStack(Items.MUSIC_DISC_11));
                            output.accept(new ItemStack(Items.MUSIC_DISC_5));
                            output.accept(new ItemStack(Items.END_CRYSTAL));
                            output.accept(new ItemStack(Items.SADDLE));
                            output.accept(new ItemStack(Items.TRIAL_KEY));
                            output.accept(new ItemStack(Items.OMINOUS_TRIAL_KEY));
                            output.accept(new ItemStack(Items.HEAVY_CORE));
                            output.accept(new ItemStack(Items.DRAGON_HEAD));
                            output.accept(new ItemStack(Items.DRAGON_EGG));
                        })
                        .build()
        ));
    }
}







