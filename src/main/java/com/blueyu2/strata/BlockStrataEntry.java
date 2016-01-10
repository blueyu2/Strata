package com.blueyu2.strata;

/**
 * Created by blueyu2 on 1/10/16.
 */
public class BlockStrataEntry {
    public String modid;
    public String baseBlock;
    public int metadata;
    public String oreTexture;
    public String stoneTexture;
    public enum Type {ORE, STONE}
    public Type type;

    public BlockStrataEntry(String modid, String baseBlock, int metadata, String stoneTexture){
        this(modid, baseBlock, metadata, null, stoneTexture);
        type = Type.STONE;
    }

    public BlockStrataEntry(String modid, String baseBlock, int metadata, String oreTexture, String stoneTexture){
        this.modid = modid;
        this.baseBlock = baseBlock;
        this.metadata = metadata;
        this.oreTexture = oreTexture;
        this.stoneTexture = stoneTexture;
        type = Type.ORE;
    }
}
