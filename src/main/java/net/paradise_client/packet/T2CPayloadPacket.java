package net.paradise_client.packet;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.network.packet.CustomPayload.Id;
import net.minecraft.util.Identifier;
import net.paradise_client.Helper;

public final class T2CPayloadPacket implements CustomPayload {
    private final String command;

    // ✅ FIXED: use Identifier.of() to avoid private constructor issue
    public static final Id<T2CPayloadPacket> ID = new Id<>(Identifier.of("t2c", "bcmd"));

    public T2CPayloadPacket(String command) {
        this.command = command;
    }

    public T2CPayloadPacket(PacketByteBuf buf) {
        buf.readString(); // skip label or tag
        this.command = buf.readString(); // get actual command
    }

    public String command() {
        return command;
    }

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }

    @Override
    public void write(PacketByteBuf buf) {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("T2Code-Console"); // plugin channel/tag
        out.writeUTF(command);          // actual command

        buf.writeByteArray(out.toByteArray());
        Helper.printChatMessage("§aPayload serialized!");
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof T2CPayloadPacket other)) return false;
        return this.command.equals(other.command);
    }

    @Override
    public int hashCode() {
        return command.hashCode();
    }

    @Override
    public String toString() {
        return "T2CPayloadPacket[command=" + command + "]";
    }
}
