package com.gmail.vincent031525.escapezone.mixin;

import com.google.common.collect.Multimap;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.neoforged.neoforge.common.CreativeModeTabRegistry;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

@Mixin(CreativeModeTabRegistry.class)
public abstract class MixinCreativeModeTabRegistry {

    @Shadow @Final private static Multimap<ResourceLocation, ResourceLocation> edges;

    @Shadow @Final private static List<CreativeModeTab> DEFAULT_TABS;

    @Shadow
    private static void addTabOrder(CreativeModeTab tab, ResourceLocation name) {
    }

    @Shadow
    private static void recalculateItemCreativeModeTabs() {
    }

    @Inject(method = {"sortTabs"}, at = {@At("HEAD")}, cancellable = true)
    private static void sortTabs(CallbackInfo ci) {
        ci.cancel();

        edges.clear();
        List<Holder<CreativeModeTab>> indexed = new ArrayList<>();
        Stream<Holder.Reference<CreativeModeTab>> var10000 = BuiltInRegistries.CREATIVE_MODE_TAB.listElements().filter(c -> !DEFAULT_TABS.contains(c.value()));
        Objects.requireNonNull(indexed);
        var10000.forEach(indexed::add);
        int vanillaTabs = 1;
        for(int i = 0; i < vanillaTabs; ++i) {
            Holder<CreativeModeTab> value = indexed.get(i);
            CreativeModeTab tab = value.value();
            ResourceLocation name = (value.unwrapKey().orElseThrow()).location();
            if (tab.tabsBefore.isEmpty() && tab.tabsAfter.isEmpty()) {

                if (i + 1 < indexed.size()) {
                    edges.put(name, (indexed.get(i + 1)).unwrapKey().orElseThrow().location());
                }
            } else {
                addTabOrder(tab, name);
            }
        }
        recalculateItemCreativeModeTabs();
    }
}
