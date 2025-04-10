package com.tttsaurus.fluxloading.mixin;

import java.util.ArrayList;
import java.util.List;

import cpw.mods.fml.common.Loader;
import zone.rong.mixinbooter.ILateMixinLoader;

public class LateMixinConfig implements ILateMixinLoader {

    @Override
    public List<String> getMixinConfigs() {
        List<String> list = new ArrayList<>();

        if (Loader.isModLoaded("nothirium")) list.add("mixins.fluxloading.late.nothirium.json");

        return list;
    }
}
