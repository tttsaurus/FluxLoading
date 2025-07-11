package com.tttsaurus.fluxloading.core.raycast;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.culling.ClippingHelper;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.chunk.Chunk;
import org.lwjgl.BufferUtils;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector4f;
import java.nio.FloatBuffer;
import java.util.*;

public final class FrustumChunkRayCastHelper
{
    public static List<Ray> getRaysFromFrustum(Vec3d camPos, ClippingHelper clippingHelper, int horizontalCount, int verticalCount)
    {
        List<Ray> rays = new ArrayList<>();

        // P * M * (worldPos - camPos) = ndcPos
        // worldPos - camPos = (P * M)^-1 * ndcPos
        // worldPos = (P * M)^-1 * ndcPos + camPos

        Matrix4f projection = new Matrix4f();
        Matrix4f modelview = new Matrix4f();

        FloatBuffer projBuf = BufferUtils.createFloatBuffer(16);
        FloatBuffer modelBuf = BufferUtils.createFloatBuffer(16);
        projBuf.put(clippingHelper.projectionMatrix).flip();
        modelBuf.put(clippingHelper.modelviewMatrix).flip();

        projection.load(projBuf);
        modelview.load(modelBuf);

        Matrix4f combined = new Matrix4f();
        Matrix4f.mul(projection, modelview, combined);

        // (P * M)^-1
        Matrix4f inverse = new Matrix4f();
        Matrix4f.invert(combined, inverse);

        float[][] ndcCorners = {
                {-1, -1}, // bottom-left
                { 1, -1}, // bottom-right
                { 1,  1}, // top-right
                {-1,  1}};// top-left

        Vec3d[] dirs = new Vec3d[4];

        int index = 0;
        for (float[] ndc : ndcCorners)
        {
            float x = ndc[0];
            float y = ndc[1];

            Vector4f clip = new Vector4f(x, y, 1f, 1f); // far plane
            Vector4f world = new Vector4f();
            // (P * M)^-1 * ndcPos
            Matrix4f.transform(inverse, clip, world);
            world.x /= world.w;
            world.y /= world.w;
            world.z /= world.w;
            // (P * M)^-1 * ndcPos + camPos
            world.x += (float)camPos.x;
            world.y += (float)camPos.y;
            world.z += (float)camPos.z;

            Vec3d farPoint = new Vec3d(world.x, world.y, world.z);

            dirs[index++] = farPoint.subtract(camPos).normalize();
        }

        Vec3d camDir = bilerpVec3d(
            dirs[3], dirs[2],
            dirs[0], dirs[1],
            0.5d, 0.5d);
        // move it behind
        camPos = camPos.subtract(camDir.scale(1.5d));

        for (int i = 0; i < horizontalCount; i++)
        {
            for (int j = 0; j < verticalCount; j++)
            {
                double x = (double) i / (double) horizontalCount;
                double y = (double) j / (double) verticalCount;

                Vec3d dir = bilerpVec3d(
                        dirs[3], dirs[2],
                        dirs[0], dirs[1],
                        x, y);
                rays.add(new Ray(camPos, dir));
            }
        }

        return rays;
    }

    private static Vec3d bilerpVec3d(
            Vec3d dir00, Vec3d dir10,
            Vec3d dir01, Vec3d dir11,
            double x, double y)
    {
        Vec3d bottom = lerpVec3d(dir00, dir10, x);
        Vec3d top    = lerpVec3d(dir01, dir11, x);
        Vec3d result = lerpVec3d(bottom, top, y);
        return result.normalize();
    }

    private static Vec3d lerpVec3d(Vec3d a, Vec3d b, double t)
    {
        return a.scale(1d - t).add(b.scale(t));
    }

    public static int getChunkRayCastNum(List<Ray> rays, List<Chunk> chunks, double maxDistance)
    {
        Set<Long> visibleChunks = new HashSet<>();

        Map<Long, Chunk> chunkMap = new HashMap<>();
        for (Chunk chunk : chunks)
        {
            long key = chunkKey(chunk.x, chunk.z);
            chunkMap.put(key, chunk);
        }

        final double step = 0.5d;

        for (Ray ray : rays)
        {
            Vec3d pos = ray.pos;
            Vec3d dir = ray.dir.normalize();
            double traveled = 0;

            while (traveled < maxDistance)
            {
                BlockPos blockPos = new BlockPos(pos);
                int chunkX = blockPos.getX() >> 4;
                int chunkZ = blockPos.getZ() >> 4;
                long key = chunkKey(chunkX, chunkZ);

                Chunk chunk = chunkMap.get(key);
                if (chunk != null && !chunk.isEmpty())
                {
                    IBlockState state = chunk.getBlockState(blockPos);
                    if (state.isOpaqueCube())
                    {
                        visibleChunks.add(key);
                        break;
                    }
                }

                pos = pos.add(dir.scale(step));
                traveled += step;
            }
        }

        return visibleChunks.size();
    }

    private static long chunkKey(int x, int z)
    {
        return (((long) x) & 0xFFFFFFFFL) | ((((long) z) & 0xFFFFFFFFL) << 32);
    }
}
