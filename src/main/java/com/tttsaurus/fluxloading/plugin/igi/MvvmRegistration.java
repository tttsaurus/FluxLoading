package com.tttsaurus.fluxloading.plugin.igi;

import com.tttsaurus.fluxloading.FluxLoading;
import com.tttsaurus.fluxloading.FluxLoadingConfig;
import com.tttsaurus.ingameinfo.common.core.forgeevent.IgiGuiLifecycleInitEvent;
import com.tttsaurus.ingameinfo.common.core.forgeevent.MvvmRegisterEvent;
import com.tttsaurus.ingameinfo.common.core.mvvm.registry.MvvmRegistry;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public final class MvvmRegistration
{
    @SubscribeEvent
    public static void onMvvmRegister(MvvmRegisterEvent event)
    {
        if (FluxLoadingConfig.REGISTER_FLUXLOADING_MVVM)
            MvvmRegistry.autoRegister("fluxloading", FluxLoadingViewModel.class);
    }

    @SubscribeEvent
    public static void onIgiGuiLifecycleInit(IgiGuiLifecycleInitEvent event)
    {
        if (event.lifecycleOwner.equals(FluxLoadingIgiGuiManager.OWNER_NAME))
        {
            FluxLoading.logger.info("FluxLoadingIgiGuiManager starts working");
            FluxLoadingIgiGuiManager.openGui(FluxLoadingConfig.MVVM_TO_DISPLAY_WHILE_LOADING);
        }
    }
}
