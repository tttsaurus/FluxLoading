package com.tttsaurus.fluxloading.util;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.tttsaurus.fluxloading.core.WorldLoadingScreenOverhaul;
import com.tttsaurus.fluxloading.render.RenderUtils;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.resources.I18n;

public class MixinHelpers {
    public static int mixinSetLoadingProgress$drawStringWithShadow(FontRenderer instance, String text, int x, int y, int color, Operation<Integer> original) {
        int res = original.call(instance, text, x, y, color);
        if (WorldLoadingScreenOverhaul.getDrawOverlay()) {
            if (WorldLoadingScreenOverhaul.getForceLoadingTitle() && text != null && !text.isEmpty())
                WorldLoadingScreenOverhaul.setForceLoadingTitle(false);
            if (WorldLoadingScreenOverhaul.getForceLoadingTitle()) {
                String i18nText = I18n.format("menu.loadingLevel");
                int width = RenderUtils.fontRenderer.getStringWidth(i18nText);
                RenderUtils.renderText(i18nText, x - (int) (width / 2f), y, 1, color, true);
            }
        }

        return res;
    }

    public static int mixinSetLoadingProgress$draw(Tessellator instance, Operation<Integer> original) {
        int ret = original.call(instance);
        if (WorldLoadingScreenOverhaul.getDrawOverlay() && WorldLoadingScreenOverhaul.isTextureAvailable()) {
            WorldLoadingScreenOverhaul.drawOverlay();
        }
        return ret;
    }
}
