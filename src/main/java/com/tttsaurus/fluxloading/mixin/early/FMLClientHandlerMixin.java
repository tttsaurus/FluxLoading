package com.tttsaurus.fluxloading.mixin.early;

import com.tttsaurus.fluxloading.FluxLoading;
import com.tttsaurus.fluxloading.FluxLoadingConfig;
import com.tttsaurus.fluxloading.core.FluxLoadingAPI;
import com.tttsaurus.fluxloading.core.FluxLoadingManager;
import com.tttsaurus.fluxloading.core.ShaderResources;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiWorldSelection;
import net.minecraft.world.storage.WorldSummary;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.common.FMLCommonHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FMLClientHandler.class)
public class FMLClientHandlerMixin
{
    @Inject(method = "tryLoadExistingWorld", at = @At("HEAD"), remap = false)
    public void tryLoadExistingWorld(GuiWorldSelection selectWorldGUI, WorldSummary comparator, CallbackInfo ci)
    {
        // join world
        if (!FMLCommonHandler.instance().getSide().isClient()) return;

        String folderName = comparator.getFileName();

        FluxLoading.LOGGER.info("Join world entry point: Forge");
        FluxLoading.LOGGER.info("Prepare to join world: " + folderName);

        ShaderResources.initShader();
        ShaderResources.resetShader();
        ShaderResources.setShaderFadingState(true);

        FluxLoadingManager.beginFluxLoading(
                folderName,
                FluxLoadingConfig.INSTANTLY_POPPED_UP_LOADING_TITLE,
                FluxLoadingConfig.DISABLE_ALL_VANILLA_LOADING_TEXTS,
                FluxLoadingConfig.WAIT_CHUNKS_TO_LOAD,
                FluxLoadingConfig.FADE_IN_DURATION,
                FluxLoadingConfig.EXTRA_WAIT_TIME,
                FluxLoadingConfig.FADE_OUT_DURATION);

        if (FluxLoadingAPI.isActive())
        {
            int frameCount = (int)(FluxLoadingConfig.FADE_IN_DURATION * 60d);

            for (int i = 0; i < frameCount; i++)
            {
                FluxLoadingManager.renderAndTick();

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
