package com.tttsaurus.fluxloading.core.listener;

import com.tttsaurus.fluxloading.core.FluxLoadingManager;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public final class FluxLoadingClientTickListener
{
    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event)
    {
        if (event.phase != TickEvent.Phase.END) return;
        if (Minecraft.getMinecraft().player == null) return;

        FluxLoadingManager.applyClientTickLock();
    }
}
