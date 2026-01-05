package com.tttsaurus.fluxloading.core;

import com.tttsaurus.fluxloading.core.fsm.FluxLoadingPhase;
import org.apache.commons.lang3.time.StopWatch;
import java.util.ArrayList;
import java.util.List;

public final class FluxLoadingAPI
{
    public static boolean isActive() { return FluxLoadingManager.isActive(); }
    public static FluxLoadingPhase getPhase() { return FluxLoadingManager.FSM.getPhase(); }

    static int tickNum = 0;
    static final List<Runnable> fluxLoadingTickListeners = new ArrayList<>();
    public static void addFluxLoadingTickListener(Runnable listener)
    {
        fluxLoadingTickListeners.add(listener);
    }

    static final List<Runnable> fluxLoadingStartListeners = new ArrayList<>();
    public static void addFluxLoadingStartListener(Runnable listener)
    {
        fluxLoadingStartListeners.add(listener);
    }

    static final List<Runnable> fluxLoadingEndListeners = new ArrayList<>();
    public static void addFluxLoadingEndListener(Runnable listener)
    {
        fluxLoadingEndListeners.add(listener);
    }

    static StopWatch stopWatch;
}
