package com.tttsaurus.fluxloading.mixin.early;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.tttsaurus.fluxloading.core.FluxLoadingManager;
import net.minecraft.util.MouseHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(MouseHelper.class)
public class MouseHelperMixin
{
    @Shadow
    public int deltaX;

    @Shadow
    public int deltaY;

    @WrapMethod(method = "mouseXYChange")
    public void mouseXYChange(Operation<Void> original)
    {
        if (FluxLoadingManager.isMovementLocked())
        {
            deltaX = 0;
            deltaY = 0;
        }
        else
            original.call();
    }
}
