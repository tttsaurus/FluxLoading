package com.tttsaurus.fluxloading.mixin.late.waila;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.tttsaurus.fluxloading.core.FluxLoadingAPI;
import mcp.mobius.waila.overlay.WailaTickHandler;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(WailaTickHandler.class)
public class WailaTickHandlerMixin
{
    @WrapMethod(method = "renderOverlay", remap = false)
    private static void renderOverlay(TickEvent.RenderTickEvent event, Operation<Void> original)
    {
        if (FluxLoadingAPI.isActive() && !FluxLoadingAPI.isFinishLoading()) return;

        original.call(event);
    }
}
