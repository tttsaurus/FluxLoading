package com.tttsaurus.fluxloading.proxy;

import com.tttsaurus.fluxloading.FluxLoadingConfig;
import com.tttsaurus.fluxloading.core.WorldLoadingScreenOverhaul;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.apache.logging.log4j.Logger;

public class ClientProxy extends CommonProxy
{
    @Override
    public void preInit(FMLPreInitializationEvent event, Logger logger)
    {
        super.preInit(event, logger);

        FluxLoadingConfig.CONFIG = new Configuration(event.getSuggestedConfigurationFile());
        FluxLoadingConfig.loadConfig();

        WorldLoadingScreenOverhaul.setChunkBuildingTitle(FluxLoadingConfig.CHUNK_BUILDING_INDICATOR);
        WorldLoadingScreenOverhaul.setWaitChunksToLoad(FluxLoadingConfig.WAIT_CHUNKS_TO_LOAD);
        WorldLoadingScreenOverhaul.setExtraWaitTime(FluxLoadingConfig.EXTRA_WAIT_TIME);
        WorldLoadingScreenOverhaul.setFadeOutDuration(FluxLoadingConfig.FADE_OUT_DURATION);
    }
    @Override
    public void init(FMLInitializationEvent event, Logger logger)
    {
        super.init(event, logger);

        MinecraftForge.EVENT_BUS.register(WorldLoadingScreenOverhaul.class);
    }
}
