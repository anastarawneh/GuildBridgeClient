package me.anastarawneh.guildbridgeclient.websocket;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.realmsclient.gui.ChatFormatting;
import me.anastarawneh.guildbridgeclient.GuildBridgeClient;
import me.anastarawneh.guildbridgeclient.discord.WebhookClient;
import net.minecraft.client.Minecraft;
import net.minecraft.event.ClickEvent;
import net.minecraft.event.HoverEvent;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatStyle;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;
import org.apache.commons.io.IOUtils;
import org.java_websocket.client.WebSocketClient;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;

public class WebSocketService {
    public static WebSocketClient ws;
    public static boolean WS_CONNECTED = false;
    private static boolean CHECK_VERSION = false;
    private static boolean CHECKED_VERSION = false;
    public static boolean FIRST_CONNECT = true;

    @SubscribeEvent
    public void onJoinServer(FMLNetworkEvent.ClientConnectedToServerEvent event) {
        if (!GuildBridgeClient.CONFIG.getCategory("guildbridgeclient").get("enabled").getBoolean()) return;
        if (WS_CONNECTED) return;
        Minecraft client = FMLClientHandler.instance().getClient();
        if (client.isSingleplayer()) return;
        String serverIP = Minecraft.getMinecraft().getCurrentServerData().serverIP;
        if (serverIP.contains("hypixel.net") && !serverIP.contains("alpha.hypixel.net")) {
            try {
                System.setProperty("java.net.preferIPv6Addresses", "false");
                System.setProperty("java.net.preferIPv4Stack", "true");

                URI uri = URI.create(GuildBridgeClient.CONFIG.getCategory("guildbridgeclient").get("websocket_url").getString());
                ws = new WebSocketInstance(uri);
                ws.connect();
                GuildBridgeClient.LOGGER.info("WebSocket connected");
            } catch (Exception e) {
                WS_CONNECTED = false;
                throw new RuntimeException(e);
            }

            String username = Minecraft.getMinecraft().getSession().getUsername();
            WebhookClient.sendMessage(username, username + " joined.");

            if (!GuildBridgeClient.USERNAME.equals("")) return;
            new Thread(() -> {
                while (true) {
                    try {
                        if (Minecraft.getMinecraft().thePlayer.getDisplayName().getUnformattedText().equals(Minecraft.getMinecraft().getSession().getUsername())) continue;
                        GuildBridgeClient.USERNAME = Minecraft.getMinecraft().thePlayer.getDisplayName().getUnformattedText().replaceFirst(" ..\\[.+]", "");
                        break;
                    } catch (NullPointerException ignored) {
                    }
                }
            }).start();
        }
        CHECK_VERSION = true;
    }

    @SubscribeEvent
    public void onTick(TickEvent.PlayerTickEvent event) {
        if (CHECK_VERSION && !CHECKED_VERSION) {
            CHECK_VERSION = false;
            new Thread(() -> {
                try {
                    InputStream stream = new URL("https://api.github.com/repos/anastarawneh/guildbridgeclient/releases/latest").openStream();
                    String content;
                    try {
                        content = IOUtils.toString(stream);
                    } finally {
                        IOUtils.closeQuietly(stream);
                    }
                    JsonObject json = new JsonParser().parse(content).getAsJsonObject();
                    String latestVersion = json.get("tag_name").getAsString().replaceFirst("v", "");
                    if (!latestVersion.equals(GuildBridgeClient.VERSION)) {
                        Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText(
                                ChatFormatting.GRAY + "[" +
                                        ChatFormatting.GREEN + "GuildBridgeClient" +
                                        ChatFormatting.GRAY + "]" +
                                        ChatFormatting.RESET + " GuildBridgeClient v" + latestVersion + " is available. Click this message to download.")
                                .setChatStyle(new ChatStyle()
                                        .setChatClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://github.com/anastarawneh/GuildBridgeClient/releases/latest"))
                                        .setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ChatComponentText("Latest GuildBridgeClient Releases")))));
                        Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText(
                                ChatFormatting.GRAY + "[" +
                                        ChatFormatting.GREEN + "GuildBridgeClient" +
                                        ChatFormatting.GRAY + "]" +
                                        ChatFormatting.RESET + " Current version: v" + GuildBridgeClient.VERSION + ". "));
                    }
                    CHECKED_VERSION = true;
                }
                catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }).start();
        }
    }

    @SubscribeEvent
    public void onLeaveServer(FMLNetworkEvent.ClientDisconnectionFromServerEvent event) {
        if (!GuildBridgeClient.CONFIG.getCategory("guildbridgeclient").get("enabled").getBoolean()) return;
        String username = Minecraft.getMinecraft().getSession().getUsername();
        WebhookClient.sendMessage(username, username + " left.");
        if (WS_CONNECTED) {
            ws.close();
            WS_CONNECTED = false;
            GuildBridgeClient.LOGGER.info("WebSocket disconnected");
        }
    }
}
