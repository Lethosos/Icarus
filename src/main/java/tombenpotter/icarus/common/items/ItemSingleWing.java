package tombenpotter.icarus.common.items;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import tombenpotter.icarus.Main;
import tombenpotter.icarus.common.IcarusItems;
import tombenpotter.icarus.utils.Reference;

import java.util.HashMap;
import java.util.List;

public class ItemSingleWing<IIcon> extends Item {

    public IIcon[] icon;
    private HashMap<Integer, String> tooltipForMeta = new HashMap<Integer, String>();

    public ItemSingleWing() {
        setCreativeTab(Main.creativeTab);
        setUnlocalizedName(Reference.NAME + ".singleWing");
        setHasSubtypes(true);
    }

    public void addTooltip(int meta, String tooltip) {
        tooltipForMeta.put(meta, tooltip);
    }

    @Override
    public String getUnlocalizedName(ItemStack stack) {
        String name = IcarusItems.wingNames.get(stack.getItemDamage());
        return getUnlocalizedName() + "." + name;
    }

    @SideOnly(Side.CLIENT)
    public IIcon getIconFromDamage(int meta) {
        return this.icon[meta];
    }

    @SideOnly(Side.CLIENT)
    public void getSubItems(Item item, CreativeTabs par2CreativeTabs, List<ItemStack> list) {
        for (int i = 0; i < IcarusItems.wingNames.size(); i++) {
            if (GameRegistry.makeItemStack("Item" + IcarusItems.wingNames.get(i) + "s",0,0,"");
            {
                list.add(new ItemStack(this, 1, i));
            }
        }
    }

    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, EntityPlayer player, List<String> list, boolean held) {
        for (int i : tooltipForMeta.keySet()) {
            if (i == stack.getItemDamage()) {
                list.add(tooltipForMeta.get(i));
            }
        }
    }
}
