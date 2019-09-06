package tombenpotter.icarus;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

import java.io.File;

import tombenpotter.icarus.common.IcarusBlocks;
import tombenpotter.icarus.common.IcarusEnchants;
import tombenpotter.icarus.common.IcarusItems;
import tombenpotter.icarus.common.network.PacketHandler;
import tombenpotter.icarus.proxies.CommonProxy;
import tombenpotter.icarus.utils.Reference;

@Mod(modid = Reference.MOD_ID, name = Reference.NAME, version = Reference.VERSION)
public class Main {

    public static File config;

    public static CreativeTabs creativeTab;
    public static String channel = "Icarus";

    public static void setCreativeTab(CreativeTabs creativeTab) {
        Main.creativeTab = creativeTab;
        creativeTab.setBackgroundImageName("DiamondGoldWing.png");
    }

    @Instance
    public static Main instance;

    @SidedProxy(clientSide = Reference.CLIENT_PROXY_CLASS, serverSide = Reference.COMMON_PROXY_CLASS)
    public static CommonProxy proxy;

    //public static final CreativeTabs MINIDISC = new DiscTab("minidisc_tab");

    @EventHandler
    public static void PreInit(FMLPreInitializationEvent event)
    {
        ConfigHandler.init(event.getSuggestedConfigurationFile());
        IcarusEnchants.registerEnchants();
        IcarusItems.registerItems();
        IcarusBlocks.registerBlocks();
        IcarusBlocks.registerTiles();

        //config = event.getModConfigurationDirectory();
    }

    @EventHandler
    public static void Init(FMLInitializationEvent event)
    {
        IcarusItems.registerItemsInInitBecausePixlepix();
        //MinecraftForge.EVENT_BUS.register(new EventHandler());
        //FMLCommonHandler.instance().bus().register(new EventHandler());
        PacketHandler.registerPackets();
    }

    @EventHandler
    public static void PostInit(FMLPostInitializationEvent event)
    {
        if (Loader.isModLoaded("Thaumcraft")) {
            Configuration thaumcraftConfig = new Configuration(new File(config, "Thaumcraft.cfg"));
            ConfigHandler.dimensionWingsDisabled.add(thaumcraftConfig.get("Biomes", "outer_lands_dim", -42).getInt());
        }
    }
}