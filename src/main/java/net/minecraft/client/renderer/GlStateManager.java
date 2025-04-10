// Copyright Mojang Studios 2010-2025.
// Adapted from 1.12.2 source code.

package net.minecraft.client.renderer;

import org.lwjgl.opengl.GL11;

public class GlStateManager {

    private static final GlStateManager.AlphaState alphaState = new GlStateManager.AlphaState();
    private static final GlStateManager.BooleanState lightingState = new GlStateManager.BooleanState(2896);
    private static final GlStateManager.BlendState blendState;
    private static final GlStateManager.DepthState depthState;
    private static final GlStateManager.CullState cullState;
    private static int activeTextureUnit;
    private static final GlStateManager.TextureState[] textureState;

    public static void disableAlpha() {
        alphaState.alphaTest.setDisabled();
    }

    public static void disableLighting() {
        lightingState.setDisabled();
    }

    public static void disableDepth() {
        depthState.depthTest.setDisabled();
    }

    public static void enableDepth() {
        depthState.depthTest.setEnabled();
    }

    public static void disableBlend() {
        blendState.blend.setDisabled();
    }

    public static void enableBlend() {
        blendState.blend.setEnabled();
    }

    public static void tryBlendFuncSeparate(GlStateManager.SourceFactor srcFactor, GlStateManager.DestFactor dstFactor,
        GlStateManager.SourceFactor srcFactorAlpha, GlStateManager.DestFactor dstFactorAlpha) {
        tryBlendFuncSeparate(srcFactor.factor, dstFactor.factor, srcFactorAlpha.factor, dstFactorAlpha.factor);
    }

    public static void tryBlendFuncSeparate(int srcFactor, int dstFactor, int srcFactorAlpha, int dstFactorAlpha) {
        if (srcFactor != blendState.srcFactor || dstFactor != blendState.dstFactor
            || srcFactorAlpha != blendState.srcFactorAlpha
            || dstFactorAlpha != blendState.dstFactorAlpha) {
            blendState.srcFactor = srcFactor;
            blendState.dstFactor = dstFactor;
            blendState.srcFactorAlpha = srcFactorAlpha;
            blendState.dstFactorAlpha = dstFactorAlpha;
            OpenGlHelper.glBlendFunc(srcFactor, dstFactor, srcFactorAlpha, dstFactorAlpha);
        }
    }

    public static void disableCull() {
        cullState.cullFace.setDisabled();
    }

    public static void setActiveTexture(int texture) {
        if (activeTextureUnit != texture - OpenGlHelper.defaultTexUnit) {
            activeTextureUnit = texture - OpenGlHelper.defaultTexUnit;
            OpenGlHelper.setActiveTexture(texture);
        }
    }

    public static void enableTexture2D() {
        textureState[activeTextureUnit].texture2DState.setEnabled();
    }

    public static void bindTexture(int texture) {
        if (texture != textureState[activeTextureUnit].textureName) {
            textureState[activeTextureUnit].textureName = texture;
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, texture);
        }
    }

    public static void pushMatrix() {
        GL11.glPushMatrix();
    }

    public static void popMatrix() {
        GL11.glPopMatrix();
    }

    public static void scale(float x, float y, float z) {
        GL11.glScalef(x, y, z);
    }

    public static void translate(float x, float y, float z) {
        GL11.glTranslatef(x, y, z);
    }

    static {
        blendState = new GlStateManager.BlendState();
        depthState = new GlStateManager.DepthState();
        cullState = new GlStateManager.CullState();
        textureState = new GlStateManager.TextureState[32];

        for (int j = 0; j < textureState.length; ++j) {
            textureState[j] = new GlStateManager.TextureState();
        }
    }

    static class AlphaState {

        public GlStateManager.BooleanState alphaTest;
        public int func;
        public float ref;

        private AlphaState() {
            this.alphaTest = new GlStateManager.BooleanState(3008);
            this.func = 519;
            this.ref = -1.0F;
        }
    }

    static class BlendState {

        public GlStateManager.BooleanState blend;
        public int srcFactor;
        public int dstFactor;
        public int srcFactorAlpha;
        public int dstFactorAlpha;

        private BlendState() {
            this.blend = new GlStateManager.BooleanState(3042);
            this.srcFactor = 1;
            this.dstFactor = 0;
            this.srcFactorAlpha = 1;
            this.dstFactorAlpha = 0;
        }
    }

    static class BooleanState {

        private final int capability;
        private boolean currentState;

        public BooleanState(int capabilityIn) {
            this.capability = capabilityIn;
        }

        public void setDisabled() {
            this.setState(false);
        }

        public void setEnabled() {
            this.setState(true);
        }

        public void setState(boolean state) {
            if (state != this.currentState) {
                this.currentState = state;

                if (state) {
                    GL11.glEnable(this.capability);
                } else {
                    GL11.glDisable(this.capability);
                }
            }
        }
    }

    static class CullState {

        public GlStateManager.BooleanState cullFace;
        public int mode;

        private CullState() {
            this.cullFace = new GlStateManager.BooleanState(2884);
            this.mode = 1029;
        }
    }

    static class DepthState {

        public GlStateManager.BooleanState depthTest;
        public boolean maskEnabled;
        public int depthFunc;

        private DepthState() {
            this.depthTest = new GlStateManager.BooleanState(2929);
            this.maskEnabled = true;
            this.depthFunc = 513;
        }
    }

    public enum DestFactor {

        ONE_MINUS_SRC_ALPHA(771),
        ZERO(0);

        public final int factor;

        DestFactor(int factorIn) {
            this.factor = factorIn;
        }
    }

    public enum SourceFactor {

        ONE(1),
        SRC_ALPHA(770);

        public final int factor;

        SourceFactor(int factorIn) {
            this.factor = factorIn;
        }
    }

    static class TextureState {

        public GlStateManager.BooleanState texture2DState;
        public int textureName;

        private TextureState() {
            this.texture2DState = new GlStateManager.BooleanState(3553);
        }
    }
}
