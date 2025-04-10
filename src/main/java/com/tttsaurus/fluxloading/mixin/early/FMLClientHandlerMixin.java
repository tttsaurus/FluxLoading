package com.tttsaurus.fluxloading.mixin.early;

import net.minecraft.client.gui.GuiSelectWorld;

import org.spongepowered.asm.mixin.Mixin;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.tttsaurus.fluxloading.FluxLoadingConfig;
import com.tttsaurus.fluxloading.core.WorldLoadingScreenOverhaul;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;

@Mixin(value = FMLClientHandler.class, remap = false)
public class FMLClientHandlerMixin {

    @WrapMethod(method = "tryLoadExistingWorld")
    public void mixinTryLoadExistingWorld(GuiSelectWorld selectWorldGUI, String dirName, String saveName,
        Operation<Void> original) {
        if (FMLCommonHandler.instance()
            .getEffectiveSide() == Side.CLIENT) {
            // join world
            WorldLoadingScreenOverhaul.resetShader();
            WorldLoadingScreenOverhaul.setDrawOverlay(true);

            if (FluxLoadingConfig.INSTANTLY_POPPED_UP_LOADING_TITLE)
                WorldLoadingScreenOverhaul.setForceLoadingTitle(true);

            // try load screenshot
            WorldLoadingScreenOverhaul.tryReadFromLocal(dirName);

            WorldLoadingScreenOverhaul.setFinishedLoadingChunks(false);
            WorldLoadingScreenOverhaul.resetChunkLoadedNum();
            WorldLoadingScreenOverhaul.resetFadeOutTimer();
            WorldLoadingScreenOverhaul.resetTargetChunkNum();
            WorldLoadingScreenOverhaul.setCountingChunkLoaded(true);
        }
        original.call(selectWorldGUI, dirName, saveName);
    }
}
