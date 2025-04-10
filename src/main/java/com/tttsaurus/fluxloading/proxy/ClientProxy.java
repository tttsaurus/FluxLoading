package com.tttsaurus.fluxloading.proxy;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;

import org.apache.logging.log4j.Logger;

import com.tttsaurus.fluxloading.FluxLoadingConfig;
import com.tttsaurus.fluxloading.core.WorldLoadingScreenOverhaul;

import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;

public class ClientProxy extends CommonProxy {

    @Override
    public void preInit(FMLPreInitializationEvent event, Logger logger) {
        super.preInit(event, logger);

        FluxLoadingConfig.CONFIG = new Configuration(event.getSuggestedConfigurationFile());
        FluxLoadingConfig.loadConfig();

        WorldLoadingScreenOverhaul.setChunkBuildingTitle(FluxLoadingConfig.CHUNK_BUILDING_INDICATOR);
        WorldLoadingScreenOverhaul.setTargetChunkNumCoefficient(FluxLoadingConfig.WAIT_CHUNK_BUILD_COEFFICIENT);
        WorldLoadingScreenOverhaul.setExtraWaitTime(FluxLoadingConfig.EXTRA_WAIT_TIME);
        WorldLoadingScreenOverhaul.setFadeOutDuration(FluxLoadingConfig.FADE_OUT_DURATION);
    }

    @Override
    public void init(FMLInitializationEvent event, Logger logger) {
        super.init(event, logger);

        MinecraftForge.EVENT_BUS.register(new WorldLoadingScreenOverhaul());
    }
}
