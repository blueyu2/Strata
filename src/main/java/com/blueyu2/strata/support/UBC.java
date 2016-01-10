package com.blueyu2.strata.support;

import com.blueyu2.strata.StrataBlock;
import com.blueyu2.strata.StrataConfig;
import com.blueyu2.strata.StrataRegistry;
import net.minecraft.block.Block;
import net.minecraftforge.common.config.Configuration;

import java.io.File;

/**
 * Created by blueyu2 on 1/9/16.
 */
public class UBC {
    private static final String modid = "UndergroundBiomes";
    private static final String metamorphic = "metamorphic";
    private static final String sedimentary = "sedimentary";
    private static final String igneous = "igneous";
    private static final String ore = "_ore";
    private static final String overlay = "_overlay";
    private static final String[] metamorphicTypes = new String[]{"gneiss", "eclogite", "marble", "quartzite", "blueschist", "greenschist", "soapstone", "migmatite"};
    private static final String[] sedimentaryTypes = new String[]{"limestone", "chalk", "shale", "siltstone", "ligniteBlock", "dolomite", "greywacke", "chert"};
    private static final String[] igneousTypes = new String[]{"redGranite", "blackGranite", "rhyolite", "andesite", "gabbro", "basalt", "komatiite", "dacite"};
    private static final String[] stoneTypes = new String[]{"Stone", "_monsterStoneEgg"};
    private static final String[] oreTypes = new String[]{"Amber", "Cinnabar", "Coal", "Copper", "Diamond", "Emerald", "Gold", "Iron", "Lapis", "Lead", "Manganese", "Redstone", "Ruby", "Sapphire", "Silver", "Tin"};
    //TODO olivine-peridot_overlay

    public static void load(){
        File ubcFile = new File(StrataConfig.configDir, modid + ".cfg");
        if(!ubcFile.exists()){
            Configuration ubcConfig = new Configuration(ubcFile, true);
            ubcConfig.load();
            loadUBCBlocks();
            for(Block block : StrataRegistry.blocks.values()){
                if(block instanceof StrataBlock){
                    StrataBlock sBlock = (StrataBlock) block;
                    String cat;

                    if(!sBlock.blockId.substring(0, sBlock.blockId.indexOf(':')).equals("UndergroundBiomes"))
                        continue;

                    switch (sBlock.type){
                        case STONE:
                            cat = StrataConfig.CATEGORY_STONE + sBlock.blockId;
                            if(sBlock.meta > 0)
                                cat = cat + ":" + sBlock.meta;
                            ubcConfig.get(cat, "stoneTexture", sBlock.stoneTexture);
                            break;
                        case ORE:
                            cat = StrataConfig.CATEGORY_ORE + sBlock.blockId;
                            if(sBlock.meta > 0)
                                cat = cat + ":" + sBlock.meta;
                            ubcConfig.get(cat, "oreTexture", sBlock.oreTexture);
                            ubcConfig.get(cat, "stoneTexture", sBlock.stoneTexture);
                            break;
                    }
                }
            }
            ubcConfig.save();
        }
    }

    public static void loadUBCBlocks(){
        for(int i = 0; i < metamorphicTypes.length; i++){
            loadUBCTypes(metamorphic, metamorphicTypes[i], i);
        }
        for(int i = 0; i < sedimentaryTypes.length; i++){
            loadUBCTypes(sedimentary, sedimentaryTypes[i], i);
        }
        for(int i = 0; i < igneousTypes.length; i++){
            loadUBCTypes(igneous, igneousTypes[i], i);
        }
    }

    public static void loadUBCTypes(String stone, String stoneType, int meta){
        for(int i = 0; i < stoneTypes.length; i++){
            StrataRegistry.registerStone(modid + ":" + stone + stoneTypes[i], meta, modid + ":" + stoneType);
        }

        for(int i = 0; i < oreTypes.length; i++){
            StrataRegistry.registerOre(modid + ":" + stone + ore + oreTypes[i], meta, modid + ":" + oreTypes[i].toLowerCase() + overlay, modid + ":" + stoneType);
        }
    }
}
