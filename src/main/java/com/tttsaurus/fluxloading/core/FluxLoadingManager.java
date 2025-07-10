package com.tttsaurus.fluxloading.core;

import com.tttsaurus.fluxloading.FluxLoading;
import com.tttsaurus.fluxloading.FluxLoadingConfig;
import com.tttsaurus.fluxloading.animation.SmoothDamp;
import com.tttsaurus.fluxloading.core.accessor.ChunkProviderClientAccessor;
import com.tttsaurus.fluxloading.render.CommonBuffers;
import com.tttsaurus.fluxloading.render.RenderUtils;
import com.tttsaurus.fluxloading.render.Texture2D;
import com.tttsaurus.fluxloading.render.shader.Shader;
import com.tttsaurus.fluxloading.render.shader.ShaderLoader;
import com.tttsaurus.fluxloading.render.shader.ShaderProgram;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.multiplayer.ChunkProviderClient;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.culling.ClippingHelper;
import net.minecraft.client.renderer.culling.ClippingHelperImpl;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.Entity;
import net.minecraft.server.integrated.IntegratedServer;
import net.minecraft.util.ScreenShotHelper;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.apache.commons.lang3.time.StopWatch;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

@SuppressWarnings("all")
public final class FluxLoadingManager
{
    // render
    private static ShaderProgram shaderProgram = null;
    private static FloatBuffer vertexBuffer;

    private static boolean screenshotToggle = false;
    private static boolean drawOverlay = false;
    private static boolean forceLoadingTitle = false;
    private static boolean chunkLoadingTitle = false;
    private static Texture2D texture = null;
    private static BufferedImage screenshot = null;
    private static boolean movementLocked = false;

    // extra chunk loading
    private static boolean waitChunksToLoad = true;
    private static boolean finishChunkLoading = false;
    private static boolean countingChunkLoaded = false;
    private static int chunkLoadedNum = 0;
    private static int targetChunkNum = 0;
    private static boolean startCalcTargetChunkNum = false;
    private static boolean targetChunkNumCalculated = false;

    // fade-out animation
    private static double extraWaitTime = 0.5d;
    private static double fadeOutDuration = 1.0d;
    private static StopWatch fadeOutStopWatch = null;
    private static SmoothDamp smoothDamp = null;
    private static double prevFadeOutTime = 0d;

    //<editor-fold desc="getters & setters">
    public static void prepareScreenshot() { screenshotToggle = true; }

    public static boolean isDrawOverlay() { return drawOverlay; }

    public static void setDrawOverlay(boolean flag) { drawOverlay = flag; }

    public static boolean isForceLoadingTitle() { return forceLoadingTitle; }

    public static void setForceLoadingTitle(boolean flag) { forceLoadingTitle = flag; }

    public static void setChunkLoadingTitle(boolean flag) { chunkLoadingTitle = flag; }

    public static boolean isTextureAvailable() { return texture != null; }

    public static void updateTexture(Texture2D tex)
    {
        if (texture != null) texture.dispose();
        texture = tex;
    }

    public static boolean isMovementLocked() { return movementLocked; }

    public static void setMovementLocked(boolean flag) { movementLocked = flag; }

    public static boolean isWaitChunksToLoad() { return waitChunksToLoad; }

    public static void setWaitChunksToLoad(boolean flag) { waitChunksToLoad = flag; }

    public static void setFinishChunkLoading(boolean flag) { finishChunkLoading = flag; }

    public static boolean isCountingChunkLoaded() { return countingChunkLoaded; }

    public static void setCountingChunkLoaded(boolean flag) { countingChunkLoaded = flag; }

    public static int getChunkLoadedNum() { return chunkLoadedNum; }

    public static void incrChunkLoadedNum() { chunkLoadedNum++; }

    public static void resetChunkLoadedNum() { chunkLoadedNum = 0; }

    public static boolean isStartCalcTargetChunkNum() { return startCalcTargetChunkNum; }

    public static void setStartCalcTargetChunkNum(boolean flag) { startCalcTargetChunkNum = flag; }

    public static boolean isTargetChunkNumCalculated() { return targetChunkNumCalculated; }

    public static void setTargetChunkNumCalculated(boolean flag) { targetChunkNumCalculated = flag; }

    public static int getTargetChunkNum() { return targetChunkNum; }

    public static void resetTargetChunkNum() { targetChunkNum = 0; }

    public static void setExtraWaitTime(double time) { extraWaitTime = time; }

    public static void setFadeOutDuration(double time) { fadeOutDuration = time; }

    public static void startFadeOutTimer()
    {
        fadeOutStopWatch = new StopWatch();
        fadeOutStopWatch.start();
        smoothDamp = new SmoothDamp(0, 1, (float)fadeOutDuration);
        prevFadeOutTime = 0d;
    }

