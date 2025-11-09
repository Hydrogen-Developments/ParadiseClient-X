package net.paradise_client.command.impl;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.command.CommandSource;
import net.paradise_client.command.Command;
import net.paradise_client.command.CommandManager;

import java.util.Objects;

public class GriefCommand extends Command {

  /**
   * Constructs a new instance of {@link GriefCommand}.
   */
  public GriefCommand() {
    super("grief", "Multiple grief commands", CommandManager.CommandCategory.UTILITY);
  }

  /**
   * Builds the command structure using Brigadier's {@link LiteralArgumentBuilder}.
   */
  @Override public void build(LiteralArgumentBuilder<CommandSource> root) {
    root.then(literal("tpall").executes((context) -> {
      ClientPlayNetworkHandler handler = MinecraftClient.getInstance().getNetworkHandler();
      handler.sendChatCommand("tpall");
      handler.sendChatCommand("etpall");
      handler.sendChatCommand("minecraft:tp @a @p");
      handler.sendChatCommand("tp @a @p");
      return SINGLE_SUCCESS;
    })).then(literal("fill").then(literal("air").executes((context) -> {
      Objects.requireNonNull(getMinecraftClient().getNetworkHandler())
        .sendChatCommand("minecraft:fill ~12 ~12 ~12 ~-12 ~-12 ~-12 air");
      return SINGLE_SUCCESS;
    })).then(literal("lava").executes((context) -> {
      Objects.requireNonNull(getMinecraftClient().getNetworkHandler())
        .sendChatCommand("minecraft:fill ~12 ~12 ~12 ~-12 ~-12 ~-12 lava");
      return SINGLE_SUCCESS;
    })).executes(this::incompleteCommand)).then(literal("sphere").then(literal("air").executes((context) -> {
      Objects.requireNonNull(getMinecraftClient().getNetworkHandler()).sendChatCommand("/sphere air 10");
      return SINGLE_SUCCESS;
    })).then(literal("lava").executes((context) -> {
      Objects.requireNonNull(getMinecraftClient().getNetworkHandler()).sendChatCommand("/sphere lava 10");
      return SINGLE_SUCCESS;
    })).executes(this::incompleteCommand)).executes(this::incompleteCommand);
  }
}
