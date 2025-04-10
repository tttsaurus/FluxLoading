package com.tttsaurus.fluxloading.mixin.early;

import net.minecraft.client.LoadingScreenRenderer;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.resources.I18n;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.tttsaurus.fluxloading.core.WorldLoadingScreenOverhaul;
import com.tttsaurus.fluxloading.render.RenderUtils;

@Mixin(LoadingScreenRenderer.class)
public class LoadingScreenRendererMixin {

    @WrapOperation(
        method = "setLoadingProgress",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/Tessellator;draw()I", ordinal = 0))
    public int mixin_setLoadingProgress_Tessellator$draw(Tessellator instance, Operation<Void> original) {
        original.call(instance);

        if (WorldLoadingScreenOverhaul.getDrawOverlay() && WorldLoadingScreenOverhaul.isTextureAvailable())
            WorldLoadingScreenOverhaul.drawOverlay();
        return 0;
    }

    @WrapOperation(
        method = "setLoadingProgress",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/gui/FontRenderer;drawStringWithShadow(Ljava/lang/String;III)I",
            ordinal = 0))
    public int mixin_setLoadingProgress_FontRenderer$drawStringWithShadow(FontRenderer instance, String text, int x, int y, int color, Operation<Integer> original) {
        int res = original.call(instance, text, x, y, color);

        if (WorldLoadingScreenOverhaul.getDrawOverlay()) {
            if (WorldLoadingScreenOverhaul.getForceLoadingTitle() && text != null && !text.isEmpty())
                WorldLoadingScreenOverhaul.setForceLoadingTitle(false);
            if (WorldLoadingScreenOverhaul.getForceLoadingTitle()) {
                String i18nText = I18n.format("menu.loadingLevel");
                int width = RenderUtils.fontRenderer.getStringWidth(i18nText);
                RenderUtils.renderText(i18nText, x - (int) (width / 2f), y, 1, color, true);
            }
        }

        return res;
    }
}
