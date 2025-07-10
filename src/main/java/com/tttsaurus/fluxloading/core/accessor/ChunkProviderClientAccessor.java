package com.tttsaurus.fluxloading.core.accessor;

import com.tttsaurus.fluxloading.core.function.Func_1Param;
import com.tttsaurus.fluxloading.core.util.AccessorUnreflector;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import net.minecraft.client.multiplayer.ChunkProviderClient;
import net.minecraft.world.chunk.Chunk;

public final class ChunkProviderClientAccessor
{
    private static boolean init = false;

    private static Func_1Param<Long2ObjectMap<Chunk>, ChunkProviderClient> loadedChunksGetter;

    @SuppressWarnings("all")
    private static void init()
    {
        if (init) return;

        loadedChunksGetter = (Func_1Param<Long2ObjectMap<Chunk>, ChunkProviderClient>)AccessorUnreflector.getDeclaredFieldGetter(
                ChunkProviderClient.class,
                "loadedChunks",
                "field_73236_b");

        init = true;
    }

    public static Long2ObjectMap<Chunk> getLoadedChunks(ChunkProviderClient obj)
    {
        init();
        return loadedChunksGetter.invoke(obj);
    }
}
