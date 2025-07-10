package com.tttsaurus.fluxloading.core;

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
}
