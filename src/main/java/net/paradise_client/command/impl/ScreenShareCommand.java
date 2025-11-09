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
public class ScreenShareCommand extends Command {

  /**
   * Constructs a new instance of {@link ScreenShareCommand}.
   */
  public ScreenShareCommand() {
    super("screenshare", "Toggles IP displayed on HUD", CommandManager.CommandCategory.UTILITY);
  }

  /**
   * Builds the command structure using Brigadier's {@link LiteralArgumentBuilder}.
   */
  @Override public void build(LiteralArgumentBuilder<CommandSource> root) {
    root.executes((context -> {
      ParadiseClient.HUD_MOD.showServerIP = !ParadiseClient.HUD_MOD.showServerIP;
      Helper.printChatMessage(ParadiseClient.HUD_MOD.showServerIP ? "Server IP shown" : "Server IP hidden");
      return SINGLE_SUCCESS;
    }));
  }
}
