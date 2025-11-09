package net.paradise_client.command.impl;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.command.CommandSource;
import net.paradise_client.*;
import net.paradise_client.command.Command;
import net.paradise_client.command.CommandManager;

/**
 * Represents a command that toggles the display of server IP on the HUD.
 *
 * @author SpigotRCE
 * @since 1.4
 */
public class ToggleTABCommand extends Command {

  /**
   * Constructs a new instance of {@link ToggleTABCommand}.
   */
  public ToggleTABCommand() {
    super("toggletab", "Toggles IP displayed on HUD", CommandManager.CommandCategory.UTILITY);
  }

  /**
   * Builds the command structure using Brigadier's {@link LiteralArgumentBuilder}.
   */
  @Override public void build(LiteralArgumentBuilder<CommandSource> root) {
    root.executes((context -> {
      ParadiseClient.HUD_MOD.showPlayerList = !ParadiseClient.HUD_MOD.showPlayerList;
      Helper.printChatMessage(ParadiseClient.HUD_MOD.showPlayerList ? "TAB shown" : "TAB hidden");
      return SINGLE_SUCCESS;
    }));
  }
}
