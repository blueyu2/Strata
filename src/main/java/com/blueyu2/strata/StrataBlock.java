package com.blueyu2.strata;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.event.ForgeEventFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by blueyu2 on 1/4/16.
 */
public class StrataBlock extends Block {
    String blockId;
    Block baseBlock;
    int meta;
    String oreTexture;
    String stoneTexture;
    int maxDepth = 2;

    enum Type {STONE, ORE}

    Type type;

    //Stone
    public StrataBlock(String blockId, int meta, String stoneTexture){
        this(blockId, meta, stoneTexture, stoneTexture);
        type = Type.STONE;
    }

    //Ore
    public StrataBlock(String blockId, int meta, String oreTexture, String stoneTexture) {
        super(Material.rock);
        baseBlock = Block.getBlockFromName(blockId);
        this.blockId = blockId;
        this.meta = meta;
        this.oreTexture = oreTexture;
        this.stoneTexture = stoneTexture;
        this.setCreativeTab(CreativeTabs.tabBlock);
        type = Type.ORE;
        this.maxDepth = StrataConfig.maxDepth;
    }

    @Override
    public void onBlockDestroyedByPlayer(World world, int x, int y, int z, int meta) {
        baseBlock.onBlockDestroyedByPlayer(world, x, y, z, meta);
    }

    @Override
    protected boolean canSilkHarvest(){
        return true;
    }

    @Override
    public boolean canHarvestBlock(EntityPlayer player, int meta){
        return type == Type.STONE ? super.canHarvestBlock(player, meta) : baseBlock.canHarvestBlock(player, this.meta);
    }

    @Override
    public int getHarvestLevel(int meta){
        return type == Type.STONE ? (meta == 0 ? 2 : 3) : (baseBlock.getHarvestLevel(this.meta));
    }

    @Override
    public String getHarvestTool(int meta){
        return baseBlock.getHarvestTool(this.meta);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void randomDisplayTick(World world, int x, int y, int z, Random random){
        int meta = world.getBlockMetadata(x, y, z);

        try{
            world.setBlock(x, y, z, baseBlock);
            baseBlock.randomDisplayTick(world, x, y, z, random);
        } catch (Exception e){
            world.setBlock(x, y, z, this, meta, 0);
            throw new RuntimeException(e);
        }
        world.setBlock(x, y, z, this, meta, 0);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void getSubBlocks(Item item, CreativeTabs tab, List list){
        for(int i = 0; i < maxDepth; i++){
            list.add(new ItemStack(item, 1, i));
        }
    }

    public IIcon[] icons = new IIcon[maxDepth];

    @SideOnly(Side.CLIENT)
    @Override
    public IIcon getIcon(int side, int meta){
        return meta < maxDepth ? icons[meta] : Blocks.dirt.getIcon(side, meta);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void registerBlockIcons(IIconRegister register){
        if(register instanceof TextureMap){
            TextureMap map = (TextureMap) register;
            for (int i = 0; i < maxDepth; i++) {
                String name = StrataTexture.getDerivedName(oreTexture, i);
                TextureAtlasSprite texture = map.getTextureExtry(name);
                if (texture == null) {
                    texture = new StrataTexture(oreTexture, stoneTexture, i, type);
                    map.setTextureEntry(name, texture);
                }
                icons[i] = map.getTextureExtry(name);
            }
        }
    }

    @Override
    public int damageDropped(int meta){
        return baseBlock.damageDropped(this.meta);
    }

    @Override
    public ArrayList<ItemStack> getDrops(World world, int x, int y, int z, int metadata, int fortune){
        return baseBlock.getDrops(world, x, y, z, this.meta, fortune);
    }

    @Override
    public float getBlockHardness(World world, int x, int y, int z){
        int metadata = world.getBlockMetadata(x, y, z);
        float hardness = this.blockHardness;
        world.setBlockMetadataWithNotify(x, y, z, meta, 0);
        try{
            hardness = baseBlock.getBlockHardness(world, x, y, z);
        }
        catch (Exception e){
            world.setBlockMetadataWithNotify(x, y, z, metadata, 0);
            throw new RuntimeException(e);
        }

        world.setBlockMetadataWithNotify(x, y, z, metadata, 0);
        return hardness;
    }

    @Override
    public int getExpDrop(IBlockAccess world, int metadata, int fortune){
        return baseBlock.getExpDrop(world, this.meta, fortune);
    }

    //Fix getPickBlock
    @Override
    public int getDamageValue(World world, int x, int y, int z){
        return world.getBlockMetadata(x, y, z);
    }

    //Because stone is too soft
    @Override
    public float getPlayerRelativeBlockHardness(EntityPlayer player, World world, int x, int y, int z) {
        if(type == Type.ORE)
            return super.getPlayerRelativeBlockHardness(player, world, x, y, z);

        //Copied from ForgeHooks.blockStrength
        int metadata = world.getBlockMetadata(x, y, z);
        float hardness = this.getBlockHardness(world, x, y, z);
        if (hardness < 0.0F)
        {
            return 0.0F;
        }

        if (!canHarvestBlock(player, metadata))
        {
            return player.getBreakSpeed(this, true, metadata, x, y, z) / hardness / 400F;
        }
        else
        {
            return player.getBreakSpeed(this, false, metadata, x, y, z) / hardness / 30F;
        }
    }

    //Fix silk touch
    @Override
    public void harvestBlock(World world, EntityPlayer player, int x, int y, int z, int meta){
        player.addStat(StatList.mineBlockStatArray[getIdFromBlock(this)], 1);
        player.addExhaustion(0.025F);

        if (this.canSilkHarvest(world, player, x, y, z, meta) && EnchantmentHelper.getSilkTouchModifier(player))
        {
            ArrayList<ItemStack> items = new ArrayList<ItemStack>();

            ItemStack itemstack = new ItemStack(Item.getItemFromBlock(baseBlock), 1, this.meta);

            items.add(itemstack);

            ForgeEventFactory.fireBlockHarvesting(items, world, baseBlock, x, y, z, this.meta, 0, 1.0f, true, player);
            for (ItemStack is : items)
            {
                this.dropBlockAsItem(world, x, y, z, is);
            }
        }
        else
        {
            harvesters.set(player);
            int i1 = EnchantmentHelper.getFortuneModifier(player);
            this.dropBlockAsItem(world, x, y, z, meta, i1);
            harvesters.set(null);
        }
    }
}
