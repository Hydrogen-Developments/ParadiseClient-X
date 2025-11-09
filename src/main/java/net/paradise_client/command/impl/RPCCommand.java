package net.paradise_client.command.impl;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.command.CommandSource;
import net.paradise_client.*;
import net.paradise_client.command.Command;
import net.paradise_client.command.CommandManager;

public class RPCCommand extends Command {
  public RPCCommand() {
    super("rpc", "Disable/Enable Discord Rich Presence", CommandManager.CommandCategory.UTILITY);
  }

  @Override public void build(LiteralArgumentBuilder<CommandSource> root) {
    root.executes(this::execute);
  }

  private int execute(CommandContext<CommandSource> ctx) {
    boolean enabled = !ParadiseClient.DISCORD_RPC_MANAGER.isEnabled();
    ParadiseClient.DISCORD_RPC_MANAGER.setEnabled(enabled);
    Helper.printChatMessage("Discord Rich Presence " + (enabled ? "enabled" : "disabled"));
    return SINGLE_SUCCESS;
  }
}
