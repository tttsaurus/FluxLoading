package com.tttsaurus.fluxloading.core.chunk.gate;

public enum FluxLoadingChunkSource
{
    VANILLA(false),
    NOTHIRIUM(false),
    CELERITAS(true);

    public final boolean instantComplete;

    FluxLoadingChunkSource(boolean instantComplete)
    {
        this.instantComplete = instantComplete;
    }
}
