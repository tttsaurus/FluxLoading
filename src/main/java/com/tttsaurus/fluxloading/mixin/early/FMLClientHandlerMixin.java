package com.tttsaurus.fluxloading.mixin.early;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.tttsaurus.fluxloading.FluxLoadingConfig;
import com.tttsaurus.fluxloading.core.FluxLoadingManager;
import net.minecraft.world.storage.WorldSummary;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.common.FMLCommonHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(FMLClientHandler.class)
public class FMLClientHandlerMixin
{
    @WrapOperation(
            method = "tryLoadExistingWorld",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/storage/WorldSummary;getFileName()Ljava/lang/String;",
                    ordinal = 0
            ))
    public String getFileName(WorldSummary instance, Operation<String> original)
    {
        if (FMLCommonHandler.instance().getSide().isClient())
        {
            String folderName = original.call(instance);

            // join world
            FluxLoadingManager.resetShader();
            FluxLoadingManager.setDrawOverlay(true);

            if (FluxLoadingConfig.INSTANTLY_POPPED_UP_LOADING_TITLE)
                FluxLoadingManager.setForceLoadingTitle(true);

            // try load screenshot
            FluxLoadingManager.tryReadFromLocal(folderName);

            FluxLoadingManager.setTargetChunkNumCalculated(false);
            FluxLoadingManager.setStartCalcTargetChunkNum(false);
            FluxLoadingManager.resetChunkLoadedNum();
            FluxLoadingManager.resetTargetChunkNum();
            FluxLoadingManager.resetFadeOutTimer();
            FluxLoadingManager.setFinishChunkLoading(false);
            FluxLoadingManager.setCountingChunkLoaded(true);

            return folderName;
        }
        else
            return original.call(instance);
    }
}
