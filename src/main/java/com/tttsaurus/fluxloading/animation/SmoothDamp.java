package com.tttsaurus.fluxloading.animation;

import net.minecraft.util.MathHelper;

public class SmoothDamp {

    private float dis;
    private float from;
    private float to;
    private float vel;
    private final float smoothTime;
    private final float maxSpeed;

    public SmoothDamp(float from, float to, float smoothTime) {
        dis = Math.abs(to - from);
        this.from = from;
        this.to = to;
        vel = 0f;
        this.smoothTime = Math.max(smoothTime * 0.268f, 0.001f);
        maxSpeed = Float.POSITIVE_INFINITY;
    }

    public float evaluate(float deltaTime) {
        float omega = 2f / smoothTime;
        float x = omega * deltaTime;
        float exp = 1f / (1f + x + 0.48f * x * x + 0.235f * x * x * x);
        float change = to - from;
        float maxChange = maxSpeed * smoothTime;
        change = MathHelper.clamp_float(change, -maxChange, maxChange);
        float temp = (vel - omega * change) * deltaTime;
        vel = (vel - omega * temp) * exp;
        from = from + change + (temp - change) * exp;
        if (Math.abs(to - from) <= 0.005f * dis) from = to;
        return from;
    }
}
