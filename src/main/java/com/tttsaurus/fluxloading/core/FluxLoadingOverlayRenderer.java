package com.tttsaurus.fluxloading.core;

import com.tttsaurus.fluxloading.core.render.CommonBuffers;
import com.tttsaurus.fluxloading.core.render.Texture2D;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;

public final class FluxLoadingOverlayRenderer
{
    public void render(Texture2D texture, boolean setPercentage, float percentage)
    {
        if (texture == null) return;

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

        ShaderResources.getShaderProgram().use();

        if (setPercentage)
        {
            ShaderResources.getShaderProgram().setUniform("percentage", percentage);
        }

        ScaledResolution resolution = new ScaledResolution(Minecraft.getMinecraft());
        ShaderResources.getShaderProgram().setUniform("resolution",
                (float)resolution.getScaledWidth_double(),
                (float)resolution.getScaledHeight_double());

        ShaderResources.triggerShader();
        ShaderResources.getShaderProgram().unuse();

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
}
