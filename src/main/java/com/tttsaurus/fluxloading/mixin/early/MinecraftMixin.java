package com.tttsaurus.fluxloading.mixin.early;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.tttsaurus.fluxloading.FluxLoading;
import com.tttsaurus.fluxloading.core.FluxLoadingManager;
import com.tttsaurus.fluxloading.render.GlResourceManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.multiplayer.WorldClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public class MinecraftMixin
{
    @Inject(method = "shutdown", at = @At("HEAD"))
    public void beforeShutdown(CallbackInfo ci)
    {
        FluxLoading.logger.info("Start disposing OpenGL resources");
        GlResourceManager.disposeAll(FluxLoading.logger);
        FluxLoading.logger.info("OpenGL resources disposed");
    }

    @Inject(method = "loadWorld(Lnet/minecraft/client/multiplayer/WorldClient;Ljava/lang/String;)V", at = @At("HEAD"))
    public void beforeLoadWorld(WorldClient worldClientIn, String loadingMessage, CallbackInfo ci)
    {
        WorldClient world = Minecraft.getMinecraft().world;
        if (world != null)
        {
            // leave world
            FluxLoadingManager.setActive(false);

            // try save screenshot
            FluxLoadingManager.trySaveToLocal();
        }
    }

    @WrapOperation(
            method = "displayInGameMenu",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/Minecraft;displayGuiScreen(Lnet/minecraft/client/gui/GuiScreen;)V"
            ))
    public void displayGuiScreen(Minecraft instance, GuiScreen i, Operation<Void> original)
    {
        // when pause game
        FluxLoadingManager.prepareScreenshot();

        original.call(instance, i);
    }
}
