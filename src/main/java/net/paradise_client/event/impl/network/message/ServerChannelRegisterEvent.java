package net.paradise_client.event.impl.network.message;

import java.util.List;

public record ServerChannelRegisterEvent(List<String> channels) {
  public ServerChannelRegisterEvent() {
    this(List.of());
  }
}
