package com.yukioto_it.util.network;

import com.yukioto_it.util.inventory.container.IGuiEventContainer;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Container;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class MessagePacketGuiEvent implements IMessage {
    public int eventID = 0;

    public MessagePacketGuiEvent() {

    }

    public MessagePacketGuiEvent(int eventID) {
        this.eventID = eventID;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        eventID = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(eventID);
    }

    public static class GuiEventMessageHandler implements IMessageHandler<MessagePacketGuiEvent, IMessage> {

        @Override
        public IMessage onMessage(MessagePacketGuiEvent message, MessageContext ctx) {
            EntityPlayerMP player = ctx.getServerHandler().player;
            Container container = player.openContainer;
            int eventID = message.eventID;

            if (container instanceof IGuiEventContainer) {
                ((IGuiEventContainer)container).onEventRaised(eventID);
            }

            return null;
        }
    }
}
