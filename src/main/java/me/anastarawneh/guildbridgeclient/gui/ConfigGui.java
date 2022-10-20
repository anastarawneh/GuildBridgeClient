package me.anastarawneh.guildbridgeclient.gui;

import me.anastarawneh.guildbridgeclient.GuildBridgeClient;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.fml.client.config.GuiConfig;

public class ConfigGui extends GuiConfig {
    public ConfigGui(GuiScreen parentScreen) {
        super(parentScreen, new ConfigElement(GuildBridgeClient.CONFIG.getCategory("guildbridgeclient")).getChildElements(), GuildBridgeClient.MODID, "guildbridgeclient", false, false, "GuildBridgeClient Configuration", GuildBridgeClient.CONFIG.getConfigFile().getPath());
    }
}
