package com.blueyu2.strata;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.IResource;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.data.AnimationMetadataSection;
import net.minecraft.util.ResourceLocation;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.List;

/**
 * Created by blueyu2 on 1/4/16.
 */
@SideOnly(Side.CLIENT)
public class StrataTexture extends TextureAtlasSprite {
    public StrataBlock.Type type;
    public int depth;
    String oreName;
    String stoneName;

    public double depthMult = 0.7d;

    public BufferedImage outputImage = null;

    public StrataTexture(String oreName, String baseName, int depth, StrataBlock.Type type) {
        super(getDerivedName(oreName, depth));
        this.oreName = oreName;
        this.stoneName = baseName;
        this.type = type;
        this.depth = depth;
    }

    public static String getDerivedName(String textureName, int depth){
        String modName = "minecraft";

        int index = textureName.indexOf(':');

        if(index >= 0){
            if(index > 1){
                modName = textureName.substring(0, index);
            }
            textureName = textureName.substring(index + 1, textureName.length());
        }

        modName = modName.toLowerCase();

        return Strata.MODID + ":" + modName + "/" + textureName + "-" + depth;
    }

    public boolean hasCustomLoader(IResourceManager manager, ResourceLocation location) {
        ResourceLocation location1 = new ResourceLocation(location.getResourceDomain(), String.format("%s/%s%s", new Object[]{"textures/blocks", location.getResourcePath(), ".png"}));
        try {
            manager.getResource(location1);
            return false;
        } catch (IOException e) {
            return true;
        }
    }

    public boolean load(IResourceManager manager, ResourceLocation location){
        boolean result = true;

        String file = getDerivedName(oreName, depth);
        int index = file.indexOf(':');
        file = file.substring(index + 1);

        switch (type){
            case STONE:
                result = loadStone(manager, location);
                Strata.logger.info("Strata: Succesfully generated stone texture for '" + oreName + "'. Place " + file + ".png in the assets folder to override.");
                break;
            case ORE:
                result = loadOre(manager, location);
                Strata.logger.info("Strata: Succesfully generated ore texture for '" + oreName + "' with background '" + stoneName + "'. Place " + file + ".png in the assets folder to override.");
                break;
        }


        return result;
    }

    public boolean loadStone(IResourceManager manager, ResourceLocation location){
        int mip = Minecraft.getMinecraft().gameSettings.mipmapLevels;

        BufferedImage[] stoneImage = new BufferedImage[1 + mip];

        AnimationMetadataSection animation;

        try{
            IResource iResource = manager.getResource(getBlockResource(stoneName));
            stoneImage[0] = ImageIO.read(iResource.getInputStream());
            animation = (AnimationMetadataSection) iResource.getMetadata("animation");
        } catch (IOException e){
            e.printStackTrace();
            return true;
        }

        stoneImage[0] = generateStone(stoneImage[0]);

        outputImage = stoneImage[0];

        this.loadSprite(stoneImage, animation, (float) Minecraft.getMinecraft().gameSettings.anisotropicFiltering > 1.0f);

        return false;
    }

    public BufferedImage generateStone(BufferedImage oldImage){
        BufferedImage output = new BufferedImage(oldImage.getWidth(), oldImage.getHeight(), 2);
        int color;
        int newColor;
        double darkness = depthMult - (((double) depth) / 5d);

        for (int x = 0; x < oldImage.getWidth(); x++){
            for(int y = 0; y < oldImage.getHeight(); y++){

                color = oldImage.getRGB(x, y);
                newColor = makeCol(Math.max((int)((double)getRed(color) * darkness), 0), Math.max((int)((double)getGreen(color) * darkness), 0), Math.max((int)((double)getBlue(color) * darkness), 0), getAlpha(color));
                output.setRGB(x, y, newColor);
            }
        }

        return output;
    }

