package me.anastarawneh.guildbridgeclient.websocket;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.realmsclient.gui.ChatFormatting;
import me.anastarawneh.guildbridgeclient.GuildBridgeClient;
import net.minecraft.client.Minecraft;
import net.minecraft.event.ClickEvent;
import net.minecraft.event.HoverEvent;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.IChatComponent;
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
        WebSocketService.WS_CONNECTED = true;
        if (WebSocketService.FIRST_CONNECT) {
            WebSocketService.FIRST_CONNECT = false;
            return;
        }
        Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText(
                GuildBridgeClient.MSG_PREFIX + " Reconnected to the WebSocket."
        ));
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
            case 3:
                // Event
                String event = json.getAsJsonObject("data").get("event").getAsString();
                Minecraft.getMinecraft().thePlayer.playSound("note.pling", 1, 0);
                if (event.equals("Traveling Zoo")) {
                    String legendary = json.getAsJsonObject("data").get("legendary").getAsString();
                    Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText(
                            GuildBridgeClient.MSG_PREFIX + " " + ChatFormatting.YELLOW + event + ChatFormatting.RESET
                                    + " starts now! The legendary pet is a " + ChatFormatting.GOLD + legendary + ChatFormatting.RESET + "."
                    ));
                    break;
                }
                String time = json.getAsJsonObject("data").get("time").getAsString();
                String warpName = json.getAsJsonObject("data").getAsJsonObject("warp").get("name").getAsString();
                String warpCommand = json.getAsJsonObject("data").getAsJsonObject("warp").get("command").getAsString();
                Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText(
                        GuildBridgeClient.MSG_PREFIX + " " + ChatFormatting.YELLOW + event + ChatFormatting.RESET
                                + " starts " + time + "! Click to warp to " + warpName + ". "
                ).appendSibling(new ChatComponentText(
                        "[" + ChatFormatting.DARK_GREEN + ChatFormatting.BOLD + "WARP" + ChatFormatting.RESET + "]"
                ).setChatStyle(new ChatStyle()
                        .setChatClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, warpCommand))
                        .setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ChatComponentText(
                                "Warp to " + warpName
                        ))))));
                break;
            case 4:
                // Item Message
                String playerName = json.getAsJsonObject("data").get("player_name").getAsString();
                String itemName = json.getAsJsonObject("data").get("item_name").getAsString();
                String nbt = json.getAsJsonObject("data").get("nbt").getAsString().replace('\u00a9', '"');
                IChatComponent component = new ChatComponentText("[").appendSibling(new ChatComponentText(itemName)).appendText("]");
                component.getChatStyle().setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_ITEM, new ChatComponentText(nbt)));
                Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText(ChatFormatting.DARK_GREEN + "Guild > ").appendSibling(new ChatComponentText(playerName)).appendSibling(new ChatComponentText(ChatFormatting.WHITE + ": ")).appendSibling(component));
                break;
            default:
                GuildBridgeClient.LOGGER.error("Unsupported status code (" + json.get("status").getAsInt() + ").");
                break;
        }
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        WebSocketService.WS_CONNECTED = false;
        if (code == 1006) {
            Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText(
                    GuildBridgeClient.MSG_PREFIX + " Disconnected from the WebSocket. Attempting to reconnect in 10 seconds."
            ));
            GuildBridgeClient.LOGGER.error("Disconnected from the WebSocket, returned code " + code);
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            new Thread(this::reconnect).start();
        }
        else if (code != 1000) {
            Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText(
                    GuildBridgeClient.MSG_PREFIX + " Could not connect to the WebSocket. Contact Anas for support."
            ));
            GuildBridgeClient.LOGGER.error("Could not connect to the WebSocket, returned code " + code);
        }
    }

    @Override
    public void onError(Exception ex) {

    }
}
