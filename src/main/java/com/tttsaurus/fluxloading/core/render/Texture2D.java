package com.tttsaurus.fluxloading.core.render;

import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;

public final class Texture2D implements IGlDisposable
{
    private static final IntBuffer intBuffer = ByteBuffer.allocateDirect(16 << 2).order(ByteOrder.nativeOrder()).asIntBuffer();

    private int glTextureID = 0;
    private final int width;
    private final int height;
    private boolean isGlBounded;

    public int getGlTextureID() { return glTextureID; }
    public int getWidth() { return width; }
    public int getHeight() { return height; }
    public boolean getIsGlBounded() { return isGlBounded; }

    public Texture2D(int width, int height, ByteBuffer byteBuffer)
    {
        GL11.glGetInteger(GL11.GL_TEXTURE_BINDING_2D, intBuffer);
        int textureID = intBuffer.get(0);

        glTextureID = GL11.glGenTextures();
        this.width = width;
        this.height = height;

        GL11.glBindTexture(GL11.GL_TEXTURE_2D, glTextureID);

        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE);

        GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA8, width, height, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, byteBuffer);

        isGlBounded = true;

        GlStateManager.bindTexture(textureID);

        GlResourceManager.addDisposable(this);
    }

    public void dispose()
    {
        if (glTextureID != 0) GL11.glDeleteTextures(glTextureID);
        glTextureID = 0;
        isGlBounded = false;
        GlResourceManager.removeDisposable(this);
    }
}
