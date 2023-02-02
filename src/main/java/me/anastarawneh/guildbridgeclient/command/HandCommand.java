package me.anastarawneh.guildbridgeclient.command;

import me.anastarawneh.guildbridgeclient.GuildBridgeClient;
import me.anastarawneh.guildbridgeclient.discord.DiscordWebhook;
import me.anastarawneh.guildbridgeclient.discord.WebhookClient;
import me.anastarawneh.guildbridgeclient.websocket.WebSocketService;
import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ChatComponentText;

import java.awt.*;

public class HandCommand extends CommandBase {
    @Override
    public String getCommandName() {
        return "hand";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/hand";
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        if (!GuildBridgeClient.CONFIG.getCategory("guildbridgeclient").get("enabled").getBoolean()) return;
        try {
            ItemStack hand = Minecraft.getMinecraft().thePlayer.inventory.getCurrentItem();
            String itemName = hand.getDisplayName();
            NBTTagCompound compound = new NBTTagCompound();
            hand.writeToNBT(compound);
            String NBT = compound.toString();
            String text = "{\"type\": \"ITEM_MESSAGE\", \"player_name\": \"" + GuildBridgeClient.USERNAME + "\", \"item_name\": \"" + itemName + "\", \"nbt\": \"" + NBT.replace('"', '\u00a9') + "\"}";
            WebSocketService.ws.send(text);
            NBTTagList loreList = hand.getTagCompound().getCompoundTag("display").getTagList("Lore", 8);
            int loreCount = loreList.tagCount();
            String loreString = "";
            for (int i = 0; i < loreCount; i++) {
                loreString += loreList.get(i).toString().replaceAll("\u00A7.", "").replaceAll("^\"|\"$", "") + "\\n";
            }
            Color embedColor;
            if (loreList.get(loreCount - 1).toString().contains("UNCOMMON")) embedColor = Color.decode("#55FF55");
            else if (loreList.get(loreCount - 1).toString().contains("COMMON")) embedColor = Color.decode("#FFFFFF");
            else if (loreList.get(loreCount - 1).toString().contains("RARE")) embedColor = Color.decode("#5555FF");
            else if (loreList.get(loreCount - 1).toString().contains("EPIC")) embedColor = Color.decode("#AA00AA");
            else if (loreList.get(loreCount - 1).toString().contains("LEGENDARY")) embedColor = Color.decode("#FFAA00");
            else if (loreList.get(loreCount - 1).toString().contains("MYTHIC")) embedColor = Color.decode("#FF55FF");
            else if (loreList.get(loreCount - 1).toString().contains("SPECIAL")) embedColor = Color.decode("#FF5555");
            else embedColor = Color.GRAY;
            DiscordWebhook.EmbedObject embed = new DiscordWebhook.EmbedObject()
                    .setTitle(itemName.replaceAll("\u00A7.", ""))
                    .setDescription(loreString)
                    .setColor(embedColor);
            WebhookClient.sendEmbed(Minecraft.getMinecraft().getSession().getUsername(), embed);
        } catch (NullPointerException ex) {
            sender.addChatMessage(new ChatComponentText(GuildBridgeClient.MSG_PREFIX + " You must select an item before running this command."));
        }
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 0;
    }
}
