package com.tttsaurus.fluxloading.mixin.early;

import com.tttsaurus.fluxloading.FluxLoading;
import com.tttsaurus.fluxloading.FluxLoadingConfig;
import com.tttsaurus.fluxloading.core.FluxLoadingAPI;
import com.tttsaurus.fluxloading.core.FluxLoadingManager;
import com.tttsaurus.fluxloading.core.ShaderResources;
import com.tttsaurus.fluxloading.core.function.Action_1Param;
import com.tttsaurus.fluxloading.core.function.Func;
import com.tttsaurus.fluxloading.core.util.AccessorUnreflector;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiWorldSelection;
import net.minecraft.world.storage.WorldSummary;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.common.FMLCommonHandler;
import org.apache.commons.lang3.time.StopWatch;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import java.util.List;

@SuppressWarnings("all")
@Mixin(FMLClientHandler.class)
public class FMLClientHandlerMixin
{
    @Unique
    private static Action_1Param<Boolean> fluxloading$duringFadingInPhaseSetter;
    @Unique
    private static Action_1Param<Boolean> fluxloading$duringDefaultWorldLoadingPhaseSetter;
    @Unique
    private static Action_1Param<Boolean> fluxloading$duringExtraChunkLoadingPhaseSetter;
    @Unique
    private static Action_1Param<Boolean> fluxloading$duringExtraWaitPhaseSetter;
    @Unique
    private static Action_1Param<Boolean> fluxloading$duringFadingOutPhaseSetter;
    @Unique
    private static Action_1Param<Boolean> fluxloading$finishLoadingSetter;
    @Unique
    private static Action_1Param<Integer> fluxloading$tickNum;
    @Unique
    private static Func<List<Runnable>> fluxloading$fluxLoadingStartListenersGetter;
    @Unique
    private static Action_1Param<StopWatch> fluxloading$stopWatchSetter;

    @Inject(method = "tryLoadExistingWorld", at = @At("HEAD"), remap = false)
    public void tryLoadExistingWorld(GuiWorldSelection selectWorldGUI, WorldSummary comparator, CallbackInfo ci)
    {
        // join world
        if (!FMLCommonHandler.instance().getSide().isClient()) return;

        String folderName = comparator.getFileName();

        FluxLoading.LOGGER.info("Join world entry point: Forge");
        FluxLoading.LOGGER.info("Prepare to join world: " + folderName);

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
            FluxLoading.LOGGER.info("Screenshot found. Start flux loading process.");
        else
            FluxLoading.LOGGER.info("No screenshot found. Abort flux loading process.");

        if (FluxLoadingManager.isTextureAvailable())
        {
            ShaderResources.initShader();
            ShaderResources.resetShader();
            ShaderResources.setShaderFadingState(true);

            FluxLoadingManager.setActive(true);

            if (FluxLoadingConfig.INSTANTLY_POPPED_UP_LOADING_TITLE)
                FluxLoadingManager.setForceLoadingTitle(true);

            FluxLoadingManager.prepareStates();

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
    }
}