    public static void resetFadeOutTimer()
    {
        if (fadeOutStopWatch != null)
        {
            fadeOutStopWatch.stop();
            fadeOutStopWatch = null;
        }
    }
    //</editor-fold>

    public static void calcTargetChunkNum()
    {
        Minecraft mc = Minecraft.getMinecraft();
        mc.addScheduledTask(() ->
        {
            ChunkProviderClient chunkProvider = mc.world.getChunkProvider();
            Long2ObjectMap<Chunk> loadedChunks = ChunkProviderClientAccessor.getLoadedChunks(chunkProvider);

            Entity camera = mc.getRenderViewEntity();
            double partialTicks = mc.getRenderPartialTicks();

            double camX = camera.lastTickPosX + (camera.posX - camera.lastTickPosX) * partialTicks;
            double camY = camera.lastTickPosY + (camera.posY - camera.lastTickPosY) * partialTicks;
            double camZ = camera.lastTickPosZ + (camera.posZ - camera.lastTickPosZ) * partialTicks;

            ClippingHelper frustumHelper = ClippingHelperImpl.getInstance();
            Frustum viewFrustum = new Frustum(frustumHelper);
            viewFrustum.setPosition(camX, camY, camZ);

            int num = 0;

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
                    if (!chunk.isEmpty())
                        num++;
            }

            targetChunkNum = num;
            targetChunkNumCalculated = true;
        });
    }

    //<editor-fold desc="save & read">
    public static void trySaveToLocal()
    {
        IntegratedServer server = Minecraft.getMinecraft().getIntegratedServer();
        if (server != null)
        {
            File worldSaveDir = new File("saves/" + server.getFolderName());
            if (screenshot != null)
                RenderUtils.createPng(
                        worldSaveDir,
                        "last_screenshot",
                        screenshot);
        }
    }

    public static void tryReadFromLocal(String folderName)
    {
        File screenshot = new File("saves/" + folderName + "/last_screenshot.png");
        if (screenshot.exists())
        {
            Texture2D texture = RenderUtils.readPng(screenshot);
            if (texture != null) updateTexture(texture);
        }
    }
    //</editor-fold>

    public static void drawOverlay()
    {
        if (!FluxLoadingAPI.duringDefaultWorldLoadingPhase)
            FluxLoadingAPI.duringDefaultWorldLoadingPhase = true;

        drawOverlay(0);
    }

    private static void drawOverlay(double time)
    {
        initShader();

        boolean blend = GL11.glIsEnabled(GL11.GL_BLEND);
        boolean depthTest = GL11.glIsEnabled(GL11.GL_DEPTH_TEST);

        GlStateManager.enableBlend();
        GlStateManager.disableDepth();

        GL11.glGetInteger(GL13.GL_ACTIVE_TEXTURE, CommonBuffers.INT_BUFFER_16);
        int texUnit = CommonBuffers.INT_BUFFER_16.get(0);

        GlStateManager.setActiveTexture(GL13.GL_TEXTURE1);
        GL11.glGetInteger(GL11.GL_TEXTURE_BINDING_2D, CommonBuffers.INT_BUFFER_16);
        int texUnit1TextureID = CommonBuffers.INT_BUFFER_16.get(0);

        GlStateManager.bindTexture(texture.getGlTextureID());

        GlStateManager.setActiveTexture(texUnit);

        shaderProgram.use();

        if (time >= extraWaitTime)
        {
            double nowFadeOutTime = (time - extraWaitTime);
            double delta = (nowFadeOutTime - prevFadeOutTime);
            float percentage = smoothDamp.evaluate((float)delta);
            prevFadeOutTime = nowFadeOutTime;

            shaderProgram.setUniform("percentage", percentage);
        }

        triggerShader();
        shaderProgram.unuse();

        GL11.glGetInteger(GL13.GL_ACTIVE_TEXTURE, CommonBuffers.INT_BUFFER_16);
        texUnit = CommonBuffers.INT_BUFFER_16.get(0);

        GlStateManager.setActiveTexture(GL13.GL_TEXTURE1);
        GlStateManager.bindTexture(texUnit1TextureID);

        GlStateManager.setActiveTexture(texUnit);

        if (depthTest)
            GlStateManager.enableDepth();
        else
            GlStateManager.disableDepth();
        if (blend)
            GlStateManager.enableBlend();
        else
            GlStateManager.disableBlend();
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onRenderGameOverlay(RenderGameOverlayEvent.Post event)
    {
        if (event.getType() != RenderGameOverlayEvent.ElementType.ALL) return;

        if (isTextureAvailable())
        {
            if (!FluxLoadingAPI.finishLoading)
            {
                if (!movementLocked)
                {
                    movementLocked = true;
                    Minecraft.getMinecraft().mouseHelper.ungrabMouseCursor();
                }
            }

            if (!finishChunkLoading)
            {
                if (!FluxLoadingAPI.duringExtraChunkLoadingPhase)
                {
                    FluxLoadingAPI.duringDefaultWorldLoadingPhase = false;
                    FluxLoadingAPI.duringExtraChunkLoadingPhase = true;
                }

                drawOverlay(0);

                if (chunkLoadingTitle)
                {
                    ScaledResolution resolution = new ScaledResolution(Minecraft.getMinecraft());
                    String i18nText = I18n.format("fluxloading.loading_wait");
                    float width = RenderUtils.fontRenderer.getStringWidth(i18nText);
                    RenderUtils.renderText(i18nText, (resolution.getScaledWidth() - width) / 2, (float) (resolution.getScaledHeight() - RenderUtils.fontRenderer.FONT_HEIGHT) / 2, 1, Color.WHITE.getRGB(), true);
                }
            }

            if (fadeOutStopWatch != null)
            {
                double time = fadeOutStopWatch.getNanoTime() / 1E9d;

                if (!FluxLoadingAPI.duringExtraWaitPhase)
                {
                    FluxLoadingAPI.duringDefaultWorldLoadingPhase = false;
                    FluxLoadingAPI.duringExtraChunkLoadingPhase = false;
                    FluxLoadingAPI.duringExtraWaitPhase = true;
                }

                if (time >= extraWaitTime && !FluxLoadingAPI.duringFadingOutPhase)
                {
                    FluxLoadingAPI.duringExtraWaitPhase = false;
                    FluxLoadingAPI.duringFadingOutPhase = true;
                }

                if (time >= fadeOutDuration + extraWaitTime)
                {
                    resetFadeOutTimer();
                    texture.dispose();
                    resetShader();

                    FluxLoadingAPI.duringDefaultWorldLoadingPhase = false;
                    FluxLoadingAPI.duringExtraChunkLoadingPhase = false;
                    FluxLoadingAPI.duringExtraWaitPhase = false;
                    FluxLoadingAPI.duringFadingOutPhase = false;
                    FluxLoadingAPI.finishLoading = true;

                    if (movementLocked)
                    {
                        movementLocked = false;
                        Minecraft.getMinecraft().mouseHelper.grabMouseCursor();
                    }

                    return;
                }

                drawOverlay(time);
            }
        }
    }

    @SubscribeEvent
    public static void onRenderWorldLast(RenderWorldLastEvent event)
    {
        if (screenshotToggle)
        {
            screenshotToggle = false;
            Minecraft minecraft = Minecraft.getMinecraft();
            screenshot = ScreenShotHelper.createScreenshot(minecraft.displayWidth, minecraft.displayHeight, minecraft.getFramebuffer());
        }
    }

    private static void triggerShader()
    {
        GL20.glGetVertexAttrib(0, GL20.GL_VERTEX_ATTRIB_ARRAY_ENABLED, CommonBuffers.INT_BUFFER_16);
        boolean enabled = CommonBuffers.INT_BUFFER_16.get(0) == GL11.GL_TRUE;

        GL20.glEnableVertexAttribArray(0);

        GL20.glVertexAttribPointer(0, 3, false, 0, vertexBuffer);
        GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, 3);

        if (enabled)
            GL20.glEnableVertexAttribArray(0);
        else
            GL20.glDisableVertexAttribArray(0);
    }

    private static void initShader()
    {
        if (shaderProgram == null)
        {
            Shader vertex = ShaderLoader.load("fluxloading:shaders/loading_screen_vertex.glsl", Shader.ShaderType.VERTEX);
            Shader frag = ShaderLoader.load("fluxloading:shaders/loading_screen_frag.glsl", Shader.ShaderType.FRAGMENT);

            shaderProgram = new ShaderProgram(vertex, frag);
            shaderProgram.setup();

            FluxLoading.logger.info(shaderProgram.getSetupDebugReport());

            shaderProgram.use();
            shaderProgram.setUniform("screenTexture", 1);
            shaderProgram.setUniform("percentage", 0f);
            shaderProgram.setUniform("enableDissolving", FluxLoadingConfig.ENABLE_DISSOLVING_EFFECT);
            shaderProgram.setUniform("enableWaving", FluxLoadingConfig.ENABLE_WAVING_EFFECT);
            shaderProgram.setUniform("enableDarkOverlay", FluxLoadingConfig.ENABLE_DARK_OVERLAY);
            shaderProgram.unuse();

            vertexBuffer = ByteBuffer.allocateDirect(9 * Float.BYTES).order(ByteOrder.nativeOrder()).asFloatBuffer();
            // vec2(-1, -1), vec2(3, -1), vec2(-1, 3)
            vertexBuffer.put(new float[]{-1, -1, 0, 3, -1, 0, -1, 3, 0}).flip();
        }
    }

    public static void resetShader()
    {
        if (shaderProgram != null)
        {
            shaderProgram.use();
            shaderProgram.setUniform("percentage", 0f);
            shaderProgram.unuse();
        }
    }
}
