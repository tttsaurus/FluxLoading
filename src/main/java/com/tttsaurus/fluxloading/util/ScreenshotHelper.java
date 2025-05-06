package com.tttsaurus.fluxloading.util;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.IntBuffer;

import javax.imageio.ImageIO;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import com.tttsaurus.fluxloading.FluxLoading;

@SuppressWarnings("DuplicatedCode")
public class ScreenshotHelper {
    // Code adapted from Minecraft's ScreenshotHelper class
    // Copyright Mojang Studios, 2010-2025.

    /** A buffer to hold pixel values returned by OpenGL. */
    private static IntBuffer pixelBuffer;
    /** The built-up array that contains all the pixel values returned by OpenGL. */
    private static int[] pixelValues;

    public static BufferedImage saveScreenshot(int p_148259_2_, int p_148259_3_, Framebuffer p_148259_4_) {
        if (OpenGlHelper.isFramebufferEnabled()) {
            p_148259_2_ = p_148259_4_.framebufferTextureWidth;
            p_148259_3_ = p_148259_4_.framebufferTextureHeight;
        }

        int k = p_148259_2_ * p_148259_3_;

        if (pixelBuffer == null || pixelBuffer.capacity() < k) {
            pixelBuffer = BufferUtils.createIntBuffer(k);
            pixelValues = new int[k];
        }

        GL11.glPixelStorei(GL11.GL_PACK_ALIGNMENT, 1);
        GL11.glPixelStorei(GL11.GL_UNPACK_ALIGNMENT, 1);
        pixelBuffer.clear();

        if (OpenGlHelper.isFramebufferEnabled()) {
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, p_148259_4_.framebufferTexture);
            GL11.glGetTexImage(GL11.GL_TEXTURE_2D, 0, GL12.GL_BGRA, GL12.GL_UNSIGNED_INT_8_8_8_8_REV, pixelBuffer);
        } else {
            GL11.glReadPixels(
                0,
                0,
                p_148259_2_,
                p_148259_3_,
                GL12.GL_BGRA,
                GL12.GL_UNSIGNED_INT_8_8_8_8_REV,
                pixelBuffer);
        }

        pixelBuffer.get(pixelValues);
        TextureUtil.func_147953_a(pixelValues, p_148259_2_, p_148259_3_);
        BufferedImage bufferedimage;
        if (OpenGlHelper.isFramebufferEnabled()) {
            bufferedimage = new BufferedImage(p_148259_4_.framebufferWidth, p_148259_4_.framebufferHeight, 1);
            int l = p_148259_4_.framebufferTextureHeight - p_148259_4_.framebufferHeight;

            for (int i1 = l; i1 < p_148259_4_.framebufferTextureHeight; ++i1) {
                for (int j1 = 0; j1 < p_148259_4_.framebufferWidth; ++j1) {
                    bufferedimage.setRGB(j1, i1 - l, pixelValues[i1 * p_148259_4_.framebufferTextureWidth + j1]);
                }
            }
        } else {
            bufferedimage = new BufferedImage(p_148259_2_, p_148259_3_, 1);
            bufferedimage.setRGB(0, 0, p_148259_2_, p_148259_3_, pixelValues, 0, p_148259_2_);
        }
        return bufferedimage;
    }

    public static BufferedImage scaleAndCropToResolution(BufferedImage source, int targetWidth, int targetHeight) {
        int srcWidth = source.getWidth();
        int srcHeight = source.getHeight();

        double scale = Math.max((double) targetWidth / srcWidth, (double) targetHeight / srcHeight);

        int scaledWidth = (int) (scale * srcWidth);
        int scaledHeight = (int) (scale * srcHeight);

        Image scaled = source.getScaledInstance(scaledWidth, scaledHeight, Image.SCALE_SMOOTH);

        BufferedImage output = new BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = output.createGraphics();

        int x = (scaledWidth - targetWidth) / 2;
        int y = (scaledHeight - targetHeight) / 2;

        g2d.drawImage(scaled, -x, -y, null);
        g2d.dispose();

        return output;
    }

    public static ResourceLocation getOrLoadScreenshot(String worldName, String screenShotName, int targetWidth,
        int targetHeight) {
        String cacheKey = worldName + "_" + screenShotName;
        return FluxLoading.screenshotCache.computeIfAbsent(cacheKey, key -> {
            File screenshot = new File(
                Minecraft.getMinecraft().mcDataDir,
                "saves/" + worldName + "/" + screenShotName + ".png");
            if (!screenshot.exists()) return null;

            try {
                BufferedImage image = ImageIO.read(screenshot);
                if (image == null) return null;

                BufferedImage resized = new BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_ARGB);
                Graphics2D g = resized.createGraphics();
                g.drawImage(image, 0, 0, targetWidth, targetHeight, null);
                g.dispose();

                DynamicTexture texture = new DynamicTexture(resized);

                return Minecraft.getMinecraft()
                    .getTextureManager()
                    .getDynamicTextureLocation("screenshot_" + cacheKey, texture);
            } catch (IOException e) {
                FluxLoading.logger.error(e.getMessage(), e);
                return null;
            }
        });
    }
}
