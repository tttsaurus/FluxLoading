package com.tttsaurus.fluxloading.core.player_freeze;

import com.tttsaurus.fluxloading.core.network.FluxLoadingNetwork;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;

public final class FluxLoadingClientMovementLock
{
    private boolean locked = false;

    private boolean lockPosFetched = false;
    private double lockX;
    private double lockY;
    private double lockZ;

    public boolean isLocked()
    {
        return locked;
    }

    public void reset()
    {
        locked = false;
        lockPosFetched = false;

        lockX = 0;
        lockY = 0;
        lockZ = 0;
    }

    public void ensureLocked()
    {
        if (locked) return;

        locked = true;
        lockPosFetched = false;
    }

    public void applyClientTickLock()
    {
        if (!locked) return;

        EntityPlayerSP player = Minecraft.getMinecraft().player;
        if (player == null) return;

        if (!lockPosFetched)
        {
            lockPosFetched = true;
            lockX = player.posX;
            lockY = player.posY;
            lockZ = player.posZ;

            FluxLoadingNetwork.requestPlayerLock(true);
        }

        player.movementInput.moveForward = 0;
        player.movementInput.moveStrafe = 0;

        player.movementInput.forwardKeyDown = false;
        player.movementInput.backKeyDown = false;
        player.movementInput.leftKeyDown = false;
        player.movementInput.rightKeyDown = false;

        player.movementInput.jump = false;
        player.movementInput.sneak = false;

        player.motionX = 0;
        player.motionY = 0;
        player.motionZ = 0;

        player.setPosition(lockX, lockY, lockZ);
    }
}
