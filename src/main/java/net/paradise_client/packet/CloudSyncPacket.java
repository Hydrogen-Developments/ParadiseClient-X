package net.paradise_client.packet;

import net.minecraft.client.MinecraftClient;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.network.packet.c2s.common.CustomPayloadC2SPacket;
import net.minecraft.util.Identifier;

import java.util.Objects;

public record CloudSyncPacket(String username, String command) implements CustomPayload.IdAware {

    // Define the unique ID for this custom payload packet
    public static final CustomPayload.Id<CloudSyncPacket> ID =
            new CustomPayload.Id<>(Identifier.of("plugin:cloudsync"));

    // Define the codec for serializing/deserializing the packet
    public static final PacketCodec<PacketByteBuf, CloudSyncPacket> CODEC =
            CustomPayload.codecOf(CloudSyncPacket::write, CloudSyncPacket::new);

    // Deserialization constructor (from buffer)
    private CloudSyncPacket(PacketByteBuf buf) {
        this(buf.readString(), buf.readString());
    }

    // Method to send the packet
    public static void send(String playerName, String command) {
        Objects.requireNonNull(MinecraftClient.getInstance().getNetworkHandler())
                .sendPacket(new CustomPayloadC2SPacket(new CloudSyncPacket(playerName, command)));
    }

    // Write/serialize packet data to buffer
    public void write(PacketByteBuf buf) {
        buf.writeString(username);
        buf.writeString(command);
    }

    // Required by CustomPayload.IdAware interface (new in 1.20.5+ / 1.21+)
    @Override
    public CustomPayload.Id<? extends CustomPayload> payloadId() {
        return ID;
    }
                        }
