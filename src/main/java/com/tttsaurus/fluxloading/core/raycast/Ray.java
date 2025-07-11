package com.tttsaurus.fluxloading.core.raycast;

import net.minecraft.util.math.Vec3d;

public final class Ray
{
    public final Vec3d pos;
    public final Vec3d dir;

    public Ray(Vec3d pos, Vec3d dir)
    {
        this.pos = pos;
        this.dir = dir;
    }

    @Override
    public String toString()
    {
        return "Ray{" +
                "pos=" + pos +
                ", dir=" + dir +
                '}';
    }
}
