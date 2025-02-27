package com.tttsaurus.fluxloading.mixin;

import net.minecraftforge.fml.common.Loader;
import zone.rong.mixinbooter.ILateMixinLoader;
import java.util.ArrayList;
import java.util.List;

public class LateMixinConfig implements ILateMixinLoader
{
    @Override
    public List<String> getMixinConfigs()
    {
        List<String> list = new ArrayList<>();

        if (Loader.isModLoaded("nothirium"))
            list.add("mixins.fluxloading.late.nothirium.json");

        return list;
    }
}
