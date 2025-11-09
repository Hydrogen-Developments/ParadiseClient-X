package net.paradise_client.command.impl;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.command.CommandSource;
import net.paradise_client.*;
import net.paradise_client.command.Command;
import net.paradise_client.command.CommandManager;
import java.util.Comparator;

public class HelpCommand extends Command {

  public HelpCommand() {
    super("help", "Shows help page", CommandManager.CommandCategory.UTILITY);
  }

  @Override
  public void build(LiteralArgumentBuilder<CommandSource> root) {
    root.executes(context -> {
      Helper.printChatMessage("&8&m-----------------------------------------------------", false);
      Helper.printChatMessage("&b&l[Command Categories]");
      Helper.printChatMessage("&7Use &a,help <command> &7for detailed info on a command.", false);

      for (CommandManager.CommandCategory category : CommandManager.CommandCategory.values()) {
        var cmds = ParadiseClient.COMMAND_MANAGER.getCommandsByCategory(category)
                .stream()
                .filter(cmd -> !cmd.getName().equalsIgnoreCase("help"))
                .sorted(Comparator.comparing(Command::getName))
                .toList();

        if (cmds.isEmpty()) continue;

        Helper.printChatMessage("&r");
        Helper.printChatMessage("&9&l" + category.getDisplayName() + " &8(&7" + cmds.size() + "&8)");
        cmds.forEach(cmd ->
                Helper.printChatMessage("&7 - &a" + cmd.getName() + " &8| &f" + cmd.getDescription())
        );
      }

      Helper.printChatMessage("&8&m-----------------------------------------------------", false);
      return SINGLE_SUCCESS;
    }).then(argument("command", StringArgumentType.word()).executes(context -> {
      String name = context.getArgument("command", String.class);
      Command command = ParadiseClient.COMMAND_MANAGER.getCommand(name);

      if (command == null) {
        Helper.printChatMessage("&4&l[Error] &cCommand not found: &f" + name);
        return SINGLE_SUCCESS;
      }

      Helper.printChatMessage("&8&m-----------------------------------------------------", false);
      Helper.printChatMessage("&b&l[Command Info]");
      Helper.printChatMessage("&7 - &aName: &f" + command.getName());
      Helper.printChatMessage("&7 - &aDescription: &f" + command.getDescription());
      Helper.printChatMessage("&7 - &aCategory: &f" + command.getCategory().getDisplayName());
      Helper.printChatMessage("&8&m-----------------------------------------------------", false);
      return SINGLE_SUCCESS;
    }).suggests((context, builder) -> {
      String partialName;
      try {
        partialName = context.getArgument("command", String.class);
      } catch (IllegalArgumentException e) {
        partialName = "";
      }

      String finalPartialName = partialName;
      ParadiseClient.COMMAND_MANAGER.getCommands()
              .stream()
              .map(Command::getName)
              .filter(cmd -> cmd.toLowerCase().startsWith(finalPartialName.toLowerCase()))
              .forEach(builder::suggest);

      return builder.buildFuture();
    }));
  }
}
