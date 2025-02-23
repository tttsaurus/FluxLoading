package com.tttsaurus.fluxloading.core;

import com.tttsaurus.fluxloading.FluxLoading;
import com.tttsaurus.fluxloading.render.CommonBuffers;
import com.tttsaurus.fluxloading.render.Mesh;
import com.tttsaurus.fluxloading.render.Texture2D;
import com.tttsaurus.fluxloading.render.shader.Shader;
import com.tttsaurus.fluxloading.render.shader.ShaderLoader;
import com.tttsaurus.fluxloading.render.shader.ShaderProgram;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.shader.Framebuffer;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import java.nio.ByteBuffer;

public final class WorldLoadingScreenOverhaul
{
    private static final Minecraft minecraft = Minecraft.getMinecraft();

    private static Framebuffer fbo = null;
    private static boolean isFboReady = false;

    private static ShaderProgram shaderProgram = null;

    private static boolean screenShotToggle = false;
    private static boolean drawOverlay = false;
    private static Texture2D texture = null;
    private static ByteBuffer textureBuffer = null;
    private static int skyColor;

    //<editor-fold desc="getters & setters">
    public static void prepareScreenShot() { screenShotToggle = true; }

    public static boolean getDrawOverlay() { return drawOverlay; }
    public static void setDrawOverlay(boolean flag) { drawOverlay = flag; }

    public static boolean isFboReady() { return isFboReady; }
    public static boolean isTextureNull() { return texture == null; }
    public static boolean isTextureBufferNull() { return textureBuffer == null; }

    public static int getTextureWidth()
    {
        if (texture == null) return 0;
        return texture.getWidth();
    }
    public static int getTextureHeight()
    {
        if (texture == null) return 0;
        return texture.getHeight();
    }
    public static ByteBuffer getTextureBuffer() { return textureBuffer; }

    public static void updateTexture(Texture2D tex)
    {
        if (texture != null) texture.dispose();
        texture = tex;
    }

    public static void setSkyColor(int color) { skyColor = color; }
    public static int getSkyColor() { return skyColor; }
    //</editor-fold>

    public static void drawOverlay()
    {
        GL11.glGetInteger(GL13.GL_ACTIVE_TEXTURE, CommonBuffers.intBuffer);
        int texUnit = CommonBuffers.intBuffer.get(0);

        GlStateManager.setActiveTexture(GL13.GL_TEXTURE1);
        GL11.glGetInteger(GL11.GL_TEXTURE_BINDING_2D, CommonBuffers.intBuffer);
        int texUnit1TextureID = CommonBuffers.intBuffer.get(0);

        fbo.bindFramebufferTexture();

        GL11.glGetInteger(GL11.GL_TEXTURE_BINDING_2D, CommonBuffers.intBuffer);
        int after = CommonBuffers.intBuffer.get(0);

        GlStateManager.setActiveTexture(texUnit);

        FluxLoading.logger.info("prev tex1 texid: " + texUnit1TextureID);
        FluxLoading.logger.info("after tex1 texid: " + after);
        FluxLoading.logger.info("fbo texid: " + fbo.framebufferTexture);



        shaderProgram.use();

        float[] vertices = new float[]
        {
                // positions          // texcoords   // normals
                -1.0f, -1.0f, 0.0f,   0.0f, 0.0f,     0.0f, 0.0f, 1.0f,  // bottom-left
                1.0f, -1.0f, 0.0f,   1.0f, 0.0f,     0.0f, 0.0f, 1.0f,  // bottom-right
                1.0f,  1.0f, 0.0f,   1.0f, 1.0f,     0.0f, 0.0f, 1.0f,  // top-right
                -1.0f,  1.0f, 0.0f,   0.0f, 1.0f,     0.0f, 0.0f, 1.0f   // top-left
        };

        int[] indices = new int[]
        {
                // two triangles to make up the quad
                0, 1, 2,   // first triangle: bottom-left, bottom-right, top-right
                0, 2, 3    // second triangle: bottom-left, top-right, top-left
        };

        Mesh mesh = new Mesh(vertices, indices);
        mesh.setup();
        mesh.render();

        shaderProgram.unuse();

        GL11.glGetInteger(GL13.GL_ACTIVE_TEXTURE, CommonBuffers.intBuffer);
        texUnit = CommonBuffers.intBuffer.get(0);

        GlStateManager.setActiveTexture(GL13.GL_TEXTURE1);
        GlStateManager.bindTexture(texUnit1TextureID);

        GlStateManager.setActiveTexture(texUnit);

//        RenderUtils.renderRectFullScreen(skyColor);
//        RenderUtils.renderTexture2DFullScreen(texture.getGlTextureID());
    }

    @SubscribeEvent
    public static void onRenderWorldLast(RenderWorldLastEvent event)
    {
//        if (event.getType() != RenderGameOverlayEvent.ElementType.ALL) return;
        if (screenShotToggle)
        {
            screenShotToggle = false;

            initFbo();
            bindMcFbo();

            initShader();

            OpenGlHelper.glBindFramebuffer(GL30.GL_READ_FRAMEBUFFER, minecraft.getFramebuffer().framebufferObject);
            OpenGlHelper.glBindFramebuffer(GL30.GL_DRAW_FRAMEBUFFER, fbo.framebufferObject);

            GL30.glBlitFramebuffer(0, 0, fbo.framebufferWidth, fbo.framebufferHeight, 0, 0, fbo.framebufferWidth, fbo.framebufferHeight, GL11.GL_COLOR_BUFFER_BIT, GL11.GL_NEAREST);

            isFboReady = true;

//            textureBuffer = RenderUtils.getInGameScreenShotByteBufferFullScreen();
//            updateTexture(new Texture2D(minecraft.displayWidth, minecraft.displayHeight, textureBuffer));
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
            shaderProgram.unuse();
        }
    }
    private static void initFbo()
    {
        if (fbo == null)
        {
            fbo = new Framebuffer(minecraft.displayWidth, minecraft.displayHeight, true);
            fbo.framebufferColor[0] = 0f;
            fbo.framebufferColor[1] = 0f;
            fbo.framebufferColor[2] = 0f;
            fbo.framebufferColor[3] = 0f;
        }
        if (fbo.framebufferWidth != minecraft.displayWidth || fbo.framebufferHeight != minecraft.displayHeight)
        {
            fbo.createBindFramebuffer(minecraft.displayWidth, minecraft.displayHeight);
        }
    }
    private static void bindMcFbo()
    {
        Framebuffer mcFbo = minecraft.getFramebuffer();

        if (mcFbo.framebufferWidth != minecraft.displayWidth || mcFbo.framebufferHeight != minecraft.displayHeight)
            mcFbo.createBindFramebuffer(minecraft.displayWidth, minecraft.displayHeight);

        mcFbo.bindFramebuffer(true);
    }
}
