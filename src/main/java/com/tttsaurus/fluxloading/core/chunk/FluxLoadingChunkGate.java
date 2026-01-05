package com.tttsaurus.fluxloading.core.chunk;

import com.tttsaurus.fluxloading.FluxLoading;
import com.tttsaurus.fluxloading.core.accessor.ChunkProviderClientAccessor;
import com.tttsaurus.fluxloading.core.chunk.raycast.FrustumChunkRayCastHelper;
import com.tttsaurus.fluxloading.core.render.RenderUtils;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ChunkProviderClient;
import net.minecraft.client.renderer.culling.ClippingHelper;
import net.minecraft.client.renderer.culling.ClippingHelperImpl;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.chunk.Chunk;

import java.util.ArrayList;
import java.util.List;

public final class FluxLoadingChunkGate
{
    public enum Decision
    {
        NONE,
        DECIDE_SKIP_WAIT,
        DECIDE_WAIT_CHUNKS,
        EXTRA_CHUNK_LOADING_FINISHED
    }

    private boolean waitChunksToLoad = true;

    private boolean startCalcTargetChunkNum = false;
    private boolean targetChunkNumCalculated = false;

    private int chunkLoadedNum = 0;
    private int targetChunkNum = 0;

    private boolean chunkLoadingTitle = false;
    private boolean chunkLoadingPercentage = false;

    private int chunkRayCastTestRayDis = 512;

    private boolean decided = false;

    public void reset(boolean waitChunksToLoadFlag)
    {
        waitChunksToLoad = waitChunksToLoadFlag;

        startCalcTargetChunkNum = false;
        targetChunkNumCalculated = false;

        chunkLoadedNum = 0;
        targetChunkNum = 0;

        decided = false;
    }

    @SuppressWarnings("all")
    public Decision onChunkCompiled(FluxLoadingChunkSource source)
    {
        // instant complete
        if (source.instantComplete)
        {
            chunkLoadedNum++;

            if (!decided)
            {
                decided = true;

                Minecraft.getMinecraft().addScheduledTask(() ->
                {
                    FluxLoading.LOGGER.info("Chunk loading stage entry point: " + source);
                    FluxLoading.LOGGER.info("Chunk loading stage: Instant chunk completion");
                });

                return Decision.DECIDE_SKIP_WAIT;
            }

            return Decision.NONE;
        }

        // incremental complete
        chunkLoadedNum++;

        if (!decided)
        {
            if (!waitChunksToLoad)
            {
                if (chunkLoadedNum >= 1)
                {
                    decided = true;
                    Minecraft.getMinecraft().addScheduledTask(() ->
                    {
                        FluxLoading.LOGGER.info("Chunk loading stage entry point: " + source);
                        FluxLoading.LOGGER.info("Chunk loading stage: Not going to wait chunks to load");
                    });

                    return Decision.DECIDE_SKIP_WAIT;
                }

                return Decision.NONE;
            }

            // waiting chunks
            ChunkProviderClient chunkProvider = Minecraft.getMinecraft().world.getChunkProvider();
            Long2ObjectMap<Chunk> loadedChunks = ChunkProviderClientAccessor.getLoadedChunks(chunkProvider);

            if (loadedChunks.size() > 4 && !startCalcTargetChunkNum)
            {
                startCalcTargetChunkNum = true;

                Minecraft.getMinecraft().addScheduledTask(() ->
                {
                    FluxLoading.LOGGER.info("Chunk loading stage entry point: " + source);
                    FluxLoading.LOGGER.info("Chunk loading stage: Waiting chunks to load");
                });

                calcTargetChunkNumAsync();
                decided = true;
                return Decision.DECIDE_WAIT_CHUNKS;
            }

            return Decision.NONE;
        }

        if (targetChunkNumCalculated && chunkLoadedNum >= targetChunkNum)
        {
            return Decision.EXTRA_CHUNK_LOADING_FINISHED;
        }

        return Decision.NONE;
    }

    private void calcTargetChunkNumAsync()
    {
        Minecraft.getMinecraft().addScheduledTask(() ->
        {
            ChunkProviderClient chunkProvider = Minecraft.getMinecraft().world.getChunkProvider();
            Long2ObjectMap<Chunk> loadedChunks = ChunkProviderClientAccessor.getLoadedChunks(chunkProvider);

            Vec3d camPos = RenderUtils.getCameraPos();

            ClippingHelper frustumHelper = ClippingHelperImpl.getInstance();
            Frustum viewFrustum = new Frustum(frustumHelper);
            viewFrustum.setPosition(camPos.x, camPos.y, camPos.z);

            List<Chunk> visibleChunks = new ArrayList<>();
            for (Chunk chunk : loadedChunks.values())
            {
                int chunkX = chunk.x;
                int chunkZ = chunk.z;

                double minX = chunkX * 16;
                double minY = 0;
                double minZ = chunkZ * 16;

                double maxX = minX + 16;
                double maxY = 256;
                double maxZ = minZ + 16;

                AxisAlignedBB box = new AxisAlignedBB(minX, minY, minZ, maxX, maxY, maxZ);

                if (viewFrustum.isBoundingBoxInFrustum(box))
                {
                    if (!chunk.isEmpty())
                    {
                        visibleChunks.add(chunk);
                    }
                }
            }

            FluxLoading.LOGGER.info("Chunk count from ChunkProviderClient: " + loadedChunks.size());
            FluxLoading.LOGGER.info("Visible chunks from player's perspective: " + visibleChunks.size());

            List<FrustumChunkRayCastHelper.Ray> frustumRays = FrustumChunkRayCastHelper.getRaysFromFrustum(camPos, ClippingHelperImpl.getInstance(), 10, 10);
            targetChunkNum = FrustumChunkRayCastHelper.getChunkRayCastNum(frustumRays, visibleChunks, chunkRayCastTestRayDis);

            FluxLoading.LOGGER.info("Visible chunks after frustum ray casting: " + targetChunkNum);

            targetChunkNumCalculated = true;
        });
    }

    public void setChunkLoadingTitleEnabled(boolean enabled) { chunkLoadingTitle = enabled; }

    public void setChunkLoadingPercentageEnabled(boolean enabled) { chunkLoadingPercentage = enabled; }

    public void setChunkRayCastTestRayDis(int dis) { chunkRayCastTestRayDis = dis; }

    public boolean isChunkLoadingTitleEnabled() { return chunkLoadingTitle; }

    public boolean isChunkLoadingPercentageEnabled() { return chunkLoadingPercentage; }

    public boolean isTargetCalculated() { return targetChunkNumCalculated; }

    public int getChunkLoadedNum() { return chunkLoadedNum; }

    public int getTargetChunkNum() { return targetChunkNum; }
}
