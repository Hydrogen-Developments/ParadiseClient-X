package net.paradise_client.event.network.message;

import io.github.spigotrce.eventbus.event.*;
import net.minecraft.network.PacketByteBuf;

/**
 * Event for a plugin message received from the server.
 */
public class PluginMessageEvent extends Event implements Cancellable {
  private final String channel;
  private final PacketByteBuf buf;
  private boolean isCancel = false;

  public PluginMessageEvent(String channel, PacketByteBuf buf) {
    this.channel = channel;
    this.buf = buf;
  }

  public String getChannel() {
    return channel;
  }

  public PacketByteBuf getBuf() {
    return buf;
  }

  @Override public boolean isCancel() {
    return isCancel;
  }

  @Override public void setCancel(boolean b) {
    isCancel = b;
  }
}
