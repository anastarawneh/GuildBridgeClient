package me.anastarawneh.guildbridgeclient.command;

import me.anastarawneh.guildbridgeclient.GuildBridgeClient;
import me.anastarawneh.guildbridgeclient.websocket.WebSocketService;
import me.anastarawneh.guildbridgeclient.websocket.WebSocketInstance;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatComponentText;

import java.net.URI;

public class WSReconnectCommand extends CommandBase {
    @Override
    public String getCommandName() {
        return "wsreconnect";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/wsreconnect";
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        if (!GuildBridgeClient.CONFIG.getCategory("guildbridgeclient").get("enabled").getBoolean()) return;
        if (WebSocketService.WS_CONNECTED) {
            WebSocketService.ws.close();
        }
        try {
            URI uri = URI.create(GuildBridgeClient.CONFIG.getCategory("guildbridgeclient").get("websocket_url").getString());
            WebSocketService.ws = new WebSocketInstance(uri);
            WebSocketService.ws.connect();
            sender.addChatMessage(new ChatComponentText(
                    GuildBridgeClient.MSG_PREFIX + " Attempting a reconnect to the WebSocket."
            ));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 0;
    }
}
