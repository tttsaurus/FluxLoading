package com.tttsaurus.fluxloading.mixin.early;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.tttsaurus.fluxloading.core.WorldLoadingScreenOverhaul;
import net.minecraft.client.LoadingScreenRenderer;
import net.minecraft.client.renderer.Tessellator;
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
    public void mixin_setLoadingProgress_Tessellator$draw(Tessellator instance, Operation<Void> original)
    {
        original.call(instance);

        if (WorldLoadingScreenOverhaul.getDrawOverlay() && WorldLoadingScreenOverhaul.isTextureAvailable())
            WorldLoadingScreenOverhaul.drawOverlay();
    }
}
