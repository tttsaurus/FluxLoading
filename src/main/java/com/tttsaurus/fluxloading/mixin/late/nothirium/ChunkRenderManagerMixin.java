package com.tttsaurus.fluxloading.mixin.late.nothirium;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.tttsaurus.fluxloading.core.WorldLoadingScreenOverhaul;
import meldexun.nothirium.api.renderer.chunk.IChunkRenderer;
import meldexun.nothirium.mc.renderer.ChunkRenderManager;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(ChunkRenderManager.class)
public class ChunkRenderManagerMixin
{
    @WrapMethod(method = "getRenderer", remap = false)
    private static IChunkRenderer<?> getRenderer(Operation<IChunkRenderer<?>> original)
    {
        // render chunk

        if (WorldLoadingScreenOverhaul.isCountingChunkLoaded())
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

        return original.call();
    }
}
