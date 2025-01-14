package tombenpotter.icarus.common.items;

import cofh.api.energy.IEnergyContainerItem;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import net.minecraftforge.common.ISpecialArmor;
import tombenpotter.icarus.ConfigHandler;
import tombenpotter.icarus.api.IcarusConstants;
import tombenpotter.icarus.api.wings.ISpecialWing;
import tombenpotter.icarus.common.util.IcarusHelper;
import tombenpotter.icarus.common.util.IcarusWing;
import tombenpotter.icarus.common.util.cofh.EnergyHelper;
import tombenpotter.icarus.common.util.cofh.StringHelper;

import java.util.List;

public class ItemWingRF extends ItemWing implements IEnergyContainerItem, ISpecialWing {

    public static final ArmorProperties FLUX = new ArmorProperties(0, 0.20D, Integer.MAX_VALUE);
    public double absorbRatio = 0.9D;
    //Armor handling methods taken from Redstone Arsenal.
    int capacity;
    int receive = 2000;
    int send = 1000;
    int energyPerDamage = 160;

    public ItemWingRF(ArmorMaterial material, IcarusWing icarusWing) {
        super(material, icarusWing);
        capacity = icarusWing.durability;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean held) {
        if (stack.stackTagCompound == null)
            EnergyHelper.setDefaultEnergyTag(stack, 0);

        if (stack.hasTagCompound() && stack.stackTagCompound.hasKey(IcarusConstants.NBT_ITEMSTACK)) {
            list.add(StringHelper.LIGHT_BLUE + StringHelper.localize("tooltip.icarus.render.armor") + StringHelper.END + ": " + ItemStack.loadItemStackFromNBT(stack.stackTagCompound.getCompoundTag(IcarusConstants.NBT_ITEMSTACK)).getDisplayName());
        }

        if (!StringHelper.isShiftKeyDown()) {
            list.add(IcarusHelper.pressShiftForDetails());
        } else if (StringHelper.isShiftKeyDown()) {
            list.add(StringHelper.LIGHT_BLUE + StringHelper.localize("tooltip.icarus.energy") + StringHelper.END + ": " + stack.stackTagCompound.getInteger("Energy") + " / " + capacity + " RF");
            if (ConfigHandler.showWingsStats) {
                list.addAll(tooltip(stack));
            }
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void getSubItems(Item item, CreativeTabs tab, List list) {
        list.add(EnergyHelper.setDefaultEnergyTag(new ItemStack(this, 1, 0), 0));
        list.add(EnergyHelper.setDefaultEnergyTag(new ItemStack(this, 1, 0), getMaxEnergyStored(new ItemStack(item))));
    }

    @Override
    public boolean isDamaged(ItemStack stack) {
        return true;
    }

    @Override
    public int getDisplayDamage(ItemStack stack) {
        if (stack.stackTagCompound == null) {
            EnergyHelper.setDefaultEnergyTag(stack, 0);
        }
        return capacity - stack.stackTagCompound.getInteger("Energy");
    }

    public int getEnergyPerDamage(ItemStack stack) {
        return energyPerDamage;
    }

    //ISpecialArmor stuff
    @Override
    public ArmorProperties getProperties(EntityLivingBase entity, ItemStack stack, DamageSource source, double damage, int slot) {
        if (stack.hasTagCompound() && stack.stackTagCompound.hasKey(IcarusConstants.NBT_ITEMSTACK)) {
            ItemStack armorStack = ItemStack.loadItemStackFromNBT(stack.stackTagCompound.getCompoundTag(IcarusConstants.NBT_ITEMSTACK));
            Item armor = armorStack.getItem();

            if (armor instanceof ISpecialArmor) {
                return ((ISpecialArmor) armor).getProperties(entity, stack, source, damage, slot);
            }
        }

        if (source.damageType.equals("flux")) {
            return FLUX;
        } else if (source.isUnblockable()) {
            int absorbMax = getEnergyPerDamage(stack) > 0 ? 25 * getEnergyStored(stack) / getEnergyPerDamage(stack) : 0;
            return new ArmorProperties(0, absorbRatio * getArmorMaterial().getDamageReductionAmount(armorType) * 0.025, absorbMax);
        }
        int absorbMax = getEnergyPerDamage(stack) > 0 ? 25 * getEnergyStored(stack) / getEnergyPerDamage(stack) : 0;
        return new ArmorProperties(0, absorbRatio * getArmorMaterial().getDamageReductionAmount(armorType) * 0.05, absorbMax);
    }

    @Override
    public int getArmorDisplay(EntityPlayer player, ItemStack stack, int slot) {
        if (stack.hasTagCompound() && stack.stackTagCompound.hasKey(IcarusConstants.NBT_ITEMSTACK)) {
            ItemStack armorStack = ItemStack.loadItemStackFromNBT(stack.stackTagCompound.getCompoundTag(IcarusConstants.NBT_ITEMSTACK));
            Item armor = armorStack.getItem();

            if (armor instanceof ISpecialArmor) {
                return ((ISpecialArmor) armor).getArmorDisplay(player, stack, slot);
            }
        }

        if (getEnergyStored(stack) >= getEnergyPerDamage(stack)) {
            return 20 * 40 / 100;
        }
        return 0;
    }

    @Override
    public void damageArmor(EntityLivingBase entity, ItemStack stack, DamageSource source, int damage, int slot) {
        if (stack.hasTagCompound() && stack.stackTagCompound.hasKey(IcarusConstants.NBT_ITEMSTACK)) {
            ItemStack armorStack = ItemStack.loadItemStackFromNBT(stack.stackTagCompound.getCompoundTag(IcarusConstants.NBT_ITEMSTACK));
            Item armor = armorStack.getItem();

            if (armor instanceof ISpecialArmor) {
                ((ISpecialArmor) armor).damageArmor(entity, stack, source, damage, slot);
            }
        }

        if (source.damageType.equals("flux")) {
            boolean p = source.getEntity() == null;
            receiveEnergy(stack, damage * (p ? energyPerDamage / 2 : getEnergyPerDamage(stack)), false);
        } else {
            extractEnergy(stack, damage * getEnergyPerDamage(stack), false);
        }
    }

    //IEnergyContainerItem Stuff
    @Override
    public int receiveEnergy(ItemStack stack, int i, boolean simulate) {
        if (stack.stackTagCompound == null)
            EnergyHelper.setDefaultEnergyTag(stack, 0);

        int energy = stack.stackTagCompound.getInteger("Energy");
        int energyReceived = Math.min(i, Math.min(capacity - energy, receive));

        if (!simulate) {
            energy += energyReceived;
            stack.stackTagCompound.setInteger("Energy", energy);
        }

        return energyReceived;
    }

    @Override
    public int extractEnergy(ItemStack stack, int extract, boolean simulate) {
        if (stack.stackTagCompound == null)
            EnergyHelper.setDefaultEnergyTag(stack, 0);

        int energy = stack.stackTagCompound.getInteger("Energy");
        int energyExtracted = Math.min(extract, Math.min(energy, send));

        if (!simulate) {
            energy -= energyExtracted;
            stack.stackTagCompound.setInteger("Energy", energy);
        }

        return energyExtracted;
    }

    @Override
    public int getEnergyStored(ItemStack stack) {
        if (stack.stackTagCompound == null)
            EnergyHelper.setDefaultEnergyTag(stack, 0);

        return stack.stackTagCompound.getInteger("Energy");
    }

    @Override
    public int getMaxEnergyStored(ItemStack stack) {
        return capacity;
    }

    @Override
    public void handleExhaustion(World world, EntityPlayer player, ItemStack stack) {
        float exhaustion = ConfigHandler.hungerConsumed / 2;
        if (player.worldObj.provider.dimensionId == -1) {
            exhaustion += 0.25F;
        } else if (player.worldObj.provider.dimensionId == 1) {
            exhaustion -= 0.125F;
        }
        player.addExhaustion(exhaustion);
    }

    @Override
    public void onWingFlap(ItemStack stack, EntityPlayer player) {
        extractEnergy(stack, energyPerDamage / 2, false);
    }

    @Override
    public void onWingHover(ItemStack stack, EntityPlayer player) {
        extractEnergy(stack, energyPerDamage / 4, false);
    }

    @Override
    public void onWingTick(ItemStack stack, EntityPlayer player) {
    }

    @Override
    public boolean canWingBeUsed(ItemStack stack, EntityPlayer player) {
        return getEnergyStored(stack) >= energyPerDamage;
    }

    @Override
    public List<String> getDisplayString(World clientWorld, EntityPlayer clientPlayer, ItemStack stack) {
        List<String> list = super.getDisplayString(clientWorld, clientPlayer, stack);
        list.add(StringHelper.LIGHT_BLUE + StringHelper.localize("tooltip.icarus.energy") + StringHelper.END + StringHelper.LIGHT_GRAY + ": " + stack.stackTagCompound.getInteger("Energy") + " / " + capacity + " RF" + StringHelper.END);
        return list;
    }

    public static class ItemWingPotato extends ItemWingRF {

        public ItemWingPotato(ArmorMaterial material, IcarusWing icarusWing) {
            super(material, icarusWing);
        }

        @Override
        @SideOnly(Side.CLIENT)
        public void getSubItems(Item item, CreativeTabs tab, List list) {
            list.add(EnergyHelper.setDefaultEnergyTag(new ItemStack(this, 1, 0), getMaxEnergyStored(new ItemStack(item))));
        }

        @Override
        public int receiveEnergy(ItemStack stack, int i, boolean simulate) {
            return 0;
        }

        @Override
        public void onUpdate(ItemStack stack, World world, Entity entity, int slot, boolean held) {
            super.onUpdate(stack, world, entity, slot, held);

            if (!stack.hasTagCompound()) {
                NBTTagCompound tag = new NBTTagCompound();
                tag.setInteger("Energy", getMaxEnergyStored(stack));
                stack.setTagCompound(tag);
            }
        }
    }
}
