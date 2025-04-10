package com.tttsaurus.fluxloading.proxy;

import org.apache.logging.log4j.Logger;

import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;

public class CommonProxy {

    public void preInit(FMLPreInitializationEvent event, Logger logger) {

    }

    public void init(FMLInitializationEvent event, Logger logger) {
        logger.info("Flux Loading starts initializing.");
    }
}
