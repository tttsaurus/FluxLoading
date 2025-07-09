package com.tttsaurus.fluxloading.mixin.late.nothirium;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.tttsaurus.fluxloading.core.WorldLoadingScreenOverhaul;
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
        if (WorldLoadingScreenOverhaul.isCountingChunkLoaded())
        {
            if (!WorldLoadingScreenOverhaul.isWaitChunksToLoad())
            {
                WorldLoadingScreenOverhaul.setCountingChunkLoaded(false);
                WorldLoadingScreenOverhaul.startFadeOutTimer();
                return original.call();
            }

            WorldLoadingScreenOverhaul.incrChunkLoadedNum();

            ChunkProviderClient chunkProvider = Minecraft.getMinecraft().world.getChunkProvider();
            Long2ObjectMap<Chunk> loadedChunks = ChunkProviderClientAccessor.getLoadedChunks(chunkProvider);

            if (loadedChunks.size() > 4 && !WorldLoadingScreenOverhaul.isStartCalcTargetChunkNum())
            {
                WorldLoadingScreenOverhaul.setStartCalcTargetChunkNum(true);
                WorldLoadingScreenOverhaul.calcTargetChunkNum();
            }

            if (WorldLoadingScreenOverhaul.isTargetChunkNumCalculated() && WorldLoadingScreenOverhaul.getChunkLoadedNum() >= WorldLoadingScreenOverhaul.getTargetChunkNum())
            {
                WorldLoadingScreenOverhaul.setCountingChunkLoaded(false);
                WorldLoadingScreenOverhaul.setFinishChunkLoading(true);
                WorldLoadingScreenOverhaul.startFadeOutTimer();
            }
        }

        return original.call();
    }
}
