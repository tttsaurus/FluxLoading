package com.tttsaurus.fluxloading.mixin.late.nothirium;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.tttsaurus.fluxloading.core.FluxLoadingAPI;
import com.tttsaurus.fluxloading.core.chunk.FluxLoadingChunkSource;
import com.tttsaurus.fluxloading.core.FluxLoadingManager;
import meldexun.nothirium.api.renderer.chunk.IChunkRenderer;
import meldexun.nothirium.mc.renderer.ChunkRenderManager;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(ChunkRenderManager.class)
public class ChunkRenderManagerMixin
{
    @WrapMethod(method = "getRenderer", remap = false)
    private static IChunkRenderer<?> getRenderer(Operation<IChunkRenderer<?>> original)
    {
        if (FluxLoadingAPI.isActive())
            FluxLoadingManager.onChunkCompileTaskProcessed(FluxLoadingChunkSource.NOTHIRIUM);

        return original.call();
    }
}
