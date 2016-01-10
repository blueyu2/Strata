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
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * Created by blueyu2 on 1/4/16.
 */
@SideOnly(Side.CLIENT)
public class StrataTexture extends TextureAtlasSprite {
    public StrataBlock.Type type;
    public int depth;
    String oreName;
    String stoneName;
    BufferedImage override = null;
    boolean overrideStone = false;

    public double depthMult = 0.7d;

    public BufferedImage outputImage = null;

    public StrataTexture(String oreName, String baseName, int depth, StrataBlock.Type type) {
        super(type == StrataBlock.Type.STONE ? getDerivedStoneName(oreName, depth) : getDerivedOreName(oreName, depth, baseName));
        this.oreName = oreName;
        this.stoneName = baseName;
        this.type = type;
        this.depth = depth;
    }

    public static String getDerivedStoneName(String oreName, int depth){
        String oreModName = "minecraft";

        int oreIndex = oreName.indexOf(':');

        if(oreIndex >= 0){
            if(oreIndex > 1){
                oreModName = oreName.substring(0, oreIndex);
            }
            oreName = oreName.substring(oreIndex + 1, oreName.length());
        }

        oreName = oreName.toLowerCase();

        return Strata.MODID + ":" + oreModName + "/" + oreName + "." + depth;
    }

    public static String getDerivedOreName(String oreName, int depth, String baseName){
        String baseModName = "minecraft";

        int baseIndex = baseName.indexOf(':');

        if(baseIndex >= 0){
            if(baseIndex > 1){
                baseModName = baseName.substring(0, baseIndex);
            }
            baseName = baseName.substring(baseIndex + 1, baseName.length());
        }

        baseName = baseName.toLowerCase();

        return getDerivedStoneName(oreName, depth) + "+" + baseModName + "|" + baseName;
    }

    public boolean hasCustomLoader(IResourceManager manager, ResourceLocation location) {
        ResourceLocation location1 = new ResourceLocation(location.getResourceDomain(), String.format("%s/%s%s", "textures/blocks", location.getResourcePath(), ".png"));
        try {
            manager.getResource(location1);
            return false;
        } catch (IOException e) {
            return true;
        }
    }

    public boolean load(IResourceManager manager, ResourceLocation location){
        boolean result = true;

        String file = null;
        switch (type){
            case STONE:
                file = getDerivedStoneName(stoneName, depth);
                break;
            case ORE:
                file = getDerivedOreName(oreName, depth, stoneName);
                break;
        }
        //String file = getDerivedName(oreName, stoneName, depth);
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

        AnimationMetadataSection oreAnimation;
        int oreAnimationMultiplier = 1;

        try{
            IResource iOverrideResource = manager.getResource(getBlockResource(getDerivedStoneName(stoneName, depth)));
            override = ImageIO.read(iOverrideResource.getInputStream());
            overrideStone = true;
        }
        catch (IOException e){
        }

        try{
            IResource iOreResource = manager.getResource(getBlockResource(oreName));
            IResource iStoneResource = manager.getResource(getBlockResource(stoneName));

            oreImage[0] = ImageIO.read(iOreResource.getInputStream());
            stoneImage = ImageIO.read(iStoneResource.getInputStream());

            //Just in case the stone image is animated. I'm not going to generate a texture from two animated textures
            stoneImage = stoneImage.getSubimage(0, 0, stoneImage.getWidth(), stoneImage.getWidth());

            oreAnimation = (AnimationMetadataSection) iOreResource.getMetadata("animation");
            if(oreAnimation != null)
                oreAnimationMultiplier = oreAnimation.getFrameCount();

            //If stone is smaller than ore, scale up stone texture
            if(stoneImage.getWidth() < oreImage[0].getWidth()){
                int oreWidth = oreImage[0].getWidth(), oreHeight = oreImage[0].getHeight();
                if(oreAnimation != null)
                    oreHeight = oreWidth;

                stoneImage = resize(stoneImage, oreWidth, oreHeight);
            }
            //Otherwise if ore is smaller, scale up ore
            else if(stoneImage.getWidth() > oreImage[0].getWidth()){
                int stoneWidth = stoneImage.getWidth(), stoneHeight = stoneImage.getHeight();

                oreImage[0] = resize(oreImage[0], stoneWidth, stoneHeight * oreAnimationMultiplier);
            }
        }
        catch (IOException e){
            e.printStackTrace();
            return true;
        }

        oreImage[0] = generateOre(oreImage[0], oreAnimationMultiplier, stoneImage);

        outputImage = oreImage[0];

        this.loadSprite(oreImage, oreAnimation, (float) Minecraft.getMinecraft().gameSettings.anisotropicFiltering > 1.0f);

        return false;
    }

    //Help from http://stackoverflow.com/a/9417836
    public static BufferedImage resize(BufferedImage img, int newW, int newH) {
        Image tmp = img.getScaledInstance(newW, newH, Image.SCALE_SMOOTH);
        BufferedImage dimg = new BufferedImage(newW, newH, BufferedImage.TYPE_INT_ARGB);

        Graphics2D g2d = dimg.createGraphics();
        g2d.drawImage(tmp, 0, 0, null);
        g2d.dispose();

        return dimg;
    }

    public BufferedImage generateAnimatedTexture(BufferedImage image, int multiplier){
        BufferedImage temp = new BufferedImage(image.getWidth(), image.getHeight() * multiplier, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = temp.createGraphics();
        for(int i = 0; i < multiplier; i++){
            g2d.drawImage(image, 0, image.getHeight() * i, null);
        }
        g2d.dispose();
        return temp;
    }

    public BufferedImage generateOre(BufferedImage oreImage, int oreAnimation, BufferedImage stoneImage){
        BufferedImage coalFix = generateStone(oreImage);
        BufferedImage newImage;
        if(!overrideStone){
            newImage = generateStone(generateAnimatedTexture(stoneImage, oreAnimation));
        }
        else {
            newImage = generateAnimatedTexture(override, oreAnimation);
        }

        for(int x = 0; x < oreImage.getWidth(); x++){
            for(int y = 0; y < oreImage.getHeight(); y++){
                int sx = x % stoneImage.getWidth();
                int sy = y % stoneImage.getHeight();

                if(getAlpha(oreImage.getRGB(x, y)) == 0 ||oreImage.getRGB(x, y) == stoneImage.getRGB(sx, sy))
                    continue;

                int r, g, b;

                //Makes coal ore and emerald ore look better
                if(override == null && (oreName.equals("coal_ore") || oreName.equals("emerald_ore"))){
                    r = Math.abs(getRed(oreImage.getRGB(x, y)) - getRed(newImage.getRGB(x, y)));
                    g = Math.abs(getGreen(oreImage.getRGB(x, y)) - getGreen(newImage.getRGB(x, y)));
                    b = Math.abs(getBlue(oreImage.getRGB(x, y)) - getBlue(newImage.getRGB(x, y)));
                    if(r < 49 && g < 49 && b < 49){
                        newImage.setRGB(x, y, coalFix.getRGB(x, y));
                        continue;
                    }
                }

                r = Math.abs(getRed(oreImage.getRGB(x, y)) - getRed(stoneImage.getRGB(sx, sy)));
                g = Math.abs(getGreen(oreImage.getRGB(x, y)) - getGreen(stoneImage.getRGB(sx, sy)));
                b = Math.abs(getBlue(oreImage.getRGB(x, y)) - getBlue(stoneImage.getRGB(sx, sy)));
                if(r < 28 && g < 28 && b < 28)
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
