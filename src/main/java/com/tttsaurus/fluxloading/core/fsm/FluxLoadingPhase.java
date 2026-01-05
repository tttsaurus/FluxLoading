package com.tttsaurus.fluxloading.core.fsm;

public enum FluxLoadingPhase {
    FADING_IN,
    DEFAULT_WORLD_LOADING,
    EXTRA_CHUNK_LOADING,
    EXTRA_WAIT,
    FADING_OUT,
    FINISHED
}
