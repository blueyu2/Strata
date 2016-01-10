package com.blueyu2.strata;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.creativetab.CreativeTabs;

/**
 * Created by blueyu2 on 1/10/16.
 */
public class BlockStrata extends Block {
    public static int maxMetadata;
    public PropertyInteger METADATA;
    public IBakedModel[] models;
    public IBakedModel[] invModels;



    public BlockStrata() {
        super(Material.rock, Material.rock.getMaterialMapColor());
        this.setCreativeTab(CreativeTabs.tabBlock);
    }
}
