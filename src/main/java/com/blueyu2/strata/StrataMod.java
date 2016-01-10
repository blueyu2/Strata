package com.blueyu2.strata;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;

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
}
