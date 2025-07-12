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

    public static final Id<T2CPayloadPacket> ID = new Id<>(Identifier.of("t2c", "bcmd"));

    public T2CPayloadPacket(String command) {
        this.command = command;
    }

    public T2CPayloadPacket(PacketByteBuf buf) {
        buf.readString(); // skip label/tag
        this.command = buf.readString(); // actual command
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
        out.writeUTF("T2Code-Console");
        out.writeUTF(command);
        buf.writeByteArray(out.toByteArray());
        Helper.printChatMessage("§aPayload serialized!");
    }

    // No  (not declared in CustomPayload)
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof T2CPayloadPacket other)) return false;
        return this.command.equals(other.command);
    }

    // No (not declared in CustomPayload)
    public int hashCode() {
        return command.hashCode();
    }

    // No (not declared in CustomPayload)
    public String toString() {
        return "T2CPayloadPacket[command=" + command + "]";
    }
}
