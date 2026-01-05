package com.tttsaurus.fluxloading.core.player_lock;

import net.minecraft.util.math.Vec3d;

import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class FluxLoadingServerMovementLock
{
    private final Map<UUID, Vec3d> lockedPlayers = new ConcurrentHashMap<>();

    public void lockPlayer(UUID playerId, Vec3d pos)
    {
        lockedPlayers.put(playerId, pos);
    }

    public void unlockPlayer(UUID playerId)
    {
        lockedPlayers.remove(playerId);
    }

    public boolean isLockEmpty()
    {
        return lockedPlayers.isEmpty();
    }

    public Set<Map.Entry<UUID, Vec3d>> entrySet()
    {
        return lockedPlayers.entrySet();
    }
}
