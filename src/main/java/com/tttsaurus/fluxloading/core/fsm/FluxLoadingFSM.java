package com.tttsaurus.fluxloading.core.fsm;

import javax.annotation.Nonnull;
import java.util.Objects;

public final class FluxLoadingFSM
{
    private FluxLoadingPhase phase = FluxLoadingPhase.FADING_IN;
    public FluxLoadingPhase getPhase() { return phase; }

    public void start()
    {
        phase = FluxLoadingPhase.FADING_IN;
    }

    public boolean isDuring(FluxLoadingPhase phase)
    {
        return this.phase == phase;
    }

    private IllegalStateException invalidEvent(String event)
    {
        return new IllegalStateException("Invalid FSM event \"" + event + "\" in phase " + phase + ".");
    }

    private IllegalStateException invalidEvent(String event, String reason)
    {
        return new IllegalStateException("Invalid FSM event \"" + event + "\" in phase " + phase + " (" + reason + ").");
    }

    private void transitionTo(@Nonnull FluxLoadingPhase next)
    {
        if (phase == next) return;

        phase = Objects.requireNonNull(next);
    }

    public void markFadingInFinished()
    {
        switch (phase)
        {
            case FADING_IN ->
                    transitionTo(FluxLoadingPhase.DEFAULT_WORLD_LOADING);

            case DEFAULT_WORLD_LOADING,
                 EXTRA_CHUNK_LOADING,
                 EXTRA_WAIT,
                 FADING_OUT,
                 FINISHED ->
                    throw invalidEvent("markFadingInFinished");
        }
    }

    public void markDefaultWorldLoadingFinished()
    {
        switch (phase)
        {
            // no automatic transition:
            // manager must decide extra chunk loading
            case DEFAULT_WORLD_LOADING -> { }

            case FADING_IN ->
                    throw invalidEvent("markDefaultWorldLoadingFinished", "Fading-in has not finished yet");

            case EXTRA_CHUNK_LOADING,
                 EXTRA_WAIT,
                 FADING_OUT,
                 FINISHED ->
                    throw invalidEvent("markDefaultWorldLoadingFinished");
        }
    }

    public void decideExtraChunkLoading(boolean required)
    {
        switch (phase)
        {
            case DEFAULT_WORLD_LOADING ->
                    transitionTo(required ? FluxLoadingPhase.EXTRA_CHUNK_LOADING : FluxLoadingPhase.EXTRA_WAIT);

            case FADING_IN ->
                    throw invalidEvent("decideExtraChunkLoading", "World loading has not started yet");

            case EXTRA_CHUNK_LOADING ->
                    throw invalidEvent("decideExtraChunkLoading", "Extra chunk loading already in progress");

            case EXTRA_WAIT,
                 FADING_OUT,
                 FINISHED ->
                    throw invalidEvent("decideExtraChunkLoading");
        }
    }

    public void markExtraChunkLoadingFinished()
    {
        switch (phase)
        {
            case EXTRA_CHUNK_LOADING ->
                    transitionTo(FluxLoadingPhase.EXTRA_WAIT);

            case DEFAULT_WORLD_LOADING ->
                    throw invalidEvent("markExtraChunkLoadingFinished", "Extra chunk loading was never started");

            case FADING_IN,
                 EXTRA_WAIT,
                 FADING_OUT,
                 FINISHED ->
                    throw invalidEvent("markExtraChunkLoadingFinished");
        }
    }

    public void markExtraWaitFinished()
    {
        switch (phase)
        {
            case EXTRA_WAIT ->
                    transitionTo(FluxLoadingPhase.FADING_OUT);

            case EXTRA_CHUNK_LOADING ->
                    throw invalidEvent("markExtraWaitFinished", "Extra chunk loading still in progress");

            case FADING_IN,
                 DEFAULT_WORLD_LOADING,
                 FADING_OUT,
                 FINISHED ->
                    throw invalidEvent("markExtraWaitFinished");
        }
    }

    public void markFadingOutFinished()
    {
        switch (phase)
        {
            case FADING_OUT ->
                    transitionTo(FluxLoadingPhase.FINISHED);

            case EXTRA_WAIT ->
                    throw invalidEvent("markFadingOutFinished", "Extra wait phase has not completed");

            case FADING_IN,
                 DEFAULT_WORLD_LOADING,
                 EXTRA_CHUNK_LOADING,
                 FINISHED ->
                    throw invalidEvent("markFadingOutFinished");
        }
    }
}
