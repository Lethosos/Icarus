/*
package tombenpotter.icarus;


import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import tombenpotter.icarus.common.IcarusBlocks;
import tombenpotter.icarus.common.IcarusEnchants;
import tombenpotter.icarus.common.IcarusItems;
import tombenpotter.icarus.common.network.PacketHandler;
import tombenpotter.icarus.common.util.EventHandler;
import tombenpotter.icarus.proxies.CommonProxy;

import java.io.File;

@Mod(modid = Icarus.modid, name = Icarus.name, version = Icarus.version, dependencies = Icarus.depend)
public class Icarus {

    public static final String modid = "TIcarus";
    public static final String name = "Icarus";
    public static final String version = "@VERSION@";
    public static final String texturePath = "icarus";
    public static final String channel = "Icarus";
    public static final String depend = "after:Thaumcraft;after:ThermalExpansion;after:Botania;after:EnderIO;after:aura;" +
            "after:witchery;after:erebus;after:ProjRed|Core;after:bluepower;after:BiomesOPlenty;after:ImmersiveEngineering";
    public static final String clientProxy = "tombenpotter.icarus.proxies.ClientProxy";
    public static final String commonProxy = "tombenpotter.icarus.proxies.CommonProxy";
    private File configDir;
    public static CreativeTabs creativeTab = new CreativeTabs("tab" + name) {
        @Override
        public Item getTabIconItem() {
            return IcarusItems.goldDiamondWings;
        }
    };

    @SidedProxy(serverSide = Icarus.commonProxy, clientSide = Icarus.clientProxy)
    public static CommonProxy proxy;

    @Mod.Instance(Icarus.modid)
    public static Icarus instance;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        instance = this;
        ConfigHandler.init(event.getSuggestedConfigurationFile());
        IcarusEnchants.registerEnchants();
        IcarusItems.registerItems();
        IcarusBlocks.registerBlocks();
        IcarusBlocks.registerTiles();

        configDir = event.getModConfigurationDirectory();
    }

    @Mod.EventHandler
    public void load(FMLInitializationEvent event) {
        proxy.registerRenders();
        IcarusItems.registerItemsInInitBecausePixlepix();
        MinecraftForge.EVENT_BUS.register(new EventHandler());
        FMLCommonHandler.instance().bus().register(new EventHandler());
        PacketHandler.registerPackets();
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        if (Loader.isModLoaded("Thaumcraft")) {
            Configuration thaumcraftConfig = new Configuration(new File(configDir, "Thaumcraft.cfg"));
            ConfigHandler.dimensionWingsDisabled.add(thaumcraftConfig.get("Biomes", "outer_lands_dim", -42).getInt());
        }
    }
}
 */