package com.blueyu2.strata;

import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

/**
 * Created by blueyu2 on 1/5/16.
 */
public class StrataBlockItem extends ItemBlock {
    StrataBlock block;
    public StrataBlockItem(Block block) {
        super(block);
        this.block = (StrataBlock) block;
        this.setHasSubtypes(true);
    }

    public int getMetadata(int meta){
        return meta;
    }

    public String getItemStackDisplayName(ItemStack itemStack){
        ItemStack tempItemStack = new ItemStack(block.baseBlock, 1, block.meta);
        return tempItemStack.getDisplayName();
    }
}
