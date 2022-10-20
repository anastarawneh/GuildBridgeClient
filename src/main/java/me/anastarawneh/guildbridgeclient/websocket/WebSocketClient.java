package me.anastarawneh.guildbridgeclient.websocket;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.realmsclient.gui.ChatFormatting;
import com.neovisionaries.ws.client.WebSocket;
import com.neovisionaries.ws.client.WebSocketAdapter;
import com.neovisionaries.ws.client.WebSocketFactory;
import me.anastarawneh.guildbridgeclient.GuildBridgeClient;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ChatComponentText;

public class WebSocketClient {
    public static WebSocket connect() throws Exception {
        return new WebSocketFactory()
                .setConnectionTimeout(5000)
                .createSocket(GuildBridgeClient.CONFIG.getCategory("guildbridgeclient").get("websocket_url").getString())
                .addListener(new WebSocketAdapter() {
                    @Override
                    public void onTextMessage(WebSocket websocket, String message) throws Exception {
                        JsonObject json = new JsonParser().parse(message).getAsJsonObject();
                        String username = json.get("username").getAsString();
                        String content = json.get("message").getAsString();
                        Minecraft.getMinecraft().thePlayer.addChatMessage((new ChatComponentText(
                                ChatFormatting.BLUE + "Discord > " + ChatFormatting.GOLD + username + ChatFormatting.RESET + ": " + content
                        )));
                    }
                })
                .connect();
    }
}
