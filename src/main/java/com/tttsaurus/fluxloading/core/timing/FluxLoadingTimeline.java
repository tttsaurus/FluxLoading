package com.tttsaurus.fluxloading.core.timing;

import com.tttsaurus.fluxloading.core.util.SmoothDamp;
import org.apache.commons.lang3.time.StopWatch;

public final class FluxLoadingTimeline
{
    public static final class UpdateResult
    {
        public boolean setPercentage;
        public float percentage;

        public boolean fadeInFinished;

        public boolean extraWaitFinished;
        public boolean fadeOutFinished;
    }

    private double fadeInDuration = 1.0d;

    private double extraWaitTime = 0.5d;
    private double fadeOutDuration = 1.0d;

    private StopWatch fadeInStopWatch;
    private SmoothDamp fadeInSmoothDamp;
    private double prevFadeInTime;

    private StopWatch fadeOutStopWatch;
    private SmoothDamp fadeOutSmoothDamp;
    private double prevFadeOutTime;

    private boolean fadeOutSequenceStarted = false;

    public void reset()
    {
        fadeInStopWatch = null;
        fadeInSmoothDamp = null;
        prevFadeInTime = 0d;

        fadeOutStopWatch = null;
        fadeOutSmoothDamp = null;
        prevFadeOutTime = 0d;

        fadeOutSequenceStarted = false;
    }

    public void startFadeIn(double durationSeconds)
    {
        fadeInDuration = durationSeconds;

        fadeInStopWatch = new StopWatch();
        fadeInStopWatch.start();

        // 1 -> 0
        fadeInSmoothDamp = new SmoothDamp(1, 0, (float)fadeInDuration);
        prevFadeInTime = 0d;
    }

    public void configureFadeOut(double extraWaitSeconds, double fadeOutDurationSeconds)
    {
        extraWaitTime = extraWaitSeconds;
        fadeOutDuration = fadeOutDurationSeconds;
    }

    public void startFadeOutSequence()
    {
        if (fadeOutSequenceStarted) return;

        fadeOutSequenceStarted = true;

        fadeOutStopWatch = new StopWatch();
        fadeOutStopWatch.start();

        // 0 -> 1
        fadeOutSmoothDamp = new SmoothDamp(0, 1, (float)fadeOutDuration);
        prevFadeOutTime = 0d;
    }

    public UpdateResult update()
    {
        UpdateResult out = new UpdateResult();

        // fade-in: sets percentage until finishes
        if (fadeInStopWatch != null)
        {
            double fadeInTime = fadeInStopWatch.getNanoTime() / 1E9d;

            if (fadeInTime >= fadeInDuration)
            {
                out.setPercentage = true;
                out.percentage = 0f;
                out.fadeInFinished = true;

                fadeInStopWatch.stop();
                fadeInStopWatch = null;
            }
            else
            {
                double delta = fadeInTime - prevFadeInTime;
                out.percentage = fadeInSmoothDamp.evaluate((float)delta);
                prevFadeInTime = fadeInTime;
                out.setPercentage = true;
            }
        }

        // fade-out sequence: extra wait then animate percentage
        if (fadeOutStopWatch != null)
        {
            double t = fadeOutStopWatch.getNanoTime() / 1E9d;

            if (t >= extraWaitTime)
            {
                out.extraWaitFinished = true;

                double nowFadeOutTime = t - extraWaitTime;

                if (nowFadeOutTime >= fadeOutDuration)
                {
                    out.setPercentage = true;
                    out.percentage = 1f;
                    out.fadeOutFinished = true;

                    fadeOutStopWatch.stop();
                    fadeOutStopWatch = null;
                }
                else
                {
                    double delta = nowFadeOutTime - prevFadeOutTime;
                    out.percentage = fadeOutSmoothDamp.evaluate((float)delta);
                    prevFadeOutTime = nowFadeOutTime;
                    out.setPercentage = true;
                }
            }
        }

        return out;
    }
}
