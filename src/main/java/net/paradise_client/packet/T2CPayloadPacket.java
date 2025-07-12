package net.paradise_client.packet;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import net.minecraft.class_2540; // PacketByteBuf
import net.minecraft.class_8710; // CustomPayload
import net.minecraft.class_8710.class_9154; // CustomPayload.Id<T>
import net.minecraft.class_9139; // Codec
import net.minecraft.class_9143; // PacketEncoder<T>
import net.minecraft.class_9141; // PacketDecoder<T>
import net.minecraft.class_2960; // Identifier
import net.paradise_client.Helper;

public final class T2CPayloadPacket implements class_8710 {

    private final String command;

    public static final class_9139<class_2540, T2CPayloadPacket> CODEC;
    public static final class_9154<T2CPayloadPacket> ID;

    static {
        // Encoder lambda
        class_9143<T2CPayloadPacket> encoder = (packet, buf) -> packet.write(buf);
        // Decoder lambda
        class_9141<class_2540, T2CPayloadPacket> decoder = T2CPayloadPacket::new;
        CODEC = class_8710.method_56484(encoder, decoder);

        ID = new class_9154<>(class_2960.method_60655("t2c", "bcmd")); // new Identifier("t2c", "bcmd")
    }

    public T2CPayloadPacket(String command) {
        this.command = command;
    }

    public T2CPayloadPacket(class_2540 buf) {
        this.command = buf.method_19772(); // readString()
    }

    public String command() {
        return command;
    }

    @Override
    public class_9154<T2CPayloadPacket> method_56479() {
        return ID;
    }

    public void write(class_2540 buf) {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("T2Code-Console");
        out.writeUTF(command);
        buf.method_52983(out.toByteArray()); // writeByteArray()
        Helper.printChatMessage("Payload sent!");
    }

    @Override
    public boolean equals(Object obj) {
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
