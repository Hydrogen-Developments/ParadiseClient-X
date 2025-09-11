package net.paradise_client.event.impl.network.packet.incoming;

import net.minecraft.network.packet.Packet;

public record PacketIncomingPostEvent(Packet<?> packet) {
  public PacketIncomingPostEvent() {
    this(null);
  }
}
