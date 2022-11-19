package me.anastarawneh.guildbridgeclient.discord;

import me.anastarawneh.guildbridgeclient.GuildBridgeClient;

import java.io.IOException;

public class WebhookClient {
    private static DiscordWebhook webhook;

    public static void sendMessage(String username, String message) {
        String url = GuildBridgeClient.CONFIG.getCategory("guildbridgeclient").get("webhook_url").getString();
        webhook = new DiscordWebhook(url);
        webhook.setAvatarUrl("https://minotar.net/helm/" + username);
        webhook.setUsername(username);
        webhook.setContent(message);
        new Thread(() -> {
            try {
                webhook.execute();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }).start();
    }
}
