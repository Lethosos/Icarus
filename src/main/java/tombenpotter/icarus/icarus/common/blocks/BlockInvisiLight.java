package tombenpotter.icarus.common.blocks;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import tombenpotter.icarus.Icarus;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class BlockInvisiLight extends BlockContainer {

    public BlockInvisiLight() {
        super(Material.air);
        this.setStepSound(soundTypeCloth);
        this.setBlockBounds(0, 0, 0, 0, 0, 0);
        this.getLightValue();
        this.setBlockName(Icarus.name + ".invisilight");
    }

    @Override
    public void getSubBlocks(Item item, CreativeTabs tab, List list) {
    }

    @Override
    public ArrayList<ItemStack> getDrops(World world, int x, int y, int z, int meta, int fortune) {
        return new ArrayList();
    }

    @Override
    public int getLightValue() {
        return 15;
    }

    @Override
    public int getRenderType() {
        return -1;
    }

    @Override
    public int quantityDropped(Random random) {
        return 0;
    }

    @Override
    public boolean canDropFromExplosion(Explosion explosion) {
        return false;
    }

    @Override
    public boolean isBlockNormalCube() {
        return false;
    }

    @Override
    public boolean isOpaqueCube() {
        return false;
    }

    @Override
    public boolean isAir(IBlockAccess world, int x, int y, int z) {
        return true;
    }

    @Override
    public boolean canCollideCheck(int par1, boolean par2) {
        return false;
    }

    @Override
    public AxisAlignedBB getCollisionBoundingBoxFromPool(World par1World, int par2, int par3, int par4) {
        return null;
    }

    @Override
    public boolean isReplaceable(IBlockAccess world, int x, int y, int z) {
        return true;
    }

    @Override
    public boolean canBeReplacedByLeaves(IBlockAccess world, int x, int y, int z) {
        return true;
    }

    @Override
    public TileEntity createNewTileEntity(World p_149915_1_, int p_149915_2_) {
        return new TileInvisibleLight();
    }

    public static class TileInvisibleLight extends TileEntity {
        @Override
        public void updateEntity() {
            if (worldObj.getTotalWorldTime() % 20 == 0) {
                List<EntityLivingBase> entityLivingBaseList = worldObj.getEntitiesWithinAABB(EntityLivingBase.class, AxisAlignedBB.getBoundingBox(xCoord - 2, yCoord - 2, zCoord - 2, xCoord + 2, yCoord + 2, zCoord + 2));
                if (entityLivingBaseList.isEmpty()) {
                    worldObj.setBlockToAir(xCoord, yCoord, zCoord);
                }
            }
        }

        @Override
        public void markDirty() {
            super.markDirty();
            this.worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
        }
    }
}
