package com.tttsaurus.fluxloading.mixin.early;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.tttsaurus.fluxloading.core.WorldLoadingScreenOverhaul;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.profiler.Profiler;
import net.minecraft.util.ScreenShotHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(EntityRenderer.class)
public class EntityRendererMixin
{
    @WrapOperation(
            method = "updateCameraAndRender",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/profiler/Profiler;endStartSection(Ljava/lang/String;)V"
            ))
    public void mixin_updateCameraAndRender_Profiler$endStartSection(Profiler instance, String name, Operation<Void> original)
    {
        // just before render gui
        if (WorldLoadingScreenOverhaul.getScreenShotToggle())
        {
            Minecraft minecraft = Minecraft.getMinecraft();
            WorldLoadingScreenOverhaul.setScreenShot(ScreenShotHelper.createScreenshot(minecraft.displayWidth, minecraft.displayHeight, minecraft.getFramebuffer()));
            WorldLoadingScreenOverhaul.finishScreenShot();
        }

        original.call(instance, name);
    }
}
