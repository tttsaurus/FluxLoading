package com.tttsaurus.fluxloading.mixin.early;

import com.tttsaurus.fluxloading.core.WorldLoadingScreenOverhaul;
import net.minecraft.client.renderer.chunk.ChunkCompileTaskGenerator;
import net.minecraft.client.renderer.chunk.ChunkRenderWorker;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ChunkRenderWorker.class)
public class ChunkRenderWorkerMixin
{
    // won't be called when nothirium is installed
    @Inject(method = "processTask", at = @At("RETURN"))
    public void processTask(ChunkCompileTaskGenerator generator, CallbackInfo ci)
    {
        // render chunk

        if (WorldLoadingScreenOverhaul.getCountingChunkLoaded())
        {
            WorldLoadingScreenOverhaul.incrChunkLoadedNum();

            if (WorldLoadingScreenOverhaul.getChunkLoadedNum() >= WorldLoadingScreenOverhaul.getTargetChunkNum())
            {
                WorldLoadingScreenOverhaul.setCountingChunkLoaded(false);
                WorldLoadingScreenOverhaul.resetChunkLoadedNum();
                WorldLoadingScreenOverhaul.resetTargetChunkNum();
                WorldLoadingScreenOverhaul.setFinishedLoadingChunks(true);
                WorldLoadingScreenOverhaul.startFadeOutTimer();
            }
        }
    }
}
