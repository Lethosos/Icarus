package tombenpotter.icarus.common.network;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import tombenpotter.icarus.common.util.HoverHandler;

public class PacketClientConfig implements IMessage, IMessageHandler<PacketClientConfig, IMessage> {

    public static MessageContext envelope;

    public PacketClientConfig() {
    }

    @Override
    public void fromBytes(ByteBuf buf) {
    }

    @Override
    public void toBytes(ByteBuf buf) {
    }

    @Override
    public IMessage onMessage(PacketClientConfig message, MessageContext ctx) {
        HoverHandler.addHoldKeyToHover(ctx.getServerHandler().player);
        envelope = ctx;
        return null;
    }
}
