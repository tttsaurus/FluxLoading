package com.tttsaurus.fluxloading.core;

import com.tttsaurus.fluxloading.FluxLoading;
import com.tttsaurus.fluxloading.render.CommonBuffers;
import com.tttsaurus.fluxloading.render.Mesh;
import com.tttsaurus.fluxloading.render.RenderUtils;
import com.tttsaurus.fluxloading.render.Texture2D;
import com.tttsaurus.fluxloading.render.shader.Shader;
import com.tttsaurus.fluxloading.render.shader.ShaderLoader;
import com.tttsaurus.fluxloading.render.shader.ShaderProgram;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.server.integrated.IntegratedServer;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.apache.commons.lang3.time.StopWatch;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import java.io.File;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;

public final class WorldLoadingScreenOverhaul
{
    private static final Minecraft minecraft = Minecraft.getMinecraft();

    private static ShaderProgram shaderProgram = null;
    private static Mesh mesh = null;

    private static boolean screenShotToggle = false;
    private static boolean drawOverlay = false;
    private static Texture2D texture = null;
    private static ByteBuffer textureBuffer = null;
    private static int textureBufferWidth;
    private static int textureBufferHeight;
    private static int fogColor;

    private static boolean countingChunkLoaded = false;
    private static int chunkLoadedNum = 0;
    private static boolean finishedLoadingChunks = false;

    private static double fadeOutDuration = 1.0d;
    private static StopWatch fadingOutStopWatch = null;

    //<editor-fold desc="getters & setters">
    public static void prepareScreenShot() { screenShotToggle = true; }

    public static boolean getDrawOverlay() { return drawOverlay; }
    public static void setDrawOverlay(boolean flag) { drawOverlay = flag; }

    public static boolean isTextureNull() { return texture == null; }

    public static void updateTexture(Texture2D tex)
    {
        if (texture != null) texture.dispose();
        texture = tex;
    }

    public static void setFogColor(int color) { fogColor = color; }

    public static boolean getCountingChunkLoaded() { return countingChunkLoaded; }
    public static void setCountingChunkLoaded(boolean flag) { countingChunkLoaded = flag; }

    public static int getChunkLoadedNum() { return chunkLoadedNum; }
    public static void incrChunkLoadedNum() { chunkLoadedNum++; }
    public static void resetChunkLoadedNum() { chunkLoadedNum = 0; }

    public static void setFinishedLoadingChunks(boolean flag) { finishedLoadingChunks = flag; }

    public static void startFadeOutTimer()
    {
        fadingOutStopWatch = new StopWatch();
        fadingOutStopWatch.start();
    }
    public static void resetFadeOutTimer()
    {
        if (fadingOutStopWatch != null)
        {
            fadingOutStopWatch.stop();
            fadingOutStopWatch = null;
        }
    }
    //</editor-fold>

    public static void trySaveToLocal()
    {
        IntegratedServer server = Minecraft.getMinecraft().getIntegratedServer();
        if (server != null)
        {
            File worldSaveDir = new File("saves/" + server.getFolderName());
            if (textureBuffer != null)
                RenderUtils.createPng(
                        worldSaveDir,
                        "last_screenshot",
                        textureBuffer,
                        textureBufferWidth,
                        textureBufferHeight);

            try
            {
                RandomAccessFile fogColorFile = new RandomAccessFile("saves/" + server.getFolderName() + "/last_fog_color", "rw");
                fogColorFile.setLength(0);
                fogColorFile.seek(0);
                fogColorFile.writeInt(fogColor);
                fogColorFile.close();
            }
            catch (Exception ignored) { }
        }
    }
    public static void tryReadFromLocal(String folderName)
    {
        File screenshot = new File("saves/" + folderName + "/last_screenshot.png");
        if (screenshot.exists())
        {
            Texture2D texture = RenderUtils.readPng(screenshot);
            if (texture != null) WorldLoadingScreenOverhaul.updateTexture(texture);
        }

        try
        {
            RandomAccessFile fogColorFile = new RandomAccessFile("saves/" + folderName + "/last_fog_color", "rw");
            fogColor = fogColorFile.readInt();
            fogColorFile.close();
        }
        catch (Exception ignored) { }
    }

    public static void drawOverlay()
    {
        drawOverlay(0);
    }
    private static void drawOverlay(double time)
    {
        initShader();

        if (time < 0.5d)
            RenderUtils.renderRectFullScreen(fogColor);

        GL11.glGetInteger(GL13.GL_ACTIVE_TEXTURE, CommonBuffers.intBuffer);
        int texUnit = CommonBuffers.intBuffer.get(0);

        GlStateManager.setActiveTexture(GL13.GL_TEXTURE1);
        GL11.glGetInteger(GL11.GL_TEXTURE_BINDING_2D, CommonBuffers.intBuffer);
        int texUnit1TextureID = CommonBuffers.intBuffer.get(0);

        GlStateManager.bindTexture(texture.getGlTextureID());

        GlStateManager.setActiveTexture(texUnit);

        shaderProgram.use();

        if (time >= 0.5d)
        {
            float percentage = (float)((time - 0.5d) / (fadeOutDuration - 0.5d));

            shaderProgram.setUniform("percentage", percentage);
        }

        mesh.render();
        shaderProgram.unuse();

        GL11.glGetInteger(GL13.GL_ACTIVE_TEXTURE, CommonBuffers.intBuffer);
        texUnit = CommonBuffers.intBuffer.get(0);

        GlStateManager.setActiveTexture(GL13.GL_TEXTURE1);
        GlStateManager.bindTexture(texUnit1TextureID);

        GlStateManager.setActiveTexture(texUnit);
    }

    @SubscribeEvent
    public static void onRenderGameOverlay(RenderGameOverlayEvent event)
    {
        if (event.getType() != RenderGameOverlayEvent.ElementType.ALL) return;
        if (!finishedLoadingChunks)
        {
            drawOverlay();
        }
        if (fadingOutStopWatch != null)
        {
            double time = fadingOutStopWatch.getNanoTime() / 1E9d;
            if (time >= fadeOutDuration)
            {
                resetFadeOutTimer();
                texture.dispose();
                shaderProgram.use();
                shaderProgram.setUniform("percentage", 0f);
                shaderProgram.unuse();
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

            textureBuffer = RenderUtils.getInGameScreenShotByteBufferFullScreen();
            textureBufferWidth = minecraft.displayWidth;
            textureBufferHeight = minecraft.displayHeight;
        }
    }

    private static void initShader()
    {
        if (shaderProgram == null)
        {
            Shader vertex = ShaderLoader.load("fluxloading:shaders/test_vertex.glsl", Shader.ShaderType.VERTEX);
            Shader frag = ShaderLoader.load("fluxloading:shaders/test_frag.glsl", Shader.ShaderType.FRAGMENT);

            shaderProgram = new ShaderProgram(vertex, frag);
            shaderProgram.setup();

            FluxLoading.logger.info(shaderProgram.getSetupDebugReport());

            shaderProgram.use();
            int screenTextureLoc = shaderProgram.getUniformLocation("screenTexture");
            GL20.glUniform1i(screenTextureLoc, 1);
            shaderProgram.setUniform("percentage", 0f);
            shaderProgram.unuse();

            mesh = new Mesh(new float[24], new int[]{0, 1, 2});
            mesh.setup();
        }
    }
}
