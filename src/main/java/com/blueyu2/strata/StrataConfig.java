package com.blueyu2.strata;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraftforge.common.config.Configuration;

import java.io.File;

/**
 * Created by blueyu2 on 1/4/16.
 */
public class StrataConfig {
    public static final StrataConfig instance = new StrataConfig();
    public static final String CATEGORY_STONE = "stones.";
    public static final String CATEGORY_ORE = "ores.";
    public static final File configDir = new File(new File(Minecraft.getMinecraft().mcDataDir, "config"), "Strata");

    public static int maxDepth = 2;
    public static boolean uninstall = false;

    public void loadConfig(File file){
        Configuration vanillaConfig = new Configuration(new File(configDir, "minecraft.cfg"), true);

        vanillaConfig.load();



        Configuration baseConfig = new Configuration(file, true);

        baseConfig.load();
        uninstall = baseConfig.getBoolean("Uninstall", "Main", false, "Set this to true and load any worlds with Strata installed to replace all Strata blocks in world with the original blocks. This allows for safe removal of Strata without your worlds getting ruined.");
        baseConfig.save();

        StrataRegistry.initVanillaBlocks();

        vanillaConfig.load();
        for(Block block : StrataRegistry.blocks.values()){
            if(block instanceof StrataBlock){
                StrataBlock sBlock = (StrataBlock) block;
                String cat;

                switch (sBlock.type){
                    case STONE:
                        cat = CATEGORY_STONE + sBlock.blockId;
                        if(sBlock.meta > 0)
                            cat = cat + ":" + sBlock.meta;
                        vanillaConfig.get(cat, "stoneTexture", sBlock.stoneTexture);
                        break;
                    case ORE:
                        cat = CATEGORY_ORE + sBlock.blockId;
                        if(sBlock.meta > 0)
                            cat = cat + ":" + sBlock.meta;
                        vanillaConfig.get(cat, "oreTexture", sBlock.oreTexture);
                        vanillaConfig.get(cat, "stoneTexture", sBlock.stoneTexture);
                        break;
                }
            }
        }
        vanillaConfig.save();

        if(configDir.listFiles() != null){
            for(File configFile : configDir.listFiles()){
                String fileName = configFile.getName();
                String extension = fileName.substring(fileName.lastIndexOf('.') + 1);
                if(!extension.equals("cfg"))
                    continue;

                Configuration config = new Configuration(configFile, true);
                config.load();
                for(String cat : config.getCategoryNames()){
                    if(cat.startsWith(CATEGORY_STONE)){
                        addBlock(cat, StrataBlock.Type.STONE, config);
                    }
                    else if(cat.startsWith(CATEGORY_ORE)){
                        addBlock(cat, StrataBlock.Type.ORE, config);
                    }
                }
                config.save();
            }
        }
    }

    private void addBlock(String cat, StrataBlock.Type type, Configuration config){
        String blockId = "NOPE";
        switch (type){
            case STONE:
                blockId = cat.substring(CATEGORY_STONE.length());
                break;
            case ORE:
                blockId = cat.substring(CATEGORY_ORE.length());
                break;
        }
        if(blockId.equals("NOPE"))
            return;

        int index = blockId.indexOf(':');
        int meta = 0;

        try{
            String[] values = blockId.split(":");
            //Add 1 to account for ':'
            blockId = blockId.substring(0, values[0].length() + 1 + values[1].length());
            meta = Integer.parseInt(values[2]);
        }
        catch (Exception e){
            //Probably doesn't have metadata
        }

        if(index > 1){
            Block baseBlock = Block.getBlockFromName(blockId);
            if(baseBlock != null){
                if(StrataRegistry.blocks.containsKey(StrataRegistry.getBlockMeta(baseBlock, meta, false)))
                    return;
                switch (type){
                    case STONE:
                        StrataRegistry.registerStone(blockId, meta, config.get(cat, "stoneTexture", "").getString().trim());
                        break;
                    case ORE:
                        StrataRegistry.registerOre(blockId, meta, config.get(cat, "oreTexture", "").getString().trim(), config.get(cat, "stoneTexture", "").getString().trim());
                }
            }
        }
    }
}
