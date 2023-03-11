package me.anastarawneh.guildbridgeclient.command;

import me.anastarawneh.guildbridgeclient.GuildBridgeClient;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatComponentText;

public class FarmCommand extends CommandBase {
    @Override
    public String getCommandName() {
        return "farm";
    }

    @Override
    public String getCommandUsage(ICommandSender iCommandSender) {
        return "/farm";
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) throws CommandException {
        int jumpCode = Minecraft.getMinecraft().gameSettings.keyBindJump.getKeyCode();
        int attackCode = Minecraft.getMinecraft().gameSettings.keyBindAttack.getKeyCode();
        Minecraft.getMinecraft().gameSettings.keyBindJump.setKeyCode(attackCode);
        Minecraft.getMinecraft().gameSettings.keyBindAttack.setKeyCode(jumpCode);
        Minecraft.getMinecraft().gameSettings.saveOptions();
        KeyBinding.resetKeyBindingArrayAndHash();

        sender.addChatMessage(new ChatComponentText(
                GuildBridgeClient.MSG_PREFIX + " Swapped jump and attack keybinds."
        ));
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 0;
    }
}
