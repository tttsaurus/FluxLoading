package com.tttsaurus.fluxloading.mixin.late.celeritas;

import com.tttsaurus.fluxloading.FluxLoading;
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
        if (FluxLoadingManager.isCountingChunkLoaded())
        {
            FluxLoading.logger.info("Chunk loading stage entry point: Celeritas");
            FluxLoading.logger.info("Chunk loading stage: Celeritas finished chunk loading");

            FluxLoadingManager.setCountingChunkLoaded(false);
            FluxLoadingManager.setFinishChunkLoading(true);
            FluxLoadingManager.startFadeOutTimer();
        }
    }
}
