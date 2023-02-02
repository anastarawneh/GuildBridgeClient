package me.anastarawneh.guildbridgeclient;

import com.mojang.realmsclient.gui.ChatFormatting;
import me.anastarawneh.guildbridgeclient.command.HandCommand;
import me.anastarawneh.guildbridgeclient.command.WSReconnectCommand;
import me.anastarawneh.guildbridgeclient.event.MinecraftMessageReceived;
import me.anastarawneh.guildbridgeclient.websocket.WebSocketService;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(modid = GuildBridgeClient.MODID, version = GuildBridgeClient.VERSION, guiFactory = "me.anastarawneh.guildbridgeclient.gui.ConfigGuiFactory")
public class GuildBridgeClient {
    public static final String MODID = "guildbridgeclient";
    public static final String VERSION = "@VERSION@";
    public static final Logger LOGGER = LogManager.getLogger(MODID);
    public static Configuration CONFIG;
    public static final String MSG_PREFIX = ChatFormatting.GRAY + "[" + ChatFormatting.GREEN + "GuildBridgeClient" + ChatFormatting.GRAY + "]" + ChatFormatting.RESET;
    public static String USERNAME = "";

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        Configuration config = new Configuration(event.getSuggestedConfigurationFile());
        config.load();
        config.get("guildbridgeclient", "enabled", true, "Enables/Disables the mod.").setLanguageKey("guildbridgeclient.config.enabled");
        config.get("guildbridgeclient", "webhook_url", "", "The Discord webhook URL to send chat messages to.").setLanguageKey("guildbridgeclient.config.webhook_url");
        config.get("guildbridgeclient", "websocket_url", "", "The WebSocket URL to receive chat messages from.").setLanguageKey("guildbridgeclient.config.websocket_url");
        if (config.hasChanged()) config.save();
        CONFIG = config;
    }

    @EventHandler
    public void init(FMLInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(this);
        MinecraftForge.EVENT_BUS.register(new MinecraftMessageReceived());
        MinecraftForge.EVENT_BUS.register(new WebSocketService());

        ClientCommandHandler.instance.registerCommand(new WSReconnectCommand());
        ClientCommandHandler.instance.registerCommand(new HandCommand());
    }

    @SubscribeEvent
    public void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event) {
        if (!event.modID.equals(MODID)) return;
        if (CONFIG.hasChanged()) CONFIG.save();
    }
}
