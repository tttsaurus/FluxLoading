package com.tttsaurus.fluxloading.core.listener;

import com.tttsaurus.fluxloading.core.FluxLoadingManager;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public final class FluxLoadingRenderListener
{
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onRenderGameOverlay(RenderGameOverlayEvent.Post event)
    {
        if (event.getType() != RenderGameOverlayEvent.ElementType.ALL) return;

        FluxLoadingManager.renderAndTick();
    }
}
