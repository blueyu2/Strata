package com.blueyu2.strata;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraftforge.client.ClientCommandHandler;

/**
 * Created by blueyu2 on 1/7/16.
 */
@SideOnly(Side.CLIENT)
public class ProxyClient extends Proxy {
    @Override
    public void postInit(){
        ClientCommandHandler.instance.registerCommand(new CommandClientOutputTextures());
    }
}
