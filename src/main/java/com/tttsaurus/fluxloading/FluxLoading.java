package com.tttsaurus.fluxloading;

import com.tttsaurus.fluxloading.proxy.CommonProxy;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.apache.logging.log4j.Logger;

@Mod(
        modid = Tags.MODID,
        version = Tags.VERSION,
        name = Tags.MODNAME,
        acceptedMinecraftVersions = "[1.12.2]",
        dependencies = "required-after:mixinbooter@[10.0,)")
public class FluxLoading
{
    public static Logger logger;

    @SidedProxy(
            clientSide = "com.tttsaurus.fluxloading.proxy.ClientProxy",
            serverSide = "com.tttsaurus.fluxloading.proxy.ServerProxy")
    public static CommonProxy proxy;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        logger = event.getModLog();
        proxy.preInit(event, logger);
    }
    @EventHandler
    public void init(FMLInitializationEvent event)
    {
        proxy.init(event, logger);
        logger.info("Flux Loading initialized.");
    }
}
