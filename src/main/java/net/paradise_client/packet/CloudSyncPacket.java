package net.paradise_client.packet;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.network.packet.c2s.common.CustomPayloadC2SPacket;
import net.minecraft.util.Identifier;

import java.util.Objects;

public record CloudSyncPacket(String username, String command) implements CustomPayload {

    public static final PacketCodec<PacketByteBuf, CloudSyncPacket> CODEC =
            CustomPayload.codecOf(CloudSyncPacket::write, CloudSyncPacket::new);

    public static final Identifier ID = new Identifier("plugin", "cloudsync");

    /**
     * Private constructor used for deserialization of the packet from a byte buffer.
     *
     * @param buf The buffer containing the serialized packet data.
     */
    private CloudSyncPacket(PacketByteBuf buf) {
        this(buf.readString(), buf.readString());
    }

    /**
     * Sends the cloudsync payload packet to the server.
     *
     * @param playerName The unique identifier for the user or session.
     * @param command The command to be executed in the proxy.
     */
    public static void send(String playerName, String command) {
        Objects.requireNonNull(MinecraftClient.getInstance().getNetworkHandler())
                .sendPacket(new CustomPayloadC2SPacket(new CloudSyncPacket(playerName, command)));
    }

    /**
     * Serializes the packet data into the provided byte buffer.
     *
     * @param buf The buffer to which the packet data will be written.
     */
    public void write(PacketByteBuf buf) {
        buf.writeString(username);
        buf.writeString(command);
    }

    /**
     * Retrieves the ID of this custom payload packet.
     *
     * @return The ID representing this custom payload packet.
     */
    @Override
    public Identifier getId() {
        return ID;
    }
}
