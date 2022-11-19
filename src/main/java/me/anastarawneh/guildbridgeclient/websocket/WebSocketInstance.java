package me.anastarawneh.guildbridgeclient.websocket;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.realmsclient.gui.ChatFormatting;
import me.anastarawneh.guildbridgeclient.GuildBridgeClient;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ChatComponentText;
import net.minecraftforge.common.ForgeHooks;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;

public class WebSocketInstance extends WebSocketClient {

    public WebSocketInstance(URI serverUri) {
        super(serverUri);
    }

    @Override
    public void onOpen(ServerHandshake handshakedata) {

    }

    @Override
    public void onMessage(String message) {
        JsonObject json = new JsonParser().parse(message).getAsJsonObject();
        switch (json.get("status").getAsInt()) {
            case 0:
                // Send data
                send("{\"uuid\": \"" + Minecraft.getMinecraft().getSession().getProfile().getId() + "\", \"version\": \"" + GuildBridgeClient.VERSION + "\"}");
                break;
            case 1:
                // Accepted
                break;
            case 2:
                // User message incoming
                String username = json.getAsJsonObject("data").get("username").getAsString();
                String content = json.getAsJsonObject("data").get("message").getAsString()
                        .replaceAll("<gbcm>", String.valueOf(ChatFormatting.AQUA))
                        .replaceAll("</gbcm>", String.valueOf(ChatFormatting.RESET))
                        .replaceAll("<gbcrm>", String.valueOf(ChatFormatting.AQUA))
                        .replaceAll("</gbcrm>", String.valueOf(ChatFormatting.RESET))
                        .replaceAll("<gbccm>", String.valueOf(ChatFormatting.AQUA))
                        .replaceAll("</gbccm>", String.valueOf(ChatFormatting.RESET));
                Minecraft.getMinecraft().thePlayer.addChatMessage((ForgeHooks.newChatWithLinks(
                        ChatFormatting.BLUE + "Discord > " + ChatFormatting.GOLD + username + ChatFormatting.RESET + ": " + content
                )));
                break;
            default:
                GuildBridgeClient.LOGGER.error("Received bad status code.");
                break;
        }
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {

    }

    @Override
    public void onError(Exception ex) {

    }
}
