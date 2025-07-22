package com.tttsaurus.fluxloading.mixin.early;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.tttsaurus.fluxloading.core.FluxLoadingAPI;
import com.tttsaurus.fluxloading.core.FluxLoadingManager;
import com.tttsaurus.fluxloading.core.accessor.TessellatorAccessor;
import com.tttsaurus.fluxloading.core.render.RenderUtils;
import net.minecraft.client.LoadingScreenRenderer;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldVertexBufferUploader;
import net.minecraft.client.resources.I18n;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(LoadingScreenRenderer.class)
public class LoadingScreenRendererMixin
{
    @Unique
    private static BufferBuilder fluxloading$proxyBufferBuilder;

    @WrapOperation(
            method = "setLoadingProgress",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/renderer/Tessellator;getBuffer()Lnet/minecraft/client/renderer/BufferBuilder;"
            ))
    public BufferBuilder getBuffer(Tessellator instance, Operation<BufferBuilder> original)
    {
        if (FluxLoadingAPI.isActive())
        {
            if (fluxloading$proxyBufferBuilder == null)
                fluxloading$proxyBufferBuilder = new BufferBuilder(64);
            return fluxloading$proxyBufferBuilder;
        }

        return original.call(instance);
    }

    @WrapOperation(
            method = "setLoadingProgress",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/renderer/Tessellator;draw()V",
                    ordinal = 0
            ))
    public void draw0(Tessellator instance, Operation<Void> original)
    {
        if (FluxLoadingAPI.isActive())
        {
            FluxLoadingManager.drawOverlayDefaultWorldLoadingAndFadingInPhase();
            FluxLoadingManager.tick();

            fluxloading$proxyBufferBuilder.finishDrawing();
            fluxloading$proxyBufferBuilder.reset();
        }
        else
            original.call(instance);
    }

    @WrapOperation(
            method = "setLoadingProgress",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/renderer/Tessellator;draw()V",
                    ordinal = 1
            ))
    public void draw1(Tessellator instance, Operation<Void> original)
    {
        if (FluxLoadingAPI.isActive())
        {
            Tessellator tessellator = Tessellator.getInstance();
            WorldVertexBufferUploader vboUploader = TessellatorAccessor.getVboUploader(tessellator);
            fluxloading$proxyBufferBuilder.finishDrawing();
            vboUploader.draw(fluxloading$proxyBufferBuilder);
            fluxloading$proxyBufferBuilder.reset();
        }
        else
            original.call(instance);
    }

    @WrapOperation(
            method = "setLoadingProgress",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/FontRenderer;drawStringWithShadow(Ljava/lang/String;FFI)I",
                    ordinal = 0
            ))
    public int drawStringWithShadow0(FontRenderer instance, String text, float x, float y, int color, Operation<Integer> original)
    {
        if (FluxLoadingAPI.isActive() && FluxLoadingManager.isDisableVanillaTexts()) return 0;

        int res = original.call(instance, text, x, y, color);

        if (FluxLoadingAPI.isActive())
        {
            if (FluxLoadingManager.isForceLoadingTitle() && text != null && !text.isEmpty())
                FluxLoadingManager.setForceLoadingTitle(false);
            if (FluxLoadingManager.isForceLoadingTitle())
            {
                String i18nText = I18n.format("menu.loadingLevel");
                int width = RenderUtils.fontRenderer.getStringWidth(i18nText);
                RenderUtils.renderText(i18nText, x - (int)(width / 2f), y, 1, color, true);
            }
        }

        return res;
    }

    @WrapOperation(
            method = "setLoadingProgress",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/FontRenderer;drawStringWithShadow(Ljava/lang/String;FFI)I",
                    ordinal = 1
            ))
    public int drawStringWithShadow1(FontRenderer instance, String text, float x, float y, int color, Operation<Integer> original)
    {
        if (FluxLoadingAPI.isActive() && FluxLoadingManager.isDisableVanillaTexts()) return 0;

        return original.call(instance, text, x, y, color);
    }
}
