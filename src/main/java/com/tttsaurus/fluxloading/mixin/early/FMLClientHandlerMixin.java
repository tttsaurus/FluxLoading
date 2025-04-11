package com.tttsaurus.fluxloading.mixin.early;

import net.minecraft.client.Minecraft;
import net.minecraft.world.WorldSettings;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.tttsaurus.fluxloading.FluxLoadingConfig;
import com.tttsaurus.fluxloading.core.WorldLoadingScreenOverhaul;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;

@Mixin(value = FMLClientHandler.class, remap = false)
public class FMLClientHandlerMixin {

    @WrapOperation(
        method = "tryLoadExistingWorld",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/Minecraft;launchIntegratedServer(Ljava/lang/String;Ljava/lang/String;Lnet/minecraft/world/WorldSettings;)V",
            remap = true))
    public void mixinTryLoadExistingWorld(Minecraft instance, String crashreportcategory, String throwable,
        WorldSettings s2, Operation<Void> original, @Local(name = "dirName") String dirName) {
        if (FMLCommonHandler.instance()
            .getEffectiveSide() == Side.CLIENT) {
            // join world
            WorldLoadingScreenOverhaul.resetShader();
            WorldLoadingScreenOverhaul.setDrawOverlay(true);

            if (FluxLoadingConfig.INSTANTLY_POPPED_UP_LOADING_TITLE) {
                WorldLoadingScreenOverhaul.setForceLoadingTitle(true);
            }

            // try load screenshot
            WorldLoadingScreenOverhaul.tryReadFromLocal(dirName);

            WorldLoadingScreenOverhaul.setFinishedLoadingChunks(false);
            WorldLoadingScreenOverhaul.resetChunkLoadedNum();
            WorldLoadingScreenOverhaul.resetFadeOutTimer();
            WorldLoadingScreenOverhaul.resetTargetChunkNum();
            WorldLoadingScreenOverhaul.setCountingChunkLoaded(true);
        }
        original.call(instance, crashreportcategory, throwable, s2);
    }
}
