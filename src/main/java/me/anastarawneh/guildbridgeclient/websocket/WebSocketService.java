package me.anastarawneh.guildbridgeclient.websocket;

import com.neovisionaries.ws.client.WebSocket;
import me.anastarawneh.guildbridgeclient.GuildBridgeClient;
import me.anastarawneh.guildbridgeclient.discord.WebhookClient;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;

public class WebSocketService {
    public static WebSocket ws;
    public static boolean WS_CONNECTED = false;

    @SubscribeEvent
    public void onJoinServer(FMLNetworkEvent.ClientConnectedToServerEvent event) {
        if (!GuildBridgeClient.CONFIG.getCategory("guildbridgeclient").get("enabled").getBoolean()) return;
        if (WS_CONNECTED) return;
        Minecraft client = FMLClientHandler.instance().getClient();
        if (client.isSingleplayer()) return;
        if (client.getCurrentServerData().serverIP.contains("hypixel.net")) {
            Thread thread = new Thread(() -> {
                try {
                    ws = WebSocketClient.connect();
                    WS_CONNECTED = true;
                    GuildBridgeClient.LOGGER.info("WebSocket connected");
                } catch (Exception e) {
                    WS_CONNECTED = false;
                    throw new RuntimeException(e);
                }
            });
            thread.start();
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
            ws.disconnect();
            WS_CONNECTED = false;
            GuildBridgeClient.LOGGER.info("WebSocket disconnected");
        }
    }
}
