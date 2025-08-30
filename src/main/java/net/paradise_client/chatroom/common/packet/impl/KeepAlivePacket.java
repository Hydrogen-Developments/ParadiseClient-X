package net.paradise_client.chatroom.common.packet.impl;

import io.netty.buffer.ByteBuf;
import net.paradise_client.chatroom.common.packet.Packet;
import net.paradise_client.chatroom.common.packet.handler.AbstractPacketHandler;

public class KeepAlivePacket extends Packet {
  private int id;

  public KeepAlivePacket(int id) {
    this.id = id;
  }

  public KeepAlivePacket() {
    this.id = 0;
  }

  @Override public void encode(ByteBuf buffer) {
    writeInt(buffer, id);
  }

  @Override public void decode(ByteBuf buffer) {
    id = readInt(buffer);
  }

  @Override public void handle(AbstractPacketHandler handler) throws Exception {
    handler.handle(this);
  }

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }
}
