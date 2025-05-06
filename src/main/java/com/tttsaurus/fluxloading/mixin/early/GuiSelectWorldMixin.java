package com.tttsaurus.fluxloading.mixin.early;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiSelectWorld;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.SaveFormatComparator;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.tttsaurus.fluxloading.FluxLoading;
import com.tttsaurus.fluxloading.FluxLoadingConfig;
import com.tttsaurus.fluxloading.core.WorldLoadingScreenOverhaul;
import com.tttsaurus.fluxloading.util.ScreenshotHelper;

@SuppressWarnings("unused")
@Mixin(targets = "net.minecraft.client.gui.GuiSelectWorld$List")
public class GuiSelectWorldMixin {

    @ModifyVariable(method = "drawSlot(IIIILnet/minecraft/client/renderer/Tessellator;II)V", at = @At("HEAD"), index = 2 // p_148126_2_
    )
    private int modifyDrawSlotX(int original) {
        return FluxLoadingConfig.ENABLE_THUMBNAIL ? original + 36 : original;
    }

    @Shadow(remap = false)
    @Final
    GuiSelectWorld this$0;

    @Inject(method = "drawSlot(IIIILnet/minecraft/client/renderer/Tessellator;II)V", at = @At("HEAD"))
    private void onDrawSlot(int index, int x, int y, int height, Tessellator tessellator, int mouseX, int mouseY,
        CallbackInfo ci) {
        if (!FluxLoadingConfig.ENABLE_THUMBNAIL) {
            return;
        }
        java.util.List saves = ((GuiSelectWorldAccessor) this$0).getField_146639_s();
        if (index < 0 || index >= saves.size()) return;

        SaveFormatComparator save = (SaveFormatComparator) saves.get(index);
        String folderName = save.getFileName();

        ResourceLocation screenshot = ScreenshotHelper.getOrLoadScreenshot(
            folderName,
            WorldLoadingScreenOverhaul.THUMBNAIL_NAME,
            FluxLoadingConfig.THUMBNAIL_SIZE,
            FluxLoadingConfig.THUMBNAIL_SIZE);
        Minecraft mc = Minecraft.getMinecraft();
        if (screenshot != null) {
            mc.getTextureManager()
                .bindTexture(screenshot);
        } else {
            mc.getTextureManager()
                .bindTexture(FluxLoading.noThumbnailRl);
        }
        Gui.func_146110_a(x - 36, y, 0, 0, 32, 32, 32, 32);
    }
}
