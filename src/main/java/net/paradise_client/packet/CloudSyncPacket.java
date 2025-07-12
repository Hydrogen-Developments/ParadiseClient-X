package net.paradise_client.packet;

import net.minecraft.client.MinecraftClient;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.network.packet.c2s.common.CustomPayloadC2SPacket;
import net.minecraft.util.Identifier;

public record CloudSyncPacket(String username, String command) implements CustomPayload {
    public static final Id<CloudSyncPacket> ID = new Id<>(Identifier.of("plugin", "cloudsync"));
    public static final PacketCodec<PacketByteBuf, CloudSyncPacket> CODEC = PacketCodec.of(
        CloudSyncPacket::write,
        CloudSyncPacket::read
    );

    public static CloudSyncPacket read(PacketByteBuf buf) {
        return new CloudSyncPacket(buf.readString(), buf.readString());
    }

    public void write(PacketByteBuf buf) {
        buf.writeString(username);
        buf.writeString(command);
    }

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }

    public static void send(String user, String command) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.getNetworkHandler() != null) {
            client.getNetworkHandler()
                  .sendPacket(new CustomPayloadC2SPacket(new CloudSyncPacket(user, command)));
        }
    }
}
