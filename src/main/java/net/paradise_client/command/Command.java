package net.paradise_client.command;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.builder.*;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.*;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.command.CommandSource;
import net.paradise_client.Helper;

import java.util.concurrent.CompletableFuture;

/**
 * Abstract class representing a command in the Paradise Client Fabric mod. This class provides basic functionality for
 * creating commands and managing their properties.
 */
public abstract class Command {
  protected static final int SINGLE_SUCCESS = com.mojang.brigadier.Command.SINGLE_SUCCESS;

  private final String name;
  private final String description;
  private final boolean async;

  /**
   * Constructor for the Command class.
   *
   * @param name        The name of the command.
   * @param description The description of the command.
   */
  public Command(String name, String description) {
    this(name, description, false);
  }

  /**
   * Constructor for the Command class.
   *
   * @param name        The name of the command.
   * @param description The description of the command.
   * @param async       Whether the command should be run inside a background thread
   */
  public Command(String name, String description, boolean async) {
    this.name = name;
    this.description = description;
    this.async = async;
  }

  /**
   * Protected method to create a literal argument builder for Brigadier.
   *
   * @param name The name of the literal argument.
   *
   * @return A Brigadier literal argument builder.
   */
  protected static LiteralArgumentBuilder<CommandSource> literal(final String name) {
    return LiteralArgumentBuilder.literal(name);
  }

  /**
   * Protected method to create an argument builder for Brigadier.
   *
   * @param name The name of the argument.
   * @param type The type of the argument.
   *
   * @return A Brigadier literal argument builder.
   */
  protected static <T> RequiredArgumentBuilder<CommandSource, T> argument(final String name,
    final ArgumentType<T> type) {
    return RequiredArgumentBuilder.argument(name, type);
  }

  /**
   * Abstract method to build the command using Brigadier's argument builder.
   */
  abstract public void build(LiteralArgumentBuilder<CommandSource> root);

  public CompletableFuture<Suggestions> suggestOnlinePlayers(CommandContext<?> ctx, SuggestionsBuilder builder) {
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
  }

  /**
   * Getter for the Minecraft client instance.
   *
   * @return The Minecraft client instance.
   */
  public MinecraftClient getMinecraftClient() {
    return MinecraftClient.getInstance();
  }

  public int incompleteCommand(CommandContext<?> context) {
    Helper.printChatMessage("Incomplete command!");
    return 1;
  }

  /**
   * Getter for the command name.
   *
   * @return The name of the command.
   */
  public String getName() {
    return name;
  }

  /**
   * Getter for the command description.
   *
   * @return The description of the command.
   */
  public String getDescription() {
    return description;
  }

  /**
   * Getter for whether command is async.
   *
   * @return Whether command is async.
   */
  public boolean isAsync() {
    return async;
  }

}
