package com.tttsaurus.fluxloading.mixin.late;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.tttsaurus.fluxloading.core.WorldLoadingScreenOverhaul;

import me.jellysquid.mods.sodium.client.render.SodiumWorldRenderer;

@Mixin(value = SodiumWorldRenderer.class, remap = false)
public class MixinSodiumWorldRenderer {

    @Inject(
        method = "drawChunkLayer",
        at = @At(
            value = "INVOKE",
            target = "Lme/jellysquid/mods/sodium/client/render/chunk/ChunkRenderManager;renderLayer(Lcom/gtnewhorizons/angelica/compat/toremove/MatrixStack;Lme/jellysquid/mods/sodium/client/render/chunk/passes/BlockRenderPass;DDD)V"))
    public void onDrawChunkLayer(CallbackInfo ci) {
        WorldLoadingScreenOverhaul.onChunkRendered();
    }
}
