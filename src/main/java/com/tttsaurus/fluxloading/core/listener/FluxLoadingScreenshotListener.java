package com.tttsaurus.fluxloading.core.listener;

import com.tttsaurus.fluxloading.core.FluxLoadingManager;

import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public final class FluxLoadingScreenshotListener
{
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onRenderWorldLast(RenderWorldLastEvent event)
    {
        FluxLoadingManager.captureScreenshotIfRequested();
    }
}
