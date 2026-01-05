package com.tttsaurus.fluxloading.mixin.early;

import com.tttsaurus.fluxloading.core.FluxLoadingAPI;
import com.tttsaurus.fluxloading.core.chunk.gate.FluxLoadingChunkSource;
import com.tttsaurus.fluxloading.core.FluxLoadingManager;
import net.minecraft.client.renderer.chunk.ChunkCompileTaskGenerator;
import net.minecraft.client.renderer.chunk.ChunkRenderWorker;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ChunkRenderWorker.class)
public class ChunkRenderWorkerMixin
{
    // won't be called when nothirium/celeritas is installed
    @Inject(method = "processTask", at = @At("RETURN"))
    public void processTask(ChunkCompileTaskGenerator generator, CallbackInfo ci)
    {
        if (FluxLoadingAPI.isActive())
            FluxLoadingManager.onChunkCompileTaskProcessed(FluxLoadingChunkSource.VANILLA);
    }
}
