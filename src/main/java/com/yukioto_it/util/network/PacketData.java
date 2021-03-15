package com.yukioto_it.util.network;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.minecraft.network.PacketBuffer;

public class PacketData extends PacketBuffer {

    public PacketData() {
        super(Unpooled.buffer());
    }

    public PacketData(ByteBuf buf) {
        super(buf);
    }
}
