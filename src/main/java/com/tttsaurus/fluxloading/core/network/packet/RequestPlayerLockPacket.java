package com.tttsaurus.fluxloading.core.network.packet;

import com.tttsaurus.fluxloading.core.FluxLoadingManager;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class RequestPlayerLockPacket implements IMessage
{
    private boolean lock;

    public RequestPlayerLockPacket() { }

    public RequestPlayerLockPacket(boolean lock)
    {
        this.lock = lock;
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
        this.lock = buf.readBoolean();
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        buf.writeBoolean(lock);
    }

    public static class Handler implements IMessageHandler<RequestPlayerLockPacket, IMessage>
    {
        @Override
        public IMessage onMessage(RequestPlayerLockPacket message, MessageContext ctx)
        {
            if (!ctx.side.isServer()) return null;

            MinecraftServer server = ctx.getServerHandler().player.getServerWorld().getMinecraftServer();
            if (server == null) return null;
            if (!server.isServerRunning()) return null;

            EntityPlayerMP player = ctx.getServerHandler().player;
            if (message.lock)
                FluxLoadingManager.SERVER_LOCK.lockPlayer(player.getUniqueID(), new Vec3d(player.posX, player.posY, player.posZ));
            else
                FluxLoadingManager.SERVER_LOCK.unlockPlayer(player.getUniqueID());

            return null;
        }
    }
}
