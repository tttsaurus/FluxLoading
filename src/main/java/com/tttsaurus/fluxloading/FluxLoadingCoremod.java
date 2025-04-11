package com.tttsaurus.fluxloading;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

import com.falsepattern.gasstation.IEarlyMixinLoader;

import cpw.mods.fml.relauncher.IFMLLoadingPlugin;

@IFMLLoadingPlugin.Name("FluxLoadingCore")
@IFMLLoadingPlugin.MCVersion("1.7.10")
public class FluxLoadingCoremod implements IFMLLoadingPlugin, IEarlyMixinLoader {

    @Override
    public String[] getASMTransformerClass() {
        return new String[0];
    }

    @Override
    public String getModContainerClass() {
        return null;
    }

    @Override
    public @Nullable String getSetupClass() {
        return null;
    }

    @Override
    public void injectData(Map<String, Object> data) {

    }

    @Override
    public String getAccessTransformerClass() {
        return null;
    }

    @Override
    public List<String> getMixinConfigs() {
        List<String> list = new ArrayList<>();

        list.add("mixins.fluxloading.json");

        return list;
    }
}
