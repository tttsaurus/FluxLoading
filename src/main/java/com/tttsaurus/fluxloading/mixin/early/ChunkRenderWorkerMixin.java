package com.tttsaurus.fluxloading.mixin.early;

import com.tttsaurus.fluxloading.FluxLoading;
import com.tttsaurus.fluxloading.core.FluxLoadingManager;
import com.tttsaurus.fluxloading.core.accessor.ChunkProviderClientAccessor;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ChunkProviderClient;
import net.minecraft.client.renderer.chunk.ChunkCompileTaskGenerator;
import net.minecraft.client.renderer.chunk.ChunkRenderWorker;
import net.minecraft.world.chunk.Chunk;
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
        if (FluxLoadingManager.isCountingChunkLoaded())
        {
            if (!FluxLoadingManager.isWaitChunksToLoad())
            {
                if (FluxLoadingManager.getChunkLoadedNum() >= 1)
                {
                    Minecraft.getMinecraft().addScheduledTask(() ->
                    {
                        FluxLoading.logger.info("Chunk Loading Stage - Not going to wait chunks to load");
                    });

                    FluxLoadingManager.setCountingChunkLoaded(false);
                    FluxLoadingManager.setFinishChunkLoading(true);
                    FluxLoadingManager.startFadeOutTimer();
                    return;
                }
            }
            else
            {
                ChunkProviderClient chunkProvider = Minecraft.getMinecraft().world.getChunkProvider();
                Long2ObjectMap<Chunk> loadedChunks = ChunkProviderClientAccessor.getLoadedChunks(chunkProvider);

                if (loadedChunks.size() > 4 && !FluxLoadingManager.isStartCalcTargetChunkNum())
                {
                    Minecraft.getMinecraft().addScheduledTask(() ->
                    {
                        FluxLoading.logger.info("Chunk Loading Stage - Waiting chunks to load");
                    });

                    FluxLoadingManager.setStartCalcTargetChunkNum(true);
                    FluxLoadingManager.calcTargetChunkNum();
                }

                if (FluxLoadingManager.isTargetChunkNumCalculated() && FluxLoadingManager.getChunkLoadedNum() >= FluxLoadingManager.getTargetChunkNum())
                {
                    FluxLoadingManager.setCountingChunkLoaded(false);
                    FluxLoadingManager.setFinishChunkLoading(true);
                    FluxLoadingManager.startFadeOutTimer();
                }
            }

            FluxLoadingManager.incrChunkLoadedNum();
        }
    }
}
