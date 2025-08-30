package net.paradise_client.event.network.packet.outgoing;

import io.github.spigotrce.eventbus.event.*;
import net.minecraft.network.packet.Packet;

@SuppressWarnings("unused") public class PacketOutgoingPreEvent extends Event implements Cancellable {
  private boolean isCancel = false;
  private Packet<?> packet;

  public PacketOutgoingPreEvent(Packet<?> packet) {
    this.packet = packet;
  }

  public Packet<?> getPacket() {
    return packet;
  }

  public void setPacket(Packet<?> packet) {
    this.packet = packet;
  }

  @Override public boolean isCancel() {
    return isCancel;
  }

  @Override public void setCancel(boolean b) {
    isCancel = b;
  }
}
