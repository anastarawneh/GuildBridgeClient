package me.anastarawneh.guildbridgeclient.discord;

import me.anastarawneh.guildbridgeclient.GuildBridgeClient;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class WebhookClient {
    private static final ExecutorService threadPool = Executors.newFixedThreadPool(1);
    private static DiscordWebhook webhook;

    public static void sendMessage(String username, String message) {
        String url = GuildBridgeClient.CONFIG.getCategory("guildbridgeclient").get("webhook_url").getString();
        webhook = new DiscordWebhook(url);
        webhook.setAvatarUrl("https://minotar.net/helm/" + username);
        webhook.setUsername(username);
        webhook.setContent(message);
        threadPool.submit(() -> {
            try {
                webhook.execute();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }
}
