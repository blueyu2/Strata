package com.blueyu2.strata;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * Created by blueyu2 on 1/7/16.
 */
@SideOnly(Side.CLIENT)
public class CommandClientOutputTextures extends CommandBase {
    @Override
    public String getCommandName() {
        return "strata_outputtextures";
    }

    @Override
    public boolean canCommandSenderUseCommand(ICommandSender par1ICommandSender) {
        return true;
    }

    @Override
    public String getCommandUsage(ICommandSender p_71518_1_) {
        return "strata.command.help";
    }

    @Override
    public void processCommand(ICommandSender p_71515_1_, String[] p_71515_2_) {
        for(Block block : StrataRegistry.blocks.values()){
            if(block instanceof StrataBlock){
                StrataBlock sBlock = (StrataBlock) block;
                for(int i = 0; i < sBlock.maxDepth; i++){
                    if(sBlock.icons[i] instanceof StrataTexture){
                        StrataTexture texture = (StrataTexture) sBlock.icons[i];

                        BufferedImage image = texture.outputImage;

                        if(image == null)
                            continue;

                        String file = null;
                        switch (sBlock.type){
                            case STONE:
                                file = StrataTexture.getDerivedStoneName(texture.stoneName, texture.depth);
                                break;
                            case ORE:
                                file = StrataTexture.getDerivedOreName(texture.oreName, texture.depth, texture.stoneName);
                                break;

                        }
                        int index = file.indexOf(':');
                        file = file.substring(index + 1);

                        File dir = new File(Minecraft.getMinecraft().mcDataDir, "stratatextures");
                        File moddir = new File(new File(new File(new File(dir, "assets"), "strata"), "textures"), "blocks");
                        File f = new File(moddir, file + ".png");

                        try {
                            if(!f.getParentFile().exists() && !f.getParentFile().mkdirs())
                                return;
                            if(!f.exists() && !f.createNewFile())
                                continue;

                            ImageIO.write(image, "png", f);
                            Strata.logger.info("Successful output of " + texture.getIconName());
                        }
                        catch (IOException e){
                            Strata.logger.info("Unable to output " + texture.getIconName());
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }
}
