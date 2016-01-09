package com.blueyu2.strata;

import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.block.Block;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by blueyu2 on 1/4/16.
 */
public class StrataRegistry {
    public static Map<BlockMeta, Block> blocks = new HashMap<BlockMeta, Block>();
    public static ArrayList<BlockMeta> blockMetas = new ArrayList<BlockMeta>();

    public static BlockMeta getBlockMeta(Block block, int meta, boolean create){
        BlockMeta tempBlockMeta = new BlockMeta(block, meta);
        for(BlockMeta blockMeta : blockMetas){
            if(blockMeta.equals(tempBlockMeta)){
                return blockMeta;
            }
        }
        if(create)
            blockMetas.add(tempBlockMeta);
        return tempBlockMeta;
    }

    public static void initVanillaBlocks(){
        registerStone("minecraft:stone", 0, "stone");
        registerStone("minecraft:dirt", 0, "dirt");
        registerStone("minecraft:gravel", 0, "gravel");
        registerOre("minecraft:iron_ore", 0, "iron_ore", "stone");
        registerOre("minecraft:gold_ore", 0, "gold_ore", "stone");
        registerOre("minecraft:lapis_ore", 0, "lapis_ore", "stone");
        registerOre("minecraft:diamond_ore", 0, "diamond_ore", "stone");
        registerOre("minecraft:emerald_ore", 0, "emerald_ore", "stone");
        registerOre("minecraft:redstone_ore", 0, "redstone_ore", "stone");
        registerOre("minecraft:coal_ore", 0, "coal_ore", "stone");
    }

    public static void registerStone(String blockId, int meta, String stoneTexture){
        registerBlock(blockId, meta, null, stoneTexture, StrataBlock.Type.STONE);
    }

    public static void registerOre(String blockId, int meta, String oreTexture, String stoneTexture){
        registerBlock(blockId, meta, oreTexture, stoneTexture, StrataBlock.Type.ORE);
    }

    public static void registerBlock(String blockId, int meta, String oreTexture, String stoneTexture, StrataBlock.Type type){
        if("".equals(blockId) || "minecraft:air".equals(blockId))
            return;

        int index = blockId.indexOf(':');

        if(index > 1){
            String modName = blockId.substring(0, index);
            String blockName = blockId.substring(index + 1, blockId.length());
            Block baseBlock = Block.getBlockFromName(blockId);

            if(baseBlock != null){
                Block block;
                switch (type){
                    case ORE:
                        block = new StrataBlock(blockId, meta, oreTexture, stoneTexture);
                        blocks.put(getBlockMeta(baseBlock, meta, true), block);
                        GameRegistry.registerBlock(block, StrataBlockItem.class, "block|" + modName + "/" + blockName + "-" + meta);
                        break;
                    case STONE:
                        block = new StrataBlock(blockId, meta, stoneTexture);
                        blocks.put(getBlockMeta(baseBlock, meta, true), block);
                        GameRegistry.registerBlock(block, StrataBlockItem.class, "block|" + modName + "/" + blockName + "-" + meta);
                        break;
                }
            }
        }
        else{
            throw new RuntimeException("Block " + blockId + " is not formatted correctly. Must be in the form mod:block");
        }
    }
}
