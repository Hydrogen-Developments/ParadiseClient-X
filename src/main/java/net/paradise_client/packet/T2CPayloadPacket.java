package net.paradise_client.packet;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import net.paradise_client.Helper;

public final class T2CPayloadPacket implements CustomPayload {
    private final String command;

    // ✅ Use Identifier directly instead of CustomPayload.Id
    public static final Identifier ID = new Identifier("t2c", "bcmd");

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

    // ✅ Return the raw Identifier, don't use CustomPayload.Id
    public Identifier getId() {
        return ID;
    }

    // ❌ Don't use @Override to avoid mapping conflict
    public void write(PacketByteBuf buf) {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("T2Code-Console");
        out.writeUTF(command);
        buf.writeByteArray(out.toByteArray());
        Helper.printChatMessage("§aPayload serialized!");
    }

    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof T2CPayloadPacket other)) return false;
        return this.command.equals(other.command);
    }

    public int hashCode() {
        return command.hashCode();
    }

    public String toString() {
        return "T2CPayloadPacket[command=" + command + "]";
    }
}
