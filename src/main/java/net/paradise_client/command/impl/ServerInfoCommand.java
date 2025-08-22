package net.paradise_client.command.impl;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.command.CommandSource;
import net.paradise_client.command.Command;
import net.paradise_client.Helper;

/**
 * Command to display server information.
 */
public class ServerInfoCommand extends Command {

    public ServerInfoCommand() {
        super("serverinfo", "Shows information about the server");
    }

    @Override
    public void build(LiteralArgumentBuilder<CommandSource> root) {
        root.executes(context -> {
            if (getMinecraftClient().getServer() == null || getMinecraftClient().player == null || getMinecraftClient().getNetworkHandler() == null) {
                Helper.printChatMessage("&cYou must be in a world to use this command.");
                return SINGLE_SUCCESS;
            }
            Helper.printChatMessage("&8&m-----------------------------------------------------", false);
            Helper.printChatMessage("&b&l[Server Info]");
            Helper.printChatMessage("&7 - &aBrand: &f" + getMinecraftClient().getNetworkHandler().getBrand());
            Helper.printChatMessage("&8&m-----------------------------------------------------", false);
            return SINGLE_SUCCESS;
        });
    }
}
