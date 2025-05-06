package com.tttsaurus.fluxloading.mixin.early;

import net.minecraft.client.gui.GuiSelectWorld;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(GuiSelectWorld.class)
public interface GuiSelectWorldAccessor {

    @Accessor
    java.util.List getField_146639_s();
}
