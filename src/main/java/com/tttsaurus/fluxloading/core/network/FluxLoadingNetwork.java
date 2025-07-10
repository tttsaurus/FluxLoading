package com.tttsaurus.fluxloading.core.network;

import com.tttsaurus.fluxloading.Tags;
import com.tttsaurus.fluxloading.core.network.packet.RequestPlayerLockPacket;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

public class FluxLoadingNetwork
{
    public static final SimpleNetworkWrapper NETWORK = NetworkRegistry.INSTANCE.newSimpleChannel(Tags.MODID);

    public static void requestPlayerLock(boolean lock)
    {
        NETWORK.sendToServer(new RequestPlayerLockPacket(lock));
    }

    public static void init()
    {
        NETWORK.registerMessage(RequestPlayerLockPacket.Handler.class, RequestPlayerLockPacket.class, 0, Side.SERVER);
    }
}
