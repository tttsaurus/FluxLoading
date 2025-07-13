package com.tttsaurus.fluxloading.proxy;

import com.tttsaurus.fluxloading.FluxLoading;
import com.tttsaurus.fluxloading.FluxLoadingConfig;
import com.tttsaurus.fluxloading.core.FluxLoadingManager;
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

        FluxLoadingManager.setDisableVanillaTexts(FluxLoadingConfig.DISABLE_ALL_VANILLA_LOADING_TEXTS);
        FluxLoadingManager.setChunkLoadingTitle(FluxLoadingConfig.CHUNK_LOADING_INDICATOR);
        FluxLoadingManager.setChunkLoadingPercentage(FluxLoadingConfig.CHUNK_LOADING_PERCENTAGE);
        FluxLoadingManager.setChunkRayCastTestRayDis(FluxLoadingConfig.CHUNK_ESTIMATION_RAY_DISTANCE);
        FluxLoadingManager.setWaitChunksToLoad(FluxLoadingConfig.WAIT_CHUNKS_TO_LOAD);
        FluxLoadingManager.setExtraWaitTime(FluxLoadingConfig.EXTRA_WAIT_TIME);
        FluxLoadingManager.setFadeOutDuration(FluxLoadingConfig.FADE_OUT_DURATION);
        FluxLoadingManager.setFadeInDuration(FluxLoadingConfig.FADE_IN_DURATION);
        FluxLoadingManager.setDebug(FluxLoadingConfig.DEBUG);

        if (FluxLoading.IS_INGAMEINFO_REBORN_LOADED && FluxLoadingConfig.ENABLE_IGI_INTEGRATION)
        {
            try
            {
                MinecraftForge.EVENT_BUS.register(Class.forName("com.tttsaurus.fluxloading.plugin.igi.IgiEntryPoint"));
            }
            catch (Throwable ignored) { }
        }
    }

    @Override
    public void init(FMLInitializationEvent event, Logger logger)
    {
        super.init(event, logger);

        MinecraftForge.EVENT_BUS.register(FluxLoadingManager.class);
    }
}
