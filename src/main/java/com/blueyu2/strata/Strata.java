package com.blueyu2.strata;

import com.blueyu2.strata.support.UBC;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.client.Minecraft;
import net.minecraftforge.common.MinecraftForge;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;


/**
 * Created by blueyu2 on 1/4/16.
 */
@Mod(modid = Strata.MODID, version = Strata.VERSION)
public class Strata {
    public static final String MODID = "strata";
    public static final String VERSION = "1.7.10-1.5.2";

    @SidedProxy(serverSide = "com.blueyu2.strata.Proxy", clientSide = "com.blueyu2.strata.ProxyClient")
    public static Proxy proxy;

    File config;

    public static Logger logger = LogManager.getLogger(Strata.MODID);

    @EventHandler
    public void preInit(FMLPreInitializationEvent event){
        config = event.getSuggestedConfigurationFile();
        StrataConfig.configDir = new File(config.getParentFile(), "Strata");
    }

    @EventHandler
    public void init(FMLInitializationEvent event){
        StrataConfig.instance.loadConfig(config);
        MinecraftForge.EVENT_BUS.register(new ChunkReplacer());
        if(Loader.isModLoaded("UndergroundBiomes"))
            UBC.load();
    }

    @EventHandler
    public void postInit(FMLPostInitializationEvent event){
        proxy.postInit();
    }
}
