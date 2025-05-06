package com.tttsaurus.fluxloading.mixin.early;

import net.minecraft.client.renderer.WorldRenderer;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.tttsaurus.fluxloading.core.WorldLoadingScreenOverhaul;

@SuppressWarnings("unused")
@Mixin(WorldRenderer.class)
public class MixinWorldRenderer {
    // This will get loaded and then later overwritten by Sodium if it is present, so we don't need to worry about
    // conditionally loading it.

    @Inject(method = "updateRenderer", at = @At("RETURN"))
    public void updateRenderer(CallbackInfo ci) {
        WorldLoadingScreenOverhaul.onChunkRendered();
    }
}
