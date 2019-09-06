package tombenpotter.icarus.common.network;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import tombenpotter.icarus.common.util.HoverHandler;

public class PacketHoverSync implements IMessage, IMessageHandler<PacketHoverSync, IMessage> {

    public boolean hoverState;

    public PacketHoverSync() {
    }

    public PacketHoverSync(boolean hoverState) {
        this.hoverState = hoverState;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        hoverState = buf.readBoolean();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeBoolean(hoverState);
    }

    @Override
    public IMessage onMessage(PacketHoverSync message, MessageContext ctx) {
        HoverHandler.setHover(ctx.getServerHandler().player, message.hoverState);
        return null;
    }
}
