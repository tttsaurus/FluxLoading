package com.tttsaurus.fluxloading.proxy;

import org.apache.logging.log4j.Logger;

import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;

public class ServerProxy extends CommonProxy {

    @Override
    public void preInit(FMLPreInitializationEvent event, Logger logger) {
        super.preInit(event, logger);
    }

    @Override
    public void init(FMLInitializationEvent event, Logger logger) {
        super.init(event, logger);
    }
}
