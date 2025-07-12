package net.paradise_client.packet;

import net.paradise_client.Helper;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import net.minecraft.network.packet.CustomPayload;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

public class BungeeCommandPacket implements CustomPayload {

    // ✅ Use Identifier.of() for 1.20.5+ compatibility
    public static final CustomPayload.Id<BungeeCommandPacket> ID =
            new CustomPayload.Id<>(Identifier.of("atlas:out"));

    private final String command;

    public BungeeCommandPacket(String command) {
        this.command = command;
    }

    public BungeeCommandPacket(PacketByteBuf buf) {
        this.command = buf.readString();
    }

    public String getCommand() {
        return command;
    }

    public void write(PacketByteBuf buf) {
        try {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            ObjectOutputStream oStream = new ObjectOutputStream(stream);

            // Writing command tag and actual command string
            oStream.writeUTF("commandBungee");
            oStream.writeObject(this.command);

            // Write the full serialized payload
            buf.writeBytes(stream.toByteArray());
        } catch (IOException e) {
            Helper.printChatMessage("Error writing BungeeCommandPacket: " + e.getMessage());
        }
    }

    @Override
    public CustomPayload.Id<? extends CustomPayload> getId() {
        return ID;
    }
}
