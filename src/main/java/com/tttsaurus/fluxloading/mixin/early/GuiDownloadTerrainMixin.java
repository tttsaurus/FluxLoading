package com.tttsaurus.fluxloading.mixin.early;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.tttsaurus.fluxloading.core.FluxLoadingAPI;
import com.tttsaurus.fluxloading.core.FluxLoadingManager;
import net.minecraft.client.gui.FontRenderer;
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
        if (FluxLoadingAPI.isActive())
        {
            FluxLoadingManager.drawOverlayDefaultWorldLoadingAndFadingInPhase();
            FluxLoadingManager.tick();
        }
        else
            original.call(instance, i);
    }

    @WrapOperation(
            method = "drawScreen",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/GuiDownloadTerrain;drawCenteredString(Lnet/minecraft/client/gui/FontRenderer;Ljava/lang/String;III)V"
            ))
    public void drawCenteredString(GuiDownloadTerrain instance, FontRenderer fontRenderer, String s, int i0, int i1, int i2, Operation<Void> original)
    {
        if (FluxLoadingAPI.isActive() && FluxLoadingManager.isDisableVanillaTexts()) return;

        original.call(instance, fontRenderer, s, i0, i1, i2);
    }
}
