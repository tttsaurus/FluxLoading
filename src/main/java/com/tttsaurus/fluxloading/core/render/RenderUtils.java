package com.tttsaurus.fluxloading.core.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL14;
import javax.annotation.Nullable;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;

import static com.tttsaurus.fluxloading.core.render.CommonBuffers.FLOAT_BUFFER_16;
import static com.tttsaurus.fluxloading.core.render.CommonBuffers.INT_BUFFER_16;

public final class RenderUtils
{
    //<editor-fold desc="gl states">
    private static int textureID = 0;
    private static float r = 0, g = 0, b = 0, a = 0;
    private static boolean blend = false;
    private static boolean lighting = false;
    private static boolean texture2D = false;
    private static boolean alphaTest = false;
    private static int shadeModel = 0;
    private static boolean depthTest = false;
    private static boolean cullFace = false;
    private static int blendSrcRgb;
    private static int blendDstRgb;
    private static int blendSrcAlpha;
    private static int blendDstAlpha;
    private static int alphaFunc;
    private static float alphaRef;
    //</editor-fold>

    //<editor-fold desc="gl state management">
    public static void storeCommonGlStates()
    {
        GL11.glGetInteger(GL11.GL_TEXTURE_BINDING_2D, INT_BUFFER_16);
        textureID = INT_BUFFER_16.get(0);
        GL11.glGetFloat(GL11.GL_CURRENT_COLOR, FLOAT_BUFFER_16);
        r = FLOAT_BUFFER_16.get(0);
        g = FLOAT_BUFFER_16.get(1);
        b = FLOAT_BUFFER_16.get(2);
        a = FLOAT_BUFFER_16.get(3);
        blend = GL11.glIsEnabled(GL11.GL_BLEND);
        lighting = GL11.glIsEnabled(GL11.GL_LIGHTING);
        texture2D = GL11.glIsEnabled(GL11.GL_TEXTURE_2D);
        alphaTest = GL11.glIsEnabled(GL11.GL_ALPHA_TEST);
        GL11.glGetInteger(GL11.GL_SHADE_MODEL, INT_BUFFER_16);
        shadeModel = INT_BUFFER_16.get(0);
        depthTest = GL11.glIsEnabled(GL11.GL_DEPTH_TEST);
        cullFace = GL11.glIsEnabled(GL11.GL_CULL_FACE);
        GL11.glGetInteger(GL14.GL_BLEND_SRC_RGB, INT_BUFFER_16);
        blendSrcRgb = INT_BUFFER_16.get(0);
        GL11.glGetInteger(GL14.GL_BLEND_DST_RGB, INT_BUFFER_16);
        blendDstRgb = INT_BUFFER_16.get(0);
        GL11.glGetInteger(GL14.GL_BLEND_SRC_ALPHA, INT_BUFFER_16);
        blendSrcAlpha = INT_BUFFER_16.get(0);
        GL11.glGetInteger(GL14.GL_BLEND_DST_ALPHA, INT_BUFFER_16);
        blendDstAlpha = INT_BUFFER_16.get(0);
        GL11.glGetInteger(GL11.GL_ALPHA_TEST_FUNC, INT_BUFFER_16);
        alphaFunc = INT_BUFFER_16.get(0);
        GL11.glGetFloat(GL11.GL_ALPHA_TEST_REF, FLOAT_BUFFER_16);
        alphaRef = FLOAT_BUFFER_16.get(0);
    }

    public static void restoreCommonGlStates()
    {
        GlStateManager.alphaFunc(alphaFunc, alphaRef);
        GlStateManager.tryBlendFuncSeparate(blendSrcRgb, blendDstRgb, blendSrcAlpha, blendDstAlpha);
        if (cullFace)
            GlStateManager.enableCull();
        else
            GlStateManager.disableCull();
        if (depthTest)
            GlStateManager.enableDepth();
        else
            GlStateManager.disableDepth();
        GlStateManager.shadeModel(shadeModel);
        if (alphaTest)
            GlStateManager.enableAlpha();
        else
            GlStateManager.disableAlpha();
        if (texture2D)
            GlStateManager.enableTexture2D();
        else
            GlStateManager.disableTexture2D();
        if (lighting)
            GlStateManager.enableLighting();
        else
            GlStateManager.disableLighting();
        if (blend)
            GlStateManager.enableBlend();
        else
            GlStateManager.disableBlend();
        GlStateManager.color(r, g, b, a);
        GlStateManager.bindTexture(textureID);
    }
    //</editor-fold>

    public static FontRenderer fontRenderer = Minecraft.getMinecraft().fontRenderer;
    public static float zLevel = 0;

