package com.tttsaurus.fluxloading.util;

import java.awt.image.BufferedImage;
import java.nio.IntBuffer;

import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.shader.Framebuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

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
}
