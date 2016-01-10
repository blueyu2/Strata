package com.blueyu2.strata;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

import java.io.File;

/**
 * Created by blueyu2 on 1/9/16.
 */
@Mod(modid = StrataMod.MODID, version = StrataMod.VERSION)
public class StrataMod {
    public static final String MODID = "strata";
    public static final String VERSION = "1.8.9-1.0";

    @SidedProxy(serverSide = "com.blueyu2.strata.Proxy", clientSide = "com.blueyu2.strata.ProxyClient")
    public static Proxy proxy;

    public static BlockStrata block;

    private File config;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event){
        config = event.getSuggestedConfigurationFile();
        StrataConfig.configDir = new File(config.getParentFile(), "Strata");
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event){
        StrataConfig.instance.loadConfig(config);
    }
}
