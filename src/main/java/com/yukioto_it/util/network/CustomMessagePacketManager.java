package com.yukioto_it.util.network;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

import java.util.HashMap;
import java.util.Map;

public class CustomMessagePacketManager {
    protected static final Map<String, IMessagePacketHandlerServer> serverHandlers = new HashMap<>();
    protected static final Map<String, IMessagePacketHandlerClient> clientHandlers = new HashMap<>();

    public static void setServerHandler(String channel, IMessagePacketHandlerServer serverHandler) {
        serverHandlers.put(channel, serverHandler);
    }

    public static void setClientHandler(String channel, IMessagePacketHandlerClient clientHandler) {
        clientHandlers.put(channel, clientHandler);
    }


    public static void sendToServer(String channel, PacketData data) {
        MainPacketHandler.INSTANCE.sendToServer(new MessagePacketCustom(channel, data));
    }

    public static void sendToAll(String channel, PacketData data) {
        MainPacketHandler.INSTANCE.sendToAll(new MessagePacketCustom(channel, data));
    }

    public static void sendTo(String channel, PacketData data, EntityPlayerMP player) {
        MainPacketHandler.INSTANCE.sendTo(new MessagePacketCustom(channel, data), player);
    }

    public static void sendToAllAround(String channel, PacketData data, NetworkRegistry.TargetPoint point) {
        MainPacketHandler.INSTANCE.sendToAllAround(new MessagePacketCustom(channel, data), point);
    }

    public static void sendToAllTracking(String channel, PacketData data, NetworkRegistry.TargetPoint point) {
        MainPacketHandler.INSTANCE.sendToAllTracking(new MessagePacketCustom(channel, data), point);
    }

    public static void sendToAllTracking(String channel, PacketData data, Entity entity) {
        MainPacketHandler.INSTANCE.sendToAllTracking(new MessagePacketCustom(channel, data), entity);
    }

    public static void sendToDimension(String channel, PacketData data, int dimensionId) {
        MainPacketHandler.INSTANCE.sendToDimension(new MessagePacketCustom(channel, data), dimensionId);
    }



    public static class CustomMessageHandler implements IMessageHandler<MessagePacketCustom, IMessage> {

        @Override
        public IMessage onMessage(MessagePacketCustom message, MessageContext ctx) {
            if (ctx.side == Side.SERVER) {
                IMessagePacketHandlerServer handler = serverHandlers.get(message.channel);
                if (handler != null) {
                    return handler.onServerMessage(new PacketData(message.data), ctx.getServerHandler());
                }
            }
            else if (ctx.side == Side.CLIENT) {
                IMessagePacketHandlerClient handler = clientHandlers.get(message.channel);
                if (handler != null) {
                    return handler.onClientMessage(new PacketData(message.data), ctx.getClientHandler());
                }
            }
            return null;
        }
    }
}
