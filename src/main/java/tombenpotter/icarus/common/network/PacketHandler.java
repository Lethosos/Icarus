package tombenpotter.icarus.common.network;

import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;
import tombenpotter.icarus.Main;

public class PacketHandler {

    public static final SimpleNetworkWrapper INSTANCE = NetworkRegistry.INSTANCE.newSimpleChannel(Main.channel);

    public static void registerPackets() {
        INSTANCE.registerMessage(PacketJump.class, PacketJump.class, 0, Side.SERVER);
        INSTANCE.registerMessage(PacketClientConfig.class, PacketClientConfig.class, 1, Side.SERVER);
        INSTANCE.registerMessage(PacketHoverSync.class, PacketHoverSync.class, 2, Side.SERVER);
    }
}
