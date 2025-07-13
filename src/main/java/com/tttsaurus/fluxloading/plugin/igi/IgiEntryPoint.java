package com.tttsaurus.fluxloading.plugin.igi;

import com.tttsaurus.fluxloading.FluxLoadingConfig;
import com.tttsaurus.fluxloading.core.FluxLoadingAPI;
import com.tttsaurus.ingameinfo.common.core.forgeevent.IgiRuntimeEntryPointEvent;
import com.tttsaurus.ingameinfo.common.core.gui.GuiLifecycleHolder;
import com.tttsaurus.ingameinfo.common.impl.gui.DefaultLifecycleProvider;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public final class IgiEntryPoint
{
    @SubscribeEvent
    public static void onIgiRuntimeEntryPoint(IgiRuntimeEntryPointEvent event)
    {
        if (FluxLoadingConfig.REGISTER_FLUXLOADING_MVVM)
            event.runtime.initPhase.registerMvvm("fluxloading", FluxLoadingViewModel.class);

        GuiLifecycleHolder holder = event.runtime.global.registerLifecycleHolder(FluxLoadingIgiLifecycleHolder.class);
        DefaultLifecycleProvider lifecycleProvider = new DefaultLifecycleProvider();
        lifecycleProvider.setEnableFbo(false);
        lifecycleProvider.setEnableShader(false);
        lifecycleProvider.setEnableMultisampleOnFbo(false);
        lifecycleProvider.setMaxFps_FixedUpdate(30);
        lifecycleProvider.setMaxFps_RenderUpdate(60);
        holder.setLifecycleProvider(lifecycleProvider);

        FluxLoadingAPI.addFluxLoadingTickListener(holder::update);

        for (String mvvm: FluxLoadingConfig.MVVM_TO_DISPLAY_WHILE_LOADING)
            event.runtime.global.openGuiOnStartup(FluxLoadingIgiLifecycleHolder.HOLDER_NAME, mvvm);
    }
}