    public boolean loadOre(IResourceManager manager, ResourceLocation location){
        int mip = Minecraft.getMinecraft().gameSettings.mipmapLevels;

        BufferedImage[] oreImage = new BufferedImage[1 + mip];

        BufferedImage stoneImage;
        int width;

        AnimationMetadataSection animation;

        try{
            IResource iOreResource = manager.getResource(getBlockResource(oreName));
            IResource iStoneResource = manager.getResource(getBlockResource(stoneName));

            oreImage[0] = ImageIO.read(iOreResource.getInputStream());
            stoneImage = ImageIO.read(iStoneResource.getInputStream());

            animation = (AnimationMetadataSection) iOreResource.getMetadata("animation");

            width = oreImage[0].getWidth();

            if(stoneImage.getWidth() != width){
                List resourcePacks = manager.getAllResources(getBlockResource(stoneName));
                for(int i = resourcePacks.size() - 1; i >= 0; --i){
                    IResource resource = (IResource) resourcePacks.get(i);
                    stoneImage = ImageIO.read(resource.getInputStream());

                    if(stoneImage.getWidth() == width){
                        break;
                    }
                }
            }
        }
        catch (IOException e){
            e.printStackTrace();
            return true;
        }

        if(stoneImage.getWidth() != width){
            return true;
        }

        oreImage[0] = generateOre(oreImage[0], stoneImage);

        outputImage = oreImage[0];

        this.loadSprite(oreImage, animation, (float) Minecraft.getMinecraft().gameSettings.anisotropicFiltering > 1.0f);

        return false;
    }

    public BufferedImage generateOre(BufferedImage oreImage, BufferedImage stoneImage){
        BufferedImage newImage = generateStone(oreImage);

        for(int x = 0; x < oreImage.getWidth(); x++){
            for(int y = 0; y < oreImage.getHeight(); y++){
                int sx = x % stoneImage.getWidth();
                int sy = y % stoneImage.getHeight();

                if(getAlpha(oreImage.getRGB(x, y)) == 0 || oreImage.getRGB(x, y) == stoneImage.getRGB(sx, sy))
                    continue;

                int r = Math.abs(getRed(oreImage.getRGB(x, y)) - getRed(stoneImage.getRGB(sx, sy)));
                int g = Math.abs(getGreen(oreImage.getRGB(x, y)) - getGreen(stoneImage.getRGB(sx, sy)));
                int b = Math.abs(getBlue(oreImage.getRGB(x, y)) - getBlue(stoneImage.getRGB(sx, sy)));



                if(r < 28 && g < 28 && b < 28)
                    continue;

                //Fix coal & other dark ores
                r = Math.abs(getRed(oreImage.getRGB(x, y)) - getRed(newImage.getRGB(x, y)));
                g = Math.abs(getGreen(oreImage.getRGB(x, y)) - getGreen(newImage.getRGB(x, y)));
                b = Math.abs(getBlue(oreImage.getRGB(x, y)) - getBlue(newImage.getRGB(x, y)));
                if(r < 36 && g < 36 && b < 36)
                    continue;

                newImage.setRGB(x, y, oreImage.getRGB(x, y));
            }
        }

        return newImage;
    }

    public static ResourceLocation getBlockResource(String blockName){
        String modName = "minecraft";

        int index = blockName.indexOf(':');

        if (index >= 0){
            if(index > 1){
                modName = blockName.substring(0, index);
            }
            blockName = blockName.substring(index + 1, blockName.length());
        }

        modName = modName.toLowerCase();
        blockName = "textures/blocks/" + blockName + ".png";

        return new ResourceLocation(modName, blockName);
    }

    public static int getAlpha(int col) {
        return (col & 0xff000000) >> 24;
    }

    public static int getRed(int col) {
        return (col & 0x00ff0000) >> 16;
    }

    public static int getGreen(int col) {
        return (col & 0x0000ff00) >> 8;
    }

    public static int getBlue(int col) {
        return col & 0x000000ff;
    }

    public static int makeCol(int red, int green, int blue, int alpha) {
        return (alpha << 24) | (red << 16) | (green << 8) | blue;
    }
}
