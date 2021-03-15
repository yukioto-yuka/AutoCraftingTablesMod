package com.yukioto_it.util.network;

import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public interface IMessagePacketHandlerClient {
    IMessage onClientMessage(PacketData data, NetHandlerPlayClient netHandler);
}
