package com.tttsaurus.fluxloading.core;

import java.util.ArrayList;
import java.util.List;

public final class FluxLoadingAPI
{
    protected static boolean duringDefaultWorldLoadingPhase = false;
    protected static boolean duringExtraChunkLoadingPhase = false;
    protected static boolean duringExtraWaitPhase = false;
    protected static boolean duringFadingOutPhase = false;
    protected static boolean finishLoading = false;

    public static boolean isDuringDefaultWorldLoadingPhase() { return duringDefaultWorldLoadingPhase; }
    public static boolean isDuringExtraChunkLoadingPhase() { return duringExtraChunkLoadingPhase; }
    public static boolean isDuringExtraWaitPhase() { return duringExtraWaitPhase; }
    public static boolean isDuringFadingOutPhase() { return duringFadingOutPhase; }
    public static boolean isFinishLoading() { return finishLoading; }

    protected static int tickNum = 0;
    private static final List<Runnable> fluxLoadingTickListeners = new ArrayList<>();

    protected static void executeFluxLoadingTickListeners()
    {
        for (Runnable runnable: fluxLoadingTickListeners)
            runnable.run();
    }

    public static void addFluxLoadingTickListener(Runnable listener)
    {
        fluxLoadingTickListeners.add(listener);
    }
}
