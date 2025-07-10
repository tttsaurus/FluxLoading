package com.tttsaurus.fluxloading.mixin.early;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.tttsaurus.fluxloading.FluxLoadingConfig;
import com.tttsaurus.fluxloading.core.FluxLoadingAPI;
import com.tttsaurus.fluxloading.core.FluxLoadingManager;
import com.tttsaurus.fluxloading.function.Action_1Param;
import com.tttsaurus.fluxloading.util.AccessorUnreflector;
import net.minecraft.world.storage.WorldSummary;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.common.FMLCommonHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@SuppressWarnings("all")
@Mixin(FMLClientHandler.class)
public class FMLClientHandlerMixin
{
    @Unique
    private Action_1Param<Boolean> fluxloading$duringDefaultWorldLoadingPhaseSetter;
    @Unique
    private Action_1Param<Boolean> fluxloading$duringExtraChunkLoadingPhaseSetter;
    @Unique
    private Action_1Param<Boolean> fluxloading$duringExtraWaitPhaseSetter;
    @Unique
    private Action_1Param<Boolean> fluxloading$duringFadingOutPhaseSetter;
    @Unique
    private Action_1Param<Boolean> fluxloading$finishLoadingSetter;

    @WrapOperation(
            method = "tryLoadExistingWorld",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/storage/WorldSummary;getFileName()Ljava/lang/String;",
                    ordinal = 0
            ))
    public String getFileName(WorldSummary instance, Operation<String> original)
    {
        // join world
        if (FMLCommonHandler.instance().getSide().isClient())
        {
            String folderName = original.call(instance);

            if (fluxloading$duringDefaultWorldLoadingPhaseSetter == null)
                fluxloading$duringDefaultWorldLoadingPhaseSetter = (Action_1Param<Boolean>)AccessorUnreflector.getDeclaredFieldSetter(FluxLoadingAPI.class, "duringDefaultWorldLoadingPhase");
            if (fluxloading$duringExtraChunkLoadingPhaseSetter == null)
                fluxloading$duringExtraChunkLoadingPhaseSetter = (Action_1Param<Boolean>)AccessorUnreflector.getDeclaredFieldSetter(FluxLoadingAPI.class, "duringExtraChunkLoadingPhase");
            if (fluxloading$duringExtraWaitPhaseSetter == null)
                fluxloading$duringExtraWaitPhaseSetter = (Action_1Param<Boolean>)AccessorUnreflector.getDeclaredFieldSetter(FluxLoadingAPI.class, "duringExtraWaitPhase");
            if (fluxloading$duringFadingOutPhaseSetter == null)
                fluxloading$duringFadingOutPhaseSetter = (Action_1Param<Boolean>)AccessorUnreflector.getDeclaredFieldSetter(FluxLoadingAPI.class, "duringFadingOutPhase");
            if (fluxloading$finishLoadingSetter == null)
                fluxloading$finishLoadingSetter = (Action_1Param<Boolean>)AccessorUnreflector.getDeclaredFieldSetter(FluxLoadingAPI.class, "finishLoading");

            fluxloading$duringDefaultWorldLoadingPhaseSetter.invoke(false);
            fluxloading$duringExtraChunkLoadingPhaseSetter.invoke(false);
            fluxloading$duringExtraWaitPhaseSetter.invoke(false);
            fluxloading$duringFadingOutPhaseSetter.invoke(false);
            fluxloading$finishLoadingSetter.invoke(false);

            FluxLoadingManager.resetShader();
            FluxLoadingManager.setActive(true);

            if (FluxLoadingConfig.INSTANTLY_POPPED_UP_LOADING_TITLE)
                FluxLoadingManager.setForceLoadingTitle(true);

            // try load screenshot
            FluxLoadingManager.tryReadFromLocal(folderName);

            FluxLoadingManager.setTargetChunkNumCalculated(false);
            FluxLoadingManager.setStartCalcTargetChunkNum(false);
            FluxLoadingManager.resetChunkLoadedNum();
            FluxLoadingManager.resetTargetChunkNum();
            FluxLoadingManager.resetFadeOutTimer();
            FluxLoadingManager.resetMovementLocked();
            FluxLoadingManager.setFinishChunkLoading(false);
            FluxLoadingManager.setCountingChunkLoaded(true);

            return folderName;
        }
        else
            return original.call(instance);
    }
}
