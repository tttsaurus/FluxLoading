package com.tttsaurus.fluxloading;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.util.ResourceLocation;

import org.apache.logging.log4j.Logger;

import com.tttsaurus.fluxloading.proxy.CommonProxy;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;

@Mod(
    modid = "fluxloading",
    version = "0.0.1",
    name = "FluxLoading",
    acceptedMinecraftVersions = "[1.7.10]",
    dependencies = "required-after:unimixins@[0.1.18,)")
public class FluxLoading {

    public static Logger logger;
    public static final Map<String, ResourceLocation> screenshotCache = new HashMap<>();
    public static final ResourceLocation noThumbnailRl = new ResourceLocation("textures/misc/unknown_pack.png");

    @SidedProxy(
        clientSide = "com.tttsaurus.fluxloading.proxy.ClientProxy",
        serverSide = "com.tttsaurus.fluxloading.proxy.ServerProxy")
    public static CommonProxy proxy;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        logger = event.getModLog();
        proxy.preInit(event, logger);
    }

    @EventHandler
    public void init(FMLInitializationEvent event) {
        proxy.init(event, logger);
        logger.info("Flux Loading initialized.");
    }
}
