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
                            output.accept(new ItemStack(Items.AIR, 1));
                            output.accept(new ItemStack(Items.AIR, 1));
                            output.accept(new ItemStack(Items.AIR, 1));
                            output.accept(new ItemStack(Items.HEART_OF_THE_SEA));
                            output.accept(new ItemStack(Items.AIR, 1));
                            output.accept(new ItemStack(Items.LAPIS_LAZULI));
                            output.accept(new ItemStack(Items.LEATHER));
                            output.accept(new ItemStack(Items.AIR, 1));
                            output.accept(new ItemStack(Items.MAGMA_CREAM));
                        })
                        .build()
        ));
    }
}







