package com.tttsaurus.fluxloading.core.listener;

import com.tttsaurus.fluxloading.core.FluxLoadingManager;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public final class FluxLoadingServerTickListener
{
    @SuppressWarnings("all")
    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event)
    {
        if (event.phase != TickEvent.Phase.END) return;
        if (FluxLoadingManager.SERVER_LOCK.isLockEmpty()) return;

        MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();

        List<UUID> outdated = new ArrayList<>();
        for (Map.Entry<UUID, Vec3d> entry: FluxLoadingManager.SERVER_LOCK.entrySet())
        {
            UUID uuid = entry.getKey();
            EntityPlayerMP player = server.getPlayerList().getPlayerByUUID(uuid);
            if (player == null)
            {
                outdated.add(uuid);
                continue;
            }

            Vec3d pos = entry.getValue();
            player.connection.setPlayerLocation(pos.x, pos.y, pos.z, player.rotationYaw, player.rotationPitch);
        }

        for (UUID uuid: outdated)
            FluxLoadingManager.SERVER_LOCK.unlockPlayer(uuid);
    }
}
