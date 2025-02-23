package com.tttsaurus.fluxloading.mixin.early;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.tttsaurus.fluxloading.FluxLoading;
import com.tttsaurus.fluxloading.core.WorldLoadingScreenOverhaul;
import com.tttsaurus.fluxloading.render.GlResourceManager;
import com.tttsaurus.fluxloading.render.RenderUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.server.integrated.IntegratedServer;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import java.io.File;
import java.io.RandomAccessFile;

@Mixin(Minecraft.class)
public class MinecraftMixin
{
    @Inject(method = "shutdown", at = @At("HEAD"))
    public void shutdown(CallbackInfo ci)
    {
        FluxLoading.logger.info("Starts disposing OpenGL resources");
        GlResourceManager.disposeAll(FluxLoading.logger);
        FluxLoading.logger.info("OpenGL resources disposed");
    }

    @Inject(method = "loadWorld(Lnet/minecraft/client/multiplayer/WorldClient;Ljava/lang/String;)V", at = @At("HEAD"))
    public void loadWorld(WorldClient worldClientIn, String loadingMessage, CallbackInfo ci)
    {
        WorldClient world = Minecraft.getMinecraft().world;
        if (world != null)
        {
            // leave world
            WorldLoadingScreenOverhaul.setDrawOverlay(false);

            // try save screenshot
            IntegratedServer server = Minecraft.getMinecraft().getIntegratedServer();
            if (server != null && !WorldLoadingScreenOverhaul.isTextureNull() && !WorldLoadingScreenOverhaul.isTextureBufferNull())
            {
                File worldSaveDir = new File("saves/" + server.getFolderName());
                RenderUtils.createPng(
                        worldSaveDir,
                        "last_screenshot",
                        WorldLoadingScreenOverhaul.getTextureBuffer(),
                        WorldLoadingScreenOverhaul.getTextureWidth(),
                        WorldLoadingScreenOverhaul.getTextureHeight());

                try
                {
                    RandomAccessFile fogColorFile = new RandomAccessFile("saves/" + server.getFolderName() + "/last_fog_color", "rw");
                    fogColorFile.setLength(0);
                    fogColorFile.seek(0);
                    fogColorFile.writeInt(WorldLoadingScreenOverhaul.getFogColor());
                    fogColorFile.close();
                }
                catch (Exception ignored) { }
            }
        }
    }

    @WrapOperation(
            method = "displayInGameMenu",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/Minecraft;displayGuiScreen(Lnet/minecraft/client/gui/GuiScreen;)V"
            ))
    public void mixin_displayInGameMenu_Minecraft$displayGuiScreen(Minecraft instance, GuiScreen i, Operation<Void> original)
    {
        // when pause game
        WorldLoadingScreenOverhaul.prepareScreenShot();

        Minecraft minecraft = Minecraft.getMinecraft();
        Vec3d vec3d = minecraft.world.getFogColor(minecraft.getRenderPartialTicks());
        int red = (int)(vec3d.x * 255);
        int green = (int)(vec3d.y * 255);
        int blue = (int)(vec3d.z * 255);
        int rgba = (255 << 24) | (red << 16) | (green << 8) | blue;

        WorldLoadingScreenOverhaul.setFogColor(rgba);

        original.call(instance, i);
    }
}
