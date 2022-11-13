package me.anastarawneh.guildbridgeclient.websocket;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.realmsclient.gui.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ChatComponentText;
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
        String username = json.get("username").getAsString();
        String content = json.get("message").getAsString()
                .replaceAll("<gbcm>", String.valueOf(ChatFormatting.AQUA))
                .replaceAll("</gbcm>", String.valueOf(ChatFormatting.RESET));
        Minecraft.getMinecraft().thePlayer.addChatMessage((new ChatComponentText(
                ChatFormatting.BLUE + "Discord > " + ChatFormatting.GOLD + username + ChatFormatting.RESET + ": " + content
        )));
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {

    }

    @Override
    public void onError(Exception ex) {

    }
}
