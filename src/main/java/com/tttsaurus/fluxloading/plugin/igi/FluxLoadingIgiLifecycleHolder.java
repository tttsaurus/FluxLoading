package com.tttsaurus.fluxloading.plugin.igi;

import com.tttsaurus.fluxloading.core.FluxLoadingAPI;
import com.tttsaurus.fluxloading.core.fsm.FluxLoadingPhase;
import com.tttsaurus.ingameinfo.common.core.gui.GuiLifecycleHolder;
import com.tttsaurus.ingameinfo.common.core.input.IgiKeyboard;
import com.tttsaurus.ingameinfo.common.core.input.IgiMouse;
import com.tttsaurus.ingameinfo.common.core.input.InputFrameGenerator;

public final class FluxLoadingIgiLifecycleHolder extends GuiLifecycleHolder
{
    public static final String HOLDER_NAME = "fluxloading_igi_lifecycle_holder";

    public FluxLoadingIgiLifecycleHolder()
    {
        super(HOLDER_NAME, new InputFrameGenerator(new IgiKeyboard(), new IgiMouse()));
    }

    @Override
    public void update()
    {
        if (getLifecycleProvider() == null) return;
        if (!FluxLoadingAPI.isActive() || FluxLoadingAPI.getPhase() == FluxLoadingPhase.FADING_OUT) return;

        getLifecycleProvider().update(inputGen.generate());
    }
}
