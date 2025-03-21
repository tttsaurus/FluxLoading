package com.tttsaurus.fluxloading.core;

import com.tttsaurus.fluxloading.FluxLoading;
import com.tttsaurus.fluxloading.FluxLoadingConfig;
import com.tttsaurus.fluxloading.animation.SmoothDamp;
import com.tttsaurus.fluxloading.render.CommonBuffers;
import com.tttsaurus.fluxloading.render.Mesh;
import com.tttsaurus.fluxloading.render.RenderUtils;
import com.tttsaurus.fluxloading.render.Texture2D;
import com.tttsaurus.fluxloading.render.shader.Shader;
import com.tttsaurus.fluxloading.render.shader.ShaderLoader;
import com.tttsaurus.fluxloading.render.shader.ShaderProgram;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.server.integrated.IntegratedServer;
import net.minecraft.util.ScreenShotHelper;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.apache.commons.lang3.time.StopWatch;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;

public final class WorldLoadingScreenOverhaul
{
    // render
    private static ShaderProgram shaderProgram = null;
    private static Mesh mesh = null;

    private static boolean screenShotToggle = false;
    private static boolean drawOverlay = false;
    private static boolean forceLoadingTitle = false;
    private static boolean chunkBuildingTitle = false;
    private static Texture2D texture = null;
    private static BufferedImage screenShot = null;

    // waiting chunk build
    private static boolean countingChunkLoaded = false;
    private static int chunkLoadedNum = 0;
    private static boolean finishedLoadingChunks = false;
    private static int targetChunkNum = 0;
    private static float targetChunkNumCoefficient = 1.0f;

    // fade out animation
    private static double extraWaitTime = 0.5d;
    private static double fadeOutDuration = 1.0d;
    private static StopWatch fadeOutStopWatch = null;
    private static SmoothDamp smoothDamp = null;
    private static double prevFadeOutTime = 0d;

    //<editor-fold desc="getters & setters">
    public static void prepareScreenShot() { screenShotToggle = true; }

    public static boolean getDrawOverlay() { return drawOverlay; }
    public static void setDrawOverlay(boolean flag) { drawOverlay = flag; }

    public static boolean getForceLoadingTitle() { return forceLoadingTitle; }
    public static void setForceLoadingTitle(boolean flag) { forceLoadingTitle = flag; }

    public static void setChunkBuildingTitle(boolean flag) { chunkBuildingTitle = flag; }

    public static boolean isTextureAvailable() { return texture != null; }
    public static void updateTexture(Texture2D tex)
    {
        if (texture != null) texture.dispose();
        texture = tex;
    }

    public static boolean getCountingChunkLoaded() { return countingChunkLoaded; }
    public static void setCountingChunkLoaded(boolean flag) { countingChunkLoaded = flag; }

    public static int getChunkLoadedNum() { return chunkLoadedNum; }
    public static void incrChunkLoadedNum() { chunkLoadedNum++; }
    public static void resetChunkLoadedNum() { chunkLoadedNum = 0; }

    public static void setFinishedLoadingChunks(boolean flag) { finishedLoadingChunks = flag; }

    public static int getTargetChunkNum()
    {
        if (targetChunkNum == 0)
        {
            int n = Minecraft.getMinecraft().gameSettings.renderDistanceChunks;
            int area = (2 * n + 1) * (2 * n + 1);
            targetChunkNum = (int)((Minecraft.getMinecraft().gameSettings.fovSetting / 360f) * area);
            targetChunkNum = (int)(targetChunkNumCoefficient * targetChunkNum);
            targetChunkNum = targetChunkNum <= 0 ? 1 : targetChunkNum;
        }
        return targetChunkNum;
    }
    public static void resetTargetChunkNum() { targetChunkNum = 0; }

    public static void setTargetChunkNumCoefficient(float coefficient) { targetChunkNumCoefficient = coefficient; }

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

    //<editor-fold desc="save & read">
    public static void trySaveToLocal()
    {
        IntegratedServer server = Minecraft.getMinecraft().getIntegratedServer();
        if (server != null)
        {
            File worldSaveDir = new File("saves/" + server.getFolderName());
            if (screenShot != null)
                RenderUtils.createPng(
                        worldSaveDir,
                        "last_screenshot",
                        screenShot);
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
        drawOverlay(0);
    }
    private static void drawOverlay(double time)
    {
        initShader();

        boolean blend = GL11.glIsEnabled(GL11.GL_BLEND);
        boolean depthTest = GL11.glIsEnabled(GL11.GL_DEPTH_TEST);

        GlStateManager.enableBlend();
        GlStateManager.disableDepth();

        GL11.glGetInteger(GL13.GL_ACTIVE_TEXTURE, CommonBuffers.intBuffer);
        int texUnit = CommonBuffers.intBuffer.get(0);

        GlStateManager.setActiveTexture(GL13.GL_TEXTURE1);
        GL11.glGetInteger(GL11.GL_TEXTURE_BINDING_2D, CommonBuffers.intBuffer);
        int texUnit1TextureID = CommonBuffers.intBuffer.get(0);

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

        mesh.render();
        shaderProgram.unuse();

        GL11.glGetInteger(GL13.GL_ACTIVE_TEXTURE, CommonBuffers.intBuffer);
        texUnit = CommonBuffers.intBuffer.get(0);

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
        if (isTextureAvailable() && !finishedLoadingChunks)
        {
            drawOverlay();

            if (chunkBuildingTitle)
            {
                ScaledResolution resolution = new ScaledResolution(Minecraft.getMinecraft());
                String i18nText = I18n.format("fluxloading.loading_wait");
                float width = RenderUtils.fontRenderer.getStringWidth(i18nText);
                RenderUtils.renderText(i18nText, (resolution.getScaledWidth() - width) / 2, (float) (resolution.getScaledHeight() - RenderUtils.fontRenderer.FONT_HEIGHT) / 2, 1, Color.WHITE.getRGB(), true);
            }
        }
        if (isTextureAvailable() && fadeOutStopWatch != null)
        {
            double time = fadeOutStopWatch.getNanoTime() / 1E9d;
            if (time >= fadeOutDuration + extraWaitTime)
            {
                resetFadeOutTimer();
                texture.dispose();
                resetShader();
                return;
            }
            drawOverlay(time);
        }
    }

    @SubscribeEvent
    public static void onRenderWorldLast(RenderWorldLastEvent event)
    {
        if (screenShotToggle)
        {
            screenShotToggle = false;
            Minecraft minecraft = Minecraft.getMinecraft();
            screenShot = ScreenShotHelper.createScreenshot(minecraft.displayWidth, minecraft.displayHeight, minecraft.getFramebuffer());
        }
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

            mesh = new Mesh(new float[24], new int[]{0, 1, 2});
            mesh.setup();
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
