package tombenpotter.icarus.common.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import tombenpotter.icarus.api.wings.ISpecialWing;
import tombenpotter.icarus.common.items.ItemWing;

public class PacketJump implements IMessage, IMessageHandler<PacketJump, IMessage> {

    public double jump;
    public boolean isSpecialWing;

    public PacketJump() {
    }

    public PacketJump(double jump, boolean isSpecialWing) {
        this.jump = jump;
        this.isSpecialWing = isSpecialWing;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        jump = buf.readDouble();
        isSpecialWing = buf.readBoolean();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeDouble(jump);
        buf.writeBoolean(isSpecialWing);
    }

    @Override
    public IMessage onMessage(PacketJump message, MessageContext ctx) {
        EntityPlayer player = ctx.getServerHandler().player;
        ItemStack wingStack = player.inventory.armorInventory.get(2);

        if (wingStack == null || !(wingStack.getItem() instanceof ItemWing)) {
            return null;
        }

        ItemWing itemWing = (ItemWing) wingStack.getItem();

        if (message.isSpecialWing) {
            ISpecialWing specialWing = (ISpecialWing) itemWing;
            if (!specialWing.canWingBeUsed(player.inventory.armorInventory.get(2), player)) {
                return null;
            }
            specialWing.onWingFlap(player.inventory.armorInventory.get(2), player);
        }

        player.motionY = message.jump;
        player.fallDistance = 0;

        itemWing.handleExhaustion(player.getEntityWorld(), player, wingStack);

        return null;
    }
}
