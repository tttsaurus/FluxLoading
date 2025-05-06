package com.tttsaurus.fluxloading.mixin.early;

import net.minecraft.client.Minecraft;
import net.minecraft.util.MouseHelper;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.tttsaurus.fluxloading.core.WorldLoadingScreenOverhaul;

@SuppressWarnings("unused")
@Mixin(MouseHelper.class)
public class MixinMouseHelper {

    @Inject(method = "mouseXYChange", at = @At("HEAD"), cancellable = true)
    public void onMouseXYChange(CallbackInfo ci) {
        if (WorldLoadingScreenOverhaul.freezePlayer) {
            Minecraft.getMinecraft().mouseHelper.deltaX = 0;
            Minecraft.getMinecraft().mouseHelper.deltaY = 0;
            ci.cancel();
        }
    }
}
