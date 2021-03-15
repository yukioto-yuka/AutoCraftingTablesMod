package com.yukioto_it.util.network;

import net.minecraft.network.NetHandlerPlayServer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public interface IMessagePacketHandlerServer {
    IMessage onServerMessage(PacketData data, NetHandlerPlayServer netHandler);
}
