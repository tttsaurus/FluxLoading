package com.tttsaurus.fluxloading.core;

import org.apache.commons.lang3.time.StopWatch;
import java.util.ArrayList;
import java.util.List;

public final class FluxLoadingAPI
{
    protected static boolean duringFadingInPhase = false;
    protected static boolean duringDefaultWorldLoadingPhase = false;
    protected static boolean duringExtraChunkLoadingPhase = false;
    protected static boolean duringExtraWaitPhase = false;
    protected static boolean duringFadingOutPhase = false;
    protected static boolean finishLoading = false;

    public static boolean isDuringFadingInPhase() { return duringFadingInPhase; }
    public static boolean isDuringDefaultWorldLoadingPhase() { return duringDefaultWorldLoadingPhase; }
    public static boolean isDuringExtraChunkLoadingPhase() { return duringExtraChunkLoadingPhase; }
    public static boolean isDuringExtraWaitPhase() { return duringExtraWaitPhase; }
    public static boolean isDuringFadingOutPhase() { return duringFadingOutPhase; }
    public static boolean isFinishLoading() { return finishLoading; }

    protected static int tickNum = 0;
    protected static final List<Runnable> fluxLoadingTickListeners = new ArrayList<>();
    public static void addFluxLoadingTickListener(Runnable listener)
    {
        fluxLoadingTickListeners.add(listener);
    }

    protected static final List<Runnable> fluxLoadingStartListeners = new ArrayList<>();
    public static void addFluxLoadingStartListener(Runnable listener)
    {
        fluxLoadingStartListeners.add(listener);
    }

    protected static final List<Runnable> fluxLoadingEndListeners = new ArrayList<>();
    public static void addFluxLoadingEndListener(Runnable listener)
    {
        fluxLoadingEndListeners.add(listener);
    }

    protected static StopWatch stopWatch;
}
