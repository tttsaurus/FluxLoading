package com.tttsaurus.fluxloading.mixin.early;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.tttsaurus.fluxloading.core.WorldLoadingScreenOverhaul;
import net.minecraft.world.storage.WorldSummary;
import net.minecraftforge.fml.client.FMLClientHandler;
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
    public String mixin_tryLoadExistingWorld_WorldSummary$getFileName(WorldSummary instance, Operation<String> original)
    {
        String folderName = original.call(instance);

        // join world
        WorldLoadingScreenOverhaul.setDrawOverlay(true);

        // try load screenshot
        WorldLoadingScreenOverhaul.tryReadFromLocal(folderName);

        return folderName;
    }
}
