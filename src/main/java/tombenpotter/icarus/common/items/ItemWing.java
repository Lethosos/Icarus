package tombenpotter.icarus.common.items;


import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.ISpecialArmor;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import tombenpotter.icarus.ConfigHandler;
import tombenpotter.icarus.Main;
import tombenpotter.icarus.api.IcarusConstants;
import tombenpotter.icarus.api.wings.ISpecialWing;
import tombenpotter.icarus.api.wings.IWingHUD;
import tombenpotter.icarus.api.wings.Wing;
import tombenpotter.icarus.common.network.PacketHandler;
import tombenpotter.icarus.common.network.PacketJump;
import tombenpotter.icarus.common.util.HoverHandler;
import tombenpotter.icarus.common.util.IcarusHelper;
import tombenpotter.icarus.common.util.cofh.StringHelper;
import tombenpotter.icarus.utils.Reference;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public abstract class ItemWing extends ItemArmor implements ISpecialArmor, IWingHUD {

    private Wing wing;

    public ItemWing(ArmorMaterial material, Wing wing) {
        super(material, 3, EntityEquipmentSlot.CHEST);

        this.wing = wing;

        setUnlocalizedName(Reference.NAME + ".wing." + getWing().name);
        setMaxDamage(getWing().durability);
        setCreativeTab(Main.creativeTab);
    }

    public Wing getWing() {
        return wing;
    }

    public Wing getWing(ItemStack stack) {
        return getWing();
    }

    public List<String> tooltip(ItemStack stack) {
        List<String> list = new ArrayList<String>();
        list.add(" ");
        list.add(StringHelper.LIGHT_BLUE + StringHelper.localize("tooltip.icarus.height") + StringHelper.END + ": " + getWing(stack).maxHeight);
        list.add(StringHelper.LIGHT_BLUE + StringHelper.localize("tooltip.icarus.boost") + StringHelper.END + ": " + getWing(stack).jumpBoost);
        list.add(StringHelper.LIGHT_BLUE + StringHelper.localize("tooltip.icarus.glide") + StringHelper.END + ": " + getWing(stack).glideFactor);
        list.add(StringHelper.LIGHT_BLUE + StringHelper.localize("tooltip.icarus.drag.rain") + StringHelper.END + ": " + getWing(stack).rainDrag);
        list.add(StringHelper.LIGHT_BLUE + StringHelper.localize("tooltip.icarus.drag.water") + StringHelper.END + ": " + getWing(stack).waterDrag);
        list.add(StringHelper.LIGHT_BLUE + StringHelper.localize("tooltip.icarus.fall.reduction") + StringHelper.END + ": " + getWing(stack).fallReductionFactor);
        list.add(" ");
        return list;
    }

    public void handleJump(World world, EntityPlayer player, ItemStack stack) {
        if (!player.onGround && world.isRemote) {
            if (Minecraft.getMinecraft().gameSettings.keyBindJump.isPressed()) {
                double jumpBoost = getWing(stack).jumpBoost;
                int enchantmentLevel = EnchantmentHelper.getEnchantmentLevel(Enchantment.getEnchantmentByID(ConfigHandler.boostEnchantID), stack);
                if (enchantmentLevel > 0) {
                    jumpBoost += (double) enchantmentLevel / 20;
                }

                PacketHandler.INSTANCE.sendToServer(new PacketJump(jumpBoost, stack.getItem() instanceof ISpecialWing));

                if (stack.getItem() instanceof ISpecialWing) {
                    ISpecialWing specialWing = (ISpecialWing) stack.getItem();
                    if (!specialWing.canWingBeUsed(stack, player)) {
                        return;
                    }
                    specialWing.onWingFlap(stack, player);
                }

                player.motionY = jumpBoost;
                player.fallDistance = 0;
            }
        }
    }

    public void handleWater(World world, EntityPlayer player, ItemStack stack) {
        if (player.isInsideOfMaterial(Material.WATER)) {
            player.motionY = getWing(stack).waterDrag;
        }
    }

    public void handleWeather(World world, EntityPlayer player, ItemStack stack) {
        if (player.world.isRaining()) {
            Field enableRain = ReflectionHelper.findField(Biome.class, "field_76765_S", "enableRain", "ax");
            try {
                double rainDrag = getWing(stack).rainDrag;
                int enchantmentLevel = EnchantmentHelper.getEnchantmentLevel(Enchantment.getEnchantmentByID(ConfigHandler.waterproofEnchantID), stack);
                if (enchantmentLevel > 0) {
                    rainDrag += 0.03 * enchantmentLevel;
                }

                if (enableRain.getBoolean(world.isRainingAt(player.getPosition())) &&
                        world.canBlockSeeSky(player.getPosition()))
                {
                    player.motionY = rainDrag;
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        if (player.world.isThundering()) {
            double rainDrag = getWing(stack).rainDrag;
            int enchantmentLevel = EnchantmentHelper.getEnchantmentLevel(Enchantment.getEnchantmentByID(ConfigHandler.waterproofEnchantID), stack);
            if (enchantmentLevel > 0) {
                rainDrag += 0.03 * enchantmentLevel;
            }

            if (world.isThundering() &&
                    world.canBlockSeeSky(player.getPosition())) {
                player.motionY = rainDrag;
            }

            if (!player.onGround && world.canBlockSeeSky(player.getPosition()) && world.rand.nextInt(500) == 0) {
                world.addWeatherEffect(new EntityLightningBolt(world, player.posX, player.posY, player.posZ, true);
                player.motionY -= 1.5;
            }
        }
    }

    public void handleHover(World world, EntityPlayer player, ItemStack stack) {
        if (HoverHandler.getHover(player) == HoverHandler.getHoldKeyToHover(player) && !player.onGround && player.motionY < 0) {
            if (stack.getItem() instanceof ISpecialWing) {
                if (!((ISpecialWing) stack.getItem()).canWingBeUsed(stack, player)) {
                    return;
                }
                ((ISpecialWing) stack.getItem()).onWingHover(stack, player);
            }

            double glideFactor = getWing(stack).glideFactor;
            int enchantmentLevel = EnchantmentHelper.getEnchantmentLevel(Enchantment.getEnchantmentByID(ConfigHandler.hoverEnchantID), stack);
            if (enchantmentLevel > 0) {
                glideFactor -= 0.03 * enchantmentLevel;
            }
            player.motionY *= glideFactor;
        }
    }

    public void handleHeight(World world, EntityPlayer player, ItemStack stack) {
        if (!player.onGround) {
            if (ConfigHandler.dimensionWingsAlwaysBurn.contains(world.provider.getDimension()) || (player.posY > (getWing(stack).maxHeight + ConfigHandler.maxHeightOffset) && world.isDaytime() && world.canBlockSeeTheSky((int) player.posX, (int) player.posY, (int) player.posZ) && !ConfigHandler.dimensionNoWingBurn.contains(world.provider.dimensionId))) {
                player.setFire(2);
                player.attackEntityFrom(DamageSource.IN_FIRE, 1.0F);
            }
        }
    }

    public void handleTick(World world, EntityPlayer player, ItemStack stack) {
        if (stack.getItem() instanceof ISpecialWing) {
            ISpecialWing specialWing = (ISpecialWing) stack.getItem();
            if (!specialWing.canWingBeUsed(stack, player)) {
                return;
            }
            specialWing.onWingTick(stack, player);
        }
    }

    public void handleExhaustion(World world, EntityPlayer player, ItemStack stack) {
        float exhaustion = ConfigHandler.hungerConsumed;
        if (player.world.provider.getDimension() == -1) {
            exhaustion += 0.5F;
        } else if (player.world.provider.getDimension() == 1) {
            exhaustion -= 0.25F;
        }
        player.addExhaustion(exhaustion);
    }

    @Override
    public void onArmorTick(World world, EntityPlayer player, ItemStack stack) {
        if (stack.hasTagCompound() && stack.stackTagCompound.hasKey(IcarusConstants.NBT_ITEMSTACK)) {
            ItemStack armorStack = ItemStack.loadItemStackFromNBT(stack.stackTagCompound.getCompoundTag(IcarusConstants.NBT_ITEMSTACK));
            ItemArmor armor = (ItemArmor) armorStack.getItem();

            armor.onArmorTick(world, player, stack);
        }

        handleTick(world, player, stack);
        handleWater(world, player, stack);
        handleWeather(world, player, stack);

        if (ConfigHandler.dimensionWingsDisabled.contains(world.provider.getDimension())) {
            return;
        }

        handleJump(world, player, stack);
        handleHover(world, player, stack);
        handleHeight(world, player, stack);
    }

    @Override
    public void onUpdate(ItemStack stack, World world, Entity entity, int slot, boolean held) {
        if (stack.hasTagCompound() && stack.stackTagCompound.hasKey(IcarusConstants.NBT_ITEMSTACK)) {
            ItemStack armorStack = ItemStack.loadItemStackFromNBT(stack.stackTagCompound.getCompoundTag(IcarusConstants.NBT_ITEMSTACK));
            Item armor = armorStack.getItem();

            armor.onUpdate(stack, world, entity, slot, held);
        }
    }

    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister iconRegister) {
        this.itemIcon = iconRegister.registerIcon(Reference.TEXTURE_PATH + ":doubleWings/" + getWing().name + "s");
    }

    @Override
    @SideOnly(Side.CLIENT)
    public String getArmorTexture(ItemStack stack, Entity entity, int slot, String type) {
        if (stack.hasTagCompound() && stack.stackTagCompound.hasKey(IcarusConstants.NBT_ITEMSTACK)) {
            ItemStack armorStack = ItemStack.loadItemStackFromNBT(stack.stackTagCompound.getCompoundTag(IcarusConstants.NBT_ITEMSTACK));
            Item armor = armorStack.getItem();

            //Because vanilla is stupid.
            if (armor == Items.LEATHER_CHESTPLATE) {
                return "textures/models/armor/leather_layer_1.png";
            } else if (armor == Items.CHAINMAIL_CHESTPLATE) {
                return "textures/models/armor/chainmail_layer_1.png";
            } else if (armor == Items.IRON_CHESTPLATE) {
                return "textures/models/armor/iron_layer_1.png";
            } else if (armor == Items.GOLDEN_CHESTPLATE) {
                return "textures/models/armor/gold_layer_1.png";
            } else if (armor == Items.DIAMOND_CHESTPLATE) {
                return "textures/models/armor/diamond_layer_1.png";
            } else {
                return armor.getArmorTexture(armorStack, entity, EntityEquipmentSlot.CHEST, type);
            }
        }

        return Icarus.texturePath + ":textures/items/EmptyArmor.png";
    }

    @Override
    @SideOnly(Side.CLIENT)
    public ModelBiped getArmorModel(EntityLivingBase entityLiving, ItemStack stack, int armorSlot) {
        if (stack.hasTagCompound() && stack.stackTagCompound.hasKey(IcarusConstants.NBT_ITEMSTACK)) {
            ItemStack armorStack = ItemStack.loadItemStackFromNBT(stack.stackTagCompound.getCompoundTag(IcarusConstants.NBT_ITEMSTACK));
            return armorStack.getItem().getArmorModel(entityLiving, armorStack, EntityEquipmentSlot.CHEST, new ModelBiped());
        }

        return super.getArmorModel(entityLiving, stack, EntityEquipmentSlot.CHEST, new ModelBiped());
    }

    @Override
    public boolean hasColor(ItemStack stack) {
        if (stack.hasTagCompound() && stack.stackTagCompound.hasKey(IcarusConstants.NBT_ITEMSTACK)) {
            ItemStack armorStack = ItemStack.loadItemStackFromNBT(stack.stackTagCompound.getCompoundTag(IcarusConstants.NBT_ITEMSTACK));
            return ((ItemArmor) armorStack.getItem()).hasColor(stack);
        }

        return super.hasColor(stack);
    }

    @Override
    public int getColor(ItemStack stack) {
        if (stack.hasTagCompound() && stack.stackTagCompound.hasKey(IcarusConstants.NBT_ITEMSTACK)) {
            ItemStack armorStack = ItemStack.loadItemStackFromNBT(stack.stackTagCompound.getCompoundTag(IcarusConstants.NBT_ITEMSTACK));
            return ((ItemArmor) armorStack.getItem()).getColor(stack);
        }
        return super.getColor(stack);
    }

    @Override
    public int getColorFromItemStack(ItemStack p_82790_1_) {
        return super.getColor(p_82790_1_);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean held) {
        if (stack.hasTagCompound() && stack.stackTagCompound.hasKey(IcarusConstants.NBT_ITEMSTACK)) {
            list.add(StringHelper.LIGHT_BLUE + StringHelper.localize("tooltip.icarus.render.armor") + StringHelper.END + ": " + ItemStack.loadItemStackFromNBT(stack.stackTagCompound.getCompoundTag(IcarusConstants.NBT_ITEMSTACK)).getDisplayName());
        }

        if (ConfigHandler.showWingsStats) {
            if (!StringHelper.isShiftKeyDown()) {
                list.add(IcarusHelper.pressShiftForDetails());
            } else {
                StringHelper.isShiftKeyDown();
                list.addAll(tooltip(stack));
            }
        }
    }

    @Override
    public List<String> getDisplayString(World clientWorld, EntityPlayer clientPlayer, ItemStack stack) {
        List<String> list = new ArrayList<String>();

        if (clientPlayer.fallDistance > 0.0F) {
            int fall = IcarusHelper.getFallDistanceWithWings(clientPlayer, this, stack);
            String str = StringHelper.localize("tooltip.icarus.fall.distance") + ": " + fall + StringHelper.END;
            if (fall > 0) {
                str = StringHelper.BOLD + StringHelper.ITALIC + StringHelper.LIGHT_RED + str;
            } else {
                str = StringHelper.LIGHT_GRAY + str;
            }
            list.add(str);
        }

        return list;
    }

    @Override
    public ArmorProperties getProperties(EntityLivingBase entity, ItemStack stack, DamageSource source, double damage, int slot) {
        if (stack.hasTagCompound() && stack.stackTagCompound.hasKey(IcarusConstants.NBT_ITEMSTACK)) {
            ItemStack armorStack = ItemStack.loadItemStackFromNBT(stack.stackTagCompound.getCompoundTag(IcarusConstants.NBT_ITEMSTACK));
            Item armor = armorStack.getItem();

            if (armor instanceof ISpecialArmor) {
                return ((ISpecialArmor) armor).getProperties(entity, stack, source, damage, slot);
            }
        }

        if (source.isUnblockable()) {
            return new ArmorProperties(0, 0, 0);
        }
        return new ArmorProperties(0, damageReduceAmount / 25D, stack.getMaxDamage() + 1 - stack.getItemDamage());
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
        return damageReduceAmount;
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

        stack.damageItem(1, entity);
    }
}
