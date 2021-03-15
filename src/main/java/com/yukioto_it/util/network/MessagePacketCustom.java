package com.yukioto_it.util.network;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

import java.nio.charset.StandardCharsets;

public class MessagePacketCustom implements IMessage {
    public String channel = "";
    public ByteBuf data;

    public MessagePacketCustom() {

    }

    public MessagePacketCustom(String channel, ByteBuf buf) {
        this.channel = channel;
        this.data = buf;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        int channelLen = buf.bytesBefore((byte)'\0');
        byte[] channelBytes = new byte[channelLen];
        for (int i = 0; i < channelLen; i++) {
            channelBytes[i] = buf.readByte();
        }
        this.channel = new String(channelBytes);
        buf.skipBytes(1);

        int len = buf.readableBytes();
        this.data = buf.readBytes(len);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        byte[] channelBytes = this.channel.getBytes(StandardCharsets.UTF_8);
        for (byte sByte : channelBytes) {
            buf.writeByte(sByte);
        }
        buf.writeByte((byte)'\0');

        buf.writeBytes(this.data);
    }
}
