package com.tttsaurus.fluxloading.plugin.igi;

import com.tttsaurus.fluxloading.core.FluxLoadingAPI;
import com.tttsaurus.ingameinfo.common.core.gui.IgiGuiContainer;
import com.tttsaurus.ingameinfo.common.core.input.IgiKeyboard;
import com.tttsaurus.ingameinfo.common.core.input.IgiMouse;
import com.tttsaurus.ingameinfo.common.core.input.InputFrameGenerator;
import com.tttsaurus.ingameinfo.common.core.mvvm.registry.MvvmRegistry;
import com.tttsaurus.ingameinfo.common.impl.gui.DefaultLifecycleProvider;

public final class FluxLoadingIgiGuiManager
{
    public static final String OWNER_NAME = "fluxloading_igi_lifecycle_holder";

    private static final InputFrameGenerator INPUT_GEN = new InputFrameGenerator(IgiKeyboard.INSTANCE, IgiMouse.INSTANCE);

    private static DefaultLifecycleProvider lifecycleProvider;
    private static void init()
    {
        lifecycleProvider = new DefaultLifecycleProvider(OWNER_NAME);
        lifecycleProvider.setEnableFbo(false);
        lifecycleProvider.setEnableShader(false);
        lifecycleProvider.setEnableMultisampleOnFbo(false);
        lifecycleProvider.setMaxFps_FixedUpdate(30);
        lifecycleProvider.setMaxFps_RenderUpdate(60);
    }

    public static void openGui(String mvvmRegistryName)
    {
        if (lifecycleProvider == null) return;
        IgiGuiContainer igiGuiContainer = MvvmRegistry.getIgiGuiContainer(mvvmRegistryName);
        if (igiGuiContainer == null) return;

        lifecycleProvider.openIgiGui(mvvmRegistryName, igiGuiContainer);
    }

    private static void update()
    {
        if (lifecycleProvider == null) return;
        if (FluxLoadingAPI.isDuringFadingOutPhase() || FluxLoadingAPI.isFinishLoading()) return;

        lifecycleProvider.update(INPUT_GEN.generate());
    }
}
