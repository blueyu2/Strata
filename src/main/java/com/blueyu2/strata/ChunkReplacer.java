package com.blueyu2.strata;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.block.Block;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.event.terraingen.PopulateChunkEvent;
import net.minecraftforge.event.world.ChunkDataEvent;

import java.util.Map;
import java.util.Objects;

/**
 * Created by blueyu2 on 1/7/16.
 */
public class ChunkReplacer {
    int duration = 23;
    private static final String STRATA = "Strata";

    @SubscribeEvent
    public void initGen(PopulateChunkEvent.Pre event){
        generate(event.world.getChunkFromChunkCoords(event.chunkX, event.chunkZ));
    }

    @SubscribeEvent
    public void retroGen(ChunkDataEvent.Load event){
        if(StrataConfig.uninstall){
            generate(event.getChunk());
            return;
        }

        NBTTagCompound chunkData = event.getData();
        NBTTagCompound rgen = chunkData.getCompoundTag(STRATA);

        for(Block sBlock : StrataRegistry.blocks.values()){
            if(!rgen.getBoolean(sBlock.getUnlocalizedName())){
                generate(event.getChunk());
            }
        }
    }

    @SubscribeEvent
    public void retroGenSave(ChunkDataEvent.Save event){
        NBTTagCompound chunkData = event.getData();
        NBTTagCompound rgen = chunkData.getCompoundTag(STRATA);

        for(Block sBlock : StrataRegistry.blocks.values()){
            rgen.setBoolean(sBlock.getUnlocalizedName(), !StrataConfig.uninstall);
        }

        chunkData.setTag(STRATA, rgen);
    }

    public void generate(Chunk chunk){
        for(int x = 0; x < 16; x++){
            for(int z = 0; z < 16; z++){
                if(!StrataConfig.uninstall){
                    int durationCounter = 0;
                    int replaceMeta = StrataConfig.maxDepth - 1;
                    if(replaceMeta < 0){
                        return;
                    }
                    for(int y = 0; y < 256; y++){
                        Block block = chunk.getBlock(x, y, z);
                        int meta = chunk.getBlockMetadata(x, y, z);
                        Block replace = StrataRegistry.blocks.get(StrataRegistry.getBlockMeta(block, meta, false));
                        if(replace != null){
                            chunk.func_150807_a(x, y, z, replace, replaceMeta);
                            durationCounter++;
                        }

                        if(durationCounter >= duration){
                            durationCounter = 0;
                            replaceMeta--;
                            if(replaceMeta < 0)
                                break;
                        }
                    }
                }
                else{
                    for(int y = 0; y < 256; y++){
                        Block block = chunk.getBlock(x, y, z);
                        BlockMeta replace = getKeyByValue(StrataRegistry.blocks, block);
                        if(replace != null){
                            chunk.func_150807_a(x, y, z, replace.block, replace.meta);
                        }
                    }
                }
            }
        }
    }

    //Copied from: http://stackoverflow.com/a/2904266
    public static <T, E> T getKeyByValue(Map<T, E> map, E value) {
        for (Map.Entry<T, E> entry : map.entrySet()) {
            if (Objects.equals(value, entry.getValue())) {
                return entry.getKey();
            }
        }
        return null;
    }
}
