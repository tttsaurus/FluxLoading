package com.tttsaurus.fluxloading.mixin.late.celeritas;

import com.tttsaurus.fluxloading.core.FluxLoadingAPI;
import com.tttsaurus.fluxloading.core.chunk.gate.FluxLoadingChunkSource;
import com.tttsaurus.fluxloading.core.FluxLoadingManager;
import org.embeddedt.embeddium.impl.render.chunk.RenderSectionManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(RenderSectionManager.class)
public class RenderSectionManagerMixin
{
    @Inject(method = "updateChunks", at = @At("RETURN"), remap = false)
    public void updateChunks(boolean updateImmediately, CallbackInfo ci)
    {
        if (FluxLoadingAPI.isActive())
            FluxLoadingManager.onChunkCompileTaskProcessed(FluxLoadingChunkSource.CELERITAS);
    }
}
