package com.tttsaurus.fluxloading.mixin.early;

import com.tttsaurus.fluxloading.util.MixinHelpers;
import net.minecraft.client.LoadingScreenRenderer;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.Tessellator;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;

@Mixin(LoadingScreenRenderer.class)
public class LoadingScreenRendererMixin {

    @WrapOperation(
        method = "setLoadingProgress",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/Tessellator;draw()I", ordinal = 0))
    public int mixin_setLoadingProgress_Tessellator$draw(Tessellator instance, Operation<Integer> original) {
        return MixinHelpers.mixinSetLoadingProgress$draw(instance, original);
    }

    @WrapOperation(
        method = "setLoadingProgress",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/gui/FontRenderer;drawStringWithShadow(Ljava/lang/String;III)I",
            ordinal = 0))
    public int mixin_setLoadingProgress_FontRenderer$drawStringWithShadow(FontRenderer instance, String text, int x,
        int y, int color, Operation<Integer> original) {
        return MixinHelpers.mixinSetLoadingProgress$drawStringWithShadow(instance, text, x, y, color, original);

    }
}
