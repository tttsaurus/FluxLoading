package com.tttsaurus.fluxloading.proxy;

import com.tttsaurus.fluxloading.core.FluxLoadingManager;
import com.tttsaurus.fluxloading.core.network.FluxLoadingNetwork;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.apache.logging.log4j.Logger;

public class CommonProxy
{
    public void preInit(FMLPreInitializationEvent event, Logger logger)
    {

    }

    public void init(FMLInitializationEvent event, Logger logger)
    {
        logger.info("Flux Loading starts initializing.");

        FluxLoadingNetwork.init();

        // both client and server listeners
        MinecraftForge.EVENT_BUS.register(FluxLoadingManager.class);
    }
}
