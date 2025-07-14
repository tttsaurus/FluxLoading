package com.tttsaurus.fluxloading.mixin.early;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.tttsaurus.fluxloading.FluxLoadingConfig;
import com.tttsaurus.fluxloading.core.FluxLoadingAPI;
import com.tttsaurus.fluxloading.core.FluxLoadingManager;
import com.tttsaurus.fluxloading.core.ShaderResources;
import com.tttsaurus.fluxloading.core.function.Action_1Param;
import com.tttsaurus.fluxloading.core.function.Func;
import com.tttsaurus.fluxloading.core.util.AccessorUnreflector;
import net.minecraft.client.Minecraft;
import net.minecraft.world.storage.WorldSummary;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.common.FMLCommonHandler;
import org.apache.commons.lang3.time.StopWatch;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import java.util.List;

@SuppressWarnings("all")
@Mixin(FMLClientHandler.class)
public class FMLClientHandlerMixin
{
    @Unique
    private Action_1Param<Boolean> fluxloading$duringFadingInPhaseSetter;
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
    @Unique
    private Action_1Param<Integer> fluxloading$tickNum;
    @Unique
    private Func<List<Runnable>> fluxloading$fluxLoadingStartListenersGetter;
    @Unique
    private Action_1Param<StopWatch> fluxloading$stopWatchSetter;

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

            if (fluxloading$duringFadingInPhaseSetter == null)
                fluxloading$duringFadingInPhaseSetter = (Action_1Param<Boolean>)AccessorUnreflector.getDeclaredFieldSetter(FluxLoadingAPI.class, "duringFadingInPhase");
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
            if (fluxloading$tickNum == null)
                fluxloading$tickNum = (Action_1Param<Integer>)AccessorUnreflector.getDeclaredFieldSetter(FluxLoadingAPI.class, "tickNum");
            if (fluxloading$fluxLoadingStartListenersGetter == null)
                fluxloading$fluxLoadingStartListenersGetter = (Func<List<Runnable>>)AccessorUnreflector.getDeclaredFieldGetter(FluxLoadingAPI.class, "fluxLoadingStartListeners");
            if (fluxloading$stopWatchSetter == null)
                fluxloading$stopWatchSetter = (Action_1Param<StopWatch>)AccessorUnreflector.getDeclaredFieldSetter(FluxLoadingAPI.class, "stopWatch");

            fluxloading$duringFadingInPhaseSetter.invoke(false);
            fluxloading$duringDefaultWorldLoadingPhaseSetter.invoke(false);
            fluxloading$duringExtraChunkLoadingPhaseSetter.invoke(false);
            fluxloading$duringExtraWaitPhaseSetter.invoke(false);
            fluxloading$duringFadingOutPhaseSetter.invoke(false);
            fluxloading$finishLoadingSetter.invoke(false);
            fluxloading$tickNum.invoke(0);

            // try load screenshot
            FluxLoadingManager.tryReadFromLocal(folderName);

            if (FluxLoadingManager.isTextureAvailable())
            {
                ShaderResources.initShader();
                ShaderResources.resetShader();
                ShaderResources.setShaderFadingState(true);

                FluxLoadingManager.setActive(true);

                if (FluxLoadingConfig.INSTANTLY_POPPED_UP_LOADING_TITLE)
                    FluxLoadingManager.setForceLoadingTitle(true);

                FluxLoadingManager.setStartCalcTargetChunkNum(false);
                FluxLoadingManager.resetTargetChunkNumCalculated();
                FluxLoadingManager.resetTargetChunkNum();
                FluxLoadingManager.resetChunkLoadedNum();
                FluxLoadingManager.resetMovementLocked();
                FluxLoadingManager.resetFadeOutTimer();
                FluxLoadingManager.resetFadeInTimer();
                FluxLoadingManager.resetFinishFadingIn();
                FluxLoadingManager.setFinishChunkLoading(false);
                FluxLoadingManager.setCountingChunkLoaded(true);

                for (Runnable runnable: fluxloading$fluxLoadingStartListenersGetter.invoke())
                    runnable.run();

                StopWatch stopWatch = new StopWatch();
                stopWatch.start();
                fluxloading$stopWatchSetter.invoke(stopWatch);

                // fps: 60
                int frameCount = (int)(FluxLoadingManager.getFadeInDuration() * 60d);
                for (int i = 0; i < frameCount; i++)
                {
                    FluxLoadingManager.drawOverlayDefaultWorldLoadingAndFadingInPhase();
                    FluxLoadingManager.tick();
                    try
                    {
                        Thread.sleep(17);
                    }
                    catch (InterruptedException ignored) { }
                    Minecraft.getMinecraft().updateDisplay();
                }
            }

            return folderName;
        }
        else
            return original.call(instance);
    }
}
