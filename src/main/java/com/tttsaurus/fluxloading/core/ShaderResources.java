package com.tttsaurus.fluxloading.core;

import com.tttsaurus.fluxloading.FluxLoading;
import com.tttsaurus.fluxloading.FluxLoadingConfig;
import com.tttsaurus.fluxloading.core.render.CommonBuffers;
import com.tttsaurus.fluxloading.core.render.shader.Shader;
import com.tttsaurus.fluxloading.core.render.shader.ShaderLoader;
import com.tttsaurus.fluxloading.core.render.shader.ShaderProgram;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

public class ShaderResources
{
    private static ShaderProgram shaderProgram = null;
    private static FloatBuffer vertexBuffer;

    public static ShaderProgram getShaderProgram()
    {
        return shaderProgram;
    }

    public static void triggerShader()
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

    public static void initShader()
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
            shaderProgram.setUniform("enableDissolving", false);
            shaderProgram.setUniform("enableWaving", false);
            shaderProgram.setUniform("enableDarkOverlay", FluxLoadingConfig.ENABLE_DARK_OVERLAY);
            shaderProgram.setUniform("enable3x3Blur", false);
            shaderProgram.setUniform("enable5x5Blur", false);
            shaderProgram.setUniform("enableKawaseBlur", false);
            shaderProgram.setUniform("targetBlurStrength", 1f);
            if (FluxLoadingConfig.ENABLE_BLUR)
            {
                switch (FluxLoadingConfig.BLUR_ALGORITHM)
                {
                    case "3x3_gaussian_blur" -> { shaderProgram.setUniform("enable3x3Blur", true); }
                    case "5x5_gaussian_blur" -> { shaderProgram.setUniform("enable5x5Blur", true); }
                    case "kawase_blur" -> { shaderProgram.setUniform("enableKawaseBlur", true); }
                }
                shaderProgram.setUniform("targetBlurStrength", FluxLoadingConfig.BLUR_STRENGTH);
            }
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
            shaderProgram.setUniform("percentage", 1f);
            shaderProgram.unuse();
        }
    }

    public static void setShaderFadingState(boolean state)
    {
        if (shaderProgram != null)
        {
            shaderProgram.use();
            // fade-in
            if (state)
            {
                shaderProgram.setUniform("enableDissolving", FluxLoadingConfig.ENABLE_FADEIN_DISSOLVING_EFFECT);
                shaderProgram.setUniform("enableWaving", FluxLoadingConfig.ENABLE_FADEIN_WAVING_EFFECT);
            }
            // fade-out
            else
            {
                shaderProgram.setUniform("enableDissolving", FluxLoadingConfig.ENABLE_FADEOUT_DISSOLVING_EFFECT);
                shaderProgram.setUniform("enableWaving", FluxLoadingConfig.ENABLE_FADEOUT_WAVING_EFFECT);
            }
            shaderProgram.unuse();
        }
    }
}
