package com.blueyu2.strata;

import net.minecraftforge.common.config.Configuration;

import java.io.File;

/**
 * Created by blueyu2 on 1/10/16.
 */
public class StrataConfig {
    public static final StrataConfig instance = new StrataConfig();
    public static final String CATEGORY_STONE = "stones.";
    public static final String CATEGORY_ORE = "ores.";
    public static File configDir = null;
    public static int maxDepth = 2;
    public static boolean uninstall = false;

    public void loadConfig(File file){
        Configuration baseConfig = new Configuration(file);

        baseConfig.load();
        uninstall = baseConfig.getBoolean("Uninstall", "Main", false, "Set this to true and go to all the areas you went to with Strata installed to replace all Strata blocks in the world with the original blocks. This allows for safe removal of Strata without your worlds getting ruined.");
        baseConfig.save();

        File vanillaFile = new File(configDir, "minecraft.cfg");
        Configuration vanillaConfig = new Configuration(vanillaFile);
        vanillaConfig.load();

    }
}
