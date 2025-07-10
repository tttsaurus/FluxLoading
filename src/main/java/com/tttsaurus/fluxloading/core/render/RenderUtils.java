package com.tttsaurus.fluxloading.core.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.BufferUtils;
import javax.annotation.Nullable;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;

public final class RenderUtils
{
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
}
