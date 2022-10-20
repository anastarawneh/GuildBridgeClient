package me.anastarawneh.guildbridgeclient.command;

import com.mojang.realmsclient.gui.ChatFormatting;
import me.anastarawneh.guildbridgeclient.GuildBridgeClient;
import me.anastarawneh.guildbridgeclient.websocket.WebSocketClient;
import me.anastarawneh.guildbridgeclient.websocket.WebSocketService;
import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatComponentText;

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
    public void processCommand(ICommandSender sender, String[] args) throws CommandException {
        if (!GuildBridgeClient.CONFIG.getCategory("guildbridgeclient").get("enabled").getBoolean()) return;
        if (WebSocketService.WS_CONNECTED) WebSocketService.ws.disconnect();
        try {
            WebSocketService.ws = WebSocketClient.connect();
            Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText(
                    ChatFormatting.GRAY + "[" +
                            ChatFormatting.GREEN + "GuildBridgeClient" +
                            ChatFormatting.GRAY + "]" +
                            ChatFormatting.RESET + " Reconnected to the WebSocket."
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
