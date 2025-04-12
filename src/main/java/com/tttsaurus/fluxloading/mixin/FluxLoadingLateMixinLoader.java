package com.tttsaurus.fluxloading.mixin;

import com.gtnewhorizon.gtnhmixins.ILateMixinLoader;
import com.gtnewhorizon.gtnhmixins.LateMixin;
import cpw.mods.fml.common.Loader;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@LateMixin
public class FluxLoadingLateMixinLoader implements ILateMixinLoader {
    @Override
    public String getMixinConfig() {
        return "mixins.fluxloading.late.json";
    }

    @Override
    public List<String> getMixins(Set<String> loadedMods) {
        List<String> mixins = new ArrayList<>();
        if (Loader.isModLoaded("embeddium")) {
            mixins.add("MixinSodiumWorldRenderer");
        }
        if (Loader.isModLoaded("loading_screen_messages")) {
            mixins.add("MixinLoadingScreen_LoadingScreenMessages");
        }
        if (Loader.isModLoaded("aether_legacy")) {
            mixins.add("MixinAetherLoadingScreen");
        }
        return mixins;
    }
}
