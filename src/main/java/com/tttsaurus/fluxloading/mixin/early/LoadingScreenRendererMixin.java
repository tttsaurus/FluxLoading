package com.tttsaurus.fluxloading.mixin.early;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.tttsaurus.fluxloading.core.FluxLoadingManager;
import com.tttsaurus.fluxloading.core.render.RenderUtils;
import net.minecraft.client.LoadingScreenRenderer;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.resources.I18n;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(LoadingScreenRenderer.class)
public class LoadingScreenRendererMixin
{
    @WrapOperation(
            method = "setLoadingProgress",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/renderer/Tessellator;draw()V",
                    ordinal = 0
            ))
    public void draw(Tessellator instance, Operation<Void> original)
    {
        original.call(instance);

        if (FluxLoadingManager.isActive() && FluxLoadingManager.isTextureAvailable())
            FluxLoadingManager.drawOverlay();
    }

    @WrapOperation(
            method = "setLoadingProgress",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/FontRenderer;drawStringWithShadow(Ljava/lang/String;FFI)I",
                    ordinal = 0
            ))
    public int drawStringWithShadow(FontRenderer instance, String text, float x, float y, int color, Operation<Integer> original)
    {
        int res = original.call(instance, text, x, y, color);

        if (FluxLoadingManager.isActive())
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
}
