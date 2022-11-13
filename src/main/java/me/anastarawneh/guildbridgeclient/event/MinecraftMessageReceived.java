package me.anastarawneh.guildbridgeclient.event;

import me.anastarawneh.guildbridgeclient.GuildBridgeClient;
import me.anastarawneh.guildbridgeclient.discord.WebhookClient;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MinecraftMessageReceived {
    @SubscribeEvent
    public void onChatMessage(ClientChatReceivedEvent event) {
        if (!GuildBridgeClient.CONFIG.getCategory("guildbridgeclient").get("enabled").getBoolean()) return;
        if (Minecraft.getMinecraft().getCurrentServerData() == null) return;
        String serverIP = Minecraft.getMinecraft().getCurrentServerData().serverIP;
        if (!serverIP.contains("hypixel.net") || serverIP.contains("alpha.hypixel.net")) return;
        if (GuildBridgeClient.CONFIG.getCategory("guildbridgeclient").get("webhook_url").getString().isEmpty()) return;
        String message = event.message.getUnformattedText().replaceAll("\u00A7.", "");
        String regex = "^Guild > (\\[(?:VI|MV)P\\+{0,2}] )?([^ ]*)( \\[[\\w\\d]*])?: (.*)";
        Matcher matcher = Pattern.compile(regex).matcher(message);
        String username = Minecraft.getMinecraft().thePlayer.getName();
        if (matcher.matches() && matcher.group(2).equalsIgnoreCase(username)) {
            WebhookClient.sendMessage(username, matcher.group(4));
        }
    }
}
