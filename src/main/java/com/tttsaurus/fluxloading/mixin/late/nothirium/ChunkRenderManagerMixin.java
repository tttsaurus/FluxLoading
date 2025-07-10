package com.tttsaurus.fluxloading.mixin.late.nothirium;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.tttsaurus.fluxloading.FluxLoading;
import com.tttsaurus.fluxloading.core.FluxLoadingManager;
import com.tttsaurus.fluxloading.core.accessor.ChunkProviderClientAccessor;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import meldexun.nothirium.api.renderer.chunk.IChunkRenderer;
import meldexun.nothirium.mc.renderer.ChunkRenderManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ChunkProviderClient;
import net.minecraft.world.chunk.Chunk;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(ChunkRenderManager.class)
public class ChunkRenderManagerMixin
{
    @WrapMethod(method = "getRenderer", remap = false)
    private static IChunkRenderer<?> getRenderer(Operation<IChunkRenderer<?>> original)
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
                    return original.call();
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

        return original.call();
    }
}