    //<editor-fold desc="text">
    public static void renderText(String text, float x, float y, float scale, int color, boolean shadow)
    {
        GlStateManager.disableCull();
        GlStateManager.enableTexture2D();
        GlStateManager.disableLighting();
        GlStateManager.enableBlend();
        GlStateManager.disableAlpha();
        GlStateManager.disableDepth();
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);

        GlStateManager.pushMatrix();
        GlStateManager.translate(x, y, zLevel);
        GlStateManager.scale(scale, scale, 0);
        fontRenderer.drawString(text, 0, 0, color, shadow);
        GlStateManager.popMatrix();
    }
    //</editor-fold>

    //<editor-fold desc="png">
    @SuppressWarnings("all")
    public static void createPng(File directory, String fileName, ByteBuffer buffer, int width, int height)
    {
        if (!directory.exists())
            directory.mkdirs();

        File pngFile = new File(directory, fileName + ".png");

        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        for (int y = 0; y < height; y++)
        {
            for (int x = 0; x < width; x++)
            {
                int i = (y * width + x) * 4;
                int r = buffer.get(i) & 0xFF;
                int g = buffer.get(i + 1) & 0xFF;
                int b = buffer.get(i + 2) & 0xFF;
                int a = buffer.get(i + 3) & 0xFF;

                int argb = (a << 24) | (r << 16) | (g << 8) | b;
                image.setRGB(x, y, argb);
            }
        }

        try
        {
            ImageIO.write(image, "PNG", pngFile);
        }
        catch (IOException ignored) { }
    }
    @SuppressWarnings("all")
    public static void createPng(File directory, String fileName, BufferedImage image)
    {
        if (!directory.exists())
            directory.mkdirs();

        File pngFile = new File(directory, fileName + ".png");

        try
        {
            ImageIO.write(image, "PNG", pngFile);
        }
        catch (IOException ignored) { }
    }
    @Nullable
    public static Texture2D readPng(File png)
    {
        if (!png.exists()) return null;
        try
        {
            return createTexture2D(ImageIO.read(png));
        }
        catch (IOException ignored) { return null; }
    }
    //</editor-fold>

    //<editor-fold desc="texture">
    @Nullable
    public static Texture2D createTexture2D(BufferedImage bufferedImage)
    {
        int width = bufferedImage.getWidth();
        int height = bufferedImage.getHeight();

        if (width == 0 || height == 0) return null;

        ByteBuffer buffer = BufferUtils.createByteBuffer(width * height * 4);

        int[] pixels = new int[width * height];
        bufferedImage.getRGB(0, 0, width, height, pixels, 0, width);

        for (int y = 0; y < height; y++)
        {
            for (int x = 0; x < width; x++)
            {
                int pixel = pixels[y * width + x];
                buffer.put((byte) ((pixel >> 16) & 0xFF));  // r
                buffer.put((byte) ((pixel >> 8) & 0xFF));   // g
                buffer.put((byte) (pixel & 0xFF));          // b
                buffer.put((byte) ((pixel >> 24) & 0xFF));  // a
            }
        }
        buffer.flip();

        return new Texture2D(width, height, buffer);
    }
    //</editor-fold>

    //<editor-fold desc="camera">
    public static Vec3d getWorldOffset()
    {
        Entity camera = Minecraft.getMinecraft().getRenderViewEntity();
        if (camera == null) camera = Minecraft.getMinecraft().player;
        double partialTicks = Minecraft.getMinecraft().getRenderPartialTicks();

        double camX = camera.lastTickPosX + (camera.posX - camera.lastTickPosX) * partialTicks;
        double camY = camera.lastTickPosY + (camera.posY - camera.lastTickPosY) * partialTicks;
        double camZ = camera.lastTickPosZ + (camera.posZ - camera.lastTickPosZ) * partialTicks;

        return new Vec3d(camX, camY, camZ);
    }

    public static Vec3d getCameraPos()
    {
        Entity camera = Minecraft.getMinecraft().getRenderViewEntity();
        if (camera == null) camera = Minecraft.getMinecraft().player;
        double partialTicks = Minecraft.getMinecraft().getRenderPartialTicks();

        double camX = camera.lastTickPosX + (camera.posX - camera.lastTickPosX) * partialTicks;
        double camY = camera.lastTickPosY + (camera.posY - camera.lastTickPosY) * partialTicks + camera.getEyeHeight();
        double camZ = camera.lastTickPosZ + (camera.posZ - camera.lastTickPosZ) * partialTicks;

        return new Vec3d(camX, camY, camZ);
    }
    //</editor-fold>
}
