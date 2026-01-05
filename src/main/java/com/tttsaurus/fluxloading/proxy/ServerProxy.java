package com.tttsaurus.fluxloading.proxy;

import com.tttsaurus.fluxloading.core.listener.FluxLoadingServerTickListener;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.apache.logging.log4j.Logger;

public class ServerProxy extends CommonProxy
{
    @Override
    public void preInit(FMLPreInitializationEvent event, Logger logger)
    {
        super.preInit(event, logger);
    }

    @Override
    public void init(FMLInitializationEvent event, Logger logger)
    {
        super.init(event, logger);

        // server event listeners
        MinecraftForge.EVENT_BUS.register(FluxLoadingServerTickListener.class);
    }
}
