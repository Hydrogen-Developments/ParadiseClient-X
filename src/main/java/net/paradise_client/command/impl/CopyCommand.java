package net.paradise_client.command.impl;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.command.CommandSource;
import net.paradise_client.Helper;
import net.paradise_client.command.Command;
import net.paradise_client.command.CommandManager;

import java.awt.*;
import java.awt.datatransfer.*;

/**
 * This class represents a command that copies a specific broadcast message to the clipboard. The command is part of the
 * ParadiseClientFabric mod for Minecraft.
 *
 * @author SpigotRCE
 * @version 1.0
 */
public class CopyCommand extends Command {

  /**
   * Constructs a new CopyCommand instance.
   */
  public CopyCommand() {
    super("copy", "Copies the broadcast of SpigotRCE", CommandManager.CommandCategory.MISC);
  }

  /**
   * Builds the command structure using Brigadier's LiteralArgumentBuilder. The command has two sub-commands: "tellraw"
   * and the default command.
   */
  @Override public void build(LiteralArgumentBuilder<CommandSource> root) {
    root.then(literal("tellraw").executes((context) -> {
      // Copies a specific tellraw message to the clipboard.
      StringSelection stringSelection = new StringSelection(
        "tellraw @a [{\"text\":\"Server hacked by\\n\", \"color\":\"green\"},{\"text\":\"https://youtube.com/@SpigotRCE\", \"color\":\"aqua\", \"bold\":true, \"clickEvent\":{\"action\":\"open_url\",\"value\":\"https://youtube.com/@SpigotRCE\"}}]");
      Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
      clipboard.setContents(stringSelection, null);
      Helper.printChatMessage("SpigotRCE's tellraw has been copied to your clipboard.");
      return SINGLE_SUCCESS;
    })).executes((context) -> {
      // Copies a specific formatted message to the clipboard.
      StringSelection stringSelection = new StringSelection("&aServer hacked by&b&l https://youtube.com/@SpigotRCE");
      Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
      clipboard.setContents(stringSelection, null);
      Helper.printChatMessage("SpigotRCE's broadcast has been copied to your clipboard.");
      return SINGLE_SUCCESS;
    });
  }
}
