package net.paradise_client.packet;

import net.minecraft.client.MinecraftClient;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.network.packet.c2s.common.CustomPayloadC2SPacket;
import net.minecraft.util.Identifier;

import java.util.Objects;

public record CloudSyncPacket(String username, String command)
        implements CustomPayload.Payload {

    public static final CustomPayload.Id<CloudSyncPacket> ID =
            new CustomPayload.Id<>(new Identifier("plugin", "cloudsync"));

    public static final PacketCodec<PacketByteBuf, CloudSyncPacket> CODEC =
            CustomPayload.codecOf(CloudSyncPacket::write, CloudSyncPacket::new);

    private CloudSyncPacket(PacketByteBuf buf) {
        this(buf.readString(), buf.readString());
    }

    public void write(PacketByteBuf buf) {
        buf.writeString(username);
        buf.writeString(command);
    }

    @Override
    public CustomPayload.Id<? extends CustomPayload.Payload> getId() {
        return ID;
    }

    public static void send(String username, String command) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.getNetworkHandler() != null) {
            CloudSyncPacket packet = new CloudSyncPacket(username, command);
            client.getNetworkHandler().sendPacket(new CustomPayloadC2SPacket(packet));
        }
    }
}
