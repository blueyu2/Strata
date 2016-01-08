package com.blueyu2.strata;

import net.minecraft.block.Block;

/**
 * Created by blueyu2 on 1/6/16.
 */
public class BlockMeta {
    public Block block;
    public int meta;

    public BlockMeta(Block block, int meta){
        this.block = block;
        this.meta = meta;
    }

    @Override
    public boolean equals(Object obj){
        if(obj instanceof BlockMeta){
            BlockMeta blockMeta = (BlockMeta) obj;
            return this.block.equals(blockMeta.block) && this.meta == blockMeta.meta;
        }
        return false;
    }
}
