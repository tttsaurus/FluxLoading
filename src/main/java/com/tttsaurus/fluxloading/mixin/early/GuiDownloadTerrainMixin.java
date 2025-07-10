package com.tttsaurus.fluxloading.mixin.early;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.tttsaurus.fluxloading.core.FluxLoadingManager;
import net.minecraft.client.gui.GuiDownloadTerrain;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(GuiDownloadTerrain.class)
public class GuiDownloadTerrainMixin
{
    @WrapOperation(
            method = "drawScreen",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/GuiDownloadTerrain;drawBackground(I)V"
            ))
    public void drawBackground(GuiDownloadTerrain instance, int i, Operation<Void> original)
    {
        original.call(instance, i);

        if (FluxLoadingManager.isActive() && FluxLoadingManager.isTextureAvailable())
            FluxLoadingManager.drawOverlay();
    }
}
