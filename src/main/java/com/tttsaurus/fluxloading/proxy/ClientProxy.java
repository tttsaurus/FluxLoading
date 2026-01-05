package com.tttsaurus.fluxloading.proxy;

import com.tttsaurus.fluxloading.FluxLoadingConfig;
import com.tttsaurus.fluxloading.core.FluxLoadingManager;
import com.tttsaurus.fluxloading.core.listener.FluxLoadingClientTickListener;
import com.tttsaurus.fluxloading.core.listener.FluxLoadingRenderListener;
import com.tttsaurus.fluxloading.core.listener.FluxLoadingScreenshotListener;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.Loader;
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

        FluxLoadingManager.setChunkLoadingTitle(FluxLoadingConfig.CHUNK_LOADING_INDICATOR);
        FluxLoadingManager.setChunkLoadingPercentage(FluxLoadingConfig.CHUNK_LOADING_PERCENTAGE);
        FluxLoadingManager.setChunkRayCastTestRayDis(FluxLoadingConfig.CHUNK_ESTIMATION_RAY_DISTANCE);

        if (Loader.isModLoaded("ingameinfo") && FluxLoadingConfig.ENABLE_IGI_INTEGRATION)
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

        // client event listeners
        MinecraftForge.EVENT_BUS.register(FluxLoadingClientTickListener.class);
        MinecraftForge.EVENT_BUS.register(FluxLoadingRenderListener.class);
        MinecraftForge.EVENT_BUS.register(FluxLoadingScreenshotListener.class);
    }
}
