package com.tttsaurus.fluxloading.mixin.early;

import net.minecraft.client.renderer.WorldRenderer;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.tttsaurus.fluxloading.core.WorldLoadingScreenOverhaul;

@Mixin(WorldRenderer.class)
public class MixinWorldRenderer {

    @Inject(method = "updateRenderer", at = @At("RETURN"))
    public void updateRenderer(CallbackInfo ci) {
        // Called after a chunk section is updated
        if (WorldLoadingScreenOverhaul.getCountingChunkLoaded()) {
            WorldLoadingScreenOverhaul.incrChunkLoadedNum();

            if (WorldLoadingScreenOverhaul.getChunkLoadedNum() >= WorldLoadingScreenOverhaul.getTargetChunkNum()) {
                WorldLoadingScreenOverhaul.setCountingChunkLoaded(false);
                WorldLoadingScreenOverhaul.resetChunkLoadedNum();
                WorldLoadingScreenOverhaul.resetTargetChunkNum();
                WorldLoadingScreenOverhaul.setFinishedLoadingChunks(true);
                WorldLoadingScreenOverhaul.startFadeOutTimer();
            }
        }
    }
}
