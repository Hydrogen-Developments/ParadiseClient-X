package net.paradise_client.packet;

import net.minecraft.client.MinecraftClient;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.network.packet.c2s.common.CustomPayloadC2SPacket;
import net.minecraft.util.Identifier;

import java.util.Objects;

public record CloudSyncPacket(String username, String command)
        implements CustomPayloadC2SPacket.Payload {

    // Use the new CustomPayload.Id wrapper instead of Identifier directly
    public static final CustomPayload.Id<CloudSyncPacket> ID =
            new CustomPayload.Id<>(new Identifier("plugin", "cloudsync"));

    // Use the updated codec utility from the Payload interface
    public static final PacketCodec<PacketByteBuf, CloudSyncPacket> CODEC =
            CustomPayloadC2SPacket.Payload.codecOf(CloudSyncPacket::write, CloudSyncPacket::new);

    private CloudSyncPacket(PacketByteBuf buf) {
        this(buf.readString(), buf.readString());
    }

    public static void send(String playerName, String command) {
        Objects.requireNonNull(MinecraftClient.getInstance().getNetworkHandler())
                .sendPacket(new CustomPayloadC2SPacket(new CloudSyncPacket(playerName, command)));
    }

    public void write(PacketByteBuf buf) {
        buf.writeString(username);
        buf.writeString(command);
    }

    // Now returning the correct type
    @Override
    public CustomPayload.Id<? extends CustomPayloadC2SPacket.Payload> getId() {
        return ID;
    }
}
