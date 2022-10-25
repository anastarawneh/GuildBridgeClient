package me.anastarawneh.guildbridgeclient.websocket;

import me.anastarawneh.guildbridgeclient.GuildBridgeClient;
import me.anastarawneh.guildbridgeclient.discord.WebhookClient;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;
import org.java_websocket.client.WebSocketClient;

import java.net.URI;

public class WebSocketService {
    public static WebSocketClient ws;
    public static boolean WS_CONNECTED = false;

    @SubscribeEvent
    public void onJoinServer(FMLNetworkEvent.ClientConnectedToServerEvent event) {
        if (!GuildBridgeClient.CONFIG.getCategory("guildbridgeclient").get("enabled").getBoolean()) return;
        if (WS_CONNECTED) return;
        Minecraft client = FMLClientHandler.instance().getClient();
        if (client.isSingleplayer()) return;
        if (client.getCurrentServerData().serverIP.contains("hypixel.net")) {
            try {
                URI uri = URI.create(GuildBridgeClient.CONFIG.getCategory("guildbridgeclient").get("websocket_url").getString());
                ws = new WebSocketInstance(uri);
                ws.connect();
                WS_CONNECTED = true;
                GuildBridgeClient.LOGGER.info("WebSocket connected");
            } catch (Exception e) {
                WS_CONNECTED = false;
                throw new RuntimeException(e);
            }

            String username = Minecraft.getMinecraft().getSession().getUsername();
            WebhookClient.sendMessage(username, username + " joined.");
        }
    }

    @SubscribeEvent
    public void onLeaveServer(FMLNetworkEvent.ClientDisconnectionFromServerEvent event) {
        if (!GuildBridgeClient.CONFIG.getCategory("guildbridgeclient").get("enabled").getBoolean()) return;
        if (WS_CONNECTED) {
            String username = Minecraft.getMinecraft().getSession().getUsername();
            WebhookClient.sendMessage(username, username + " left.");
            ws.close();
            WS_CONNECTED = false;
            GuildBridgeClient.LOGGER.info("WebSocket disconnected");
        }
    }
}
