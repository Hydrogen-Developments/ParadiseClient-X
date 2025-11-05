package net.paradise_client;

import net.minecraft.network.*;

public class NetworkConfiguration {
  public NetworkPhase phase;
  public NetworkSide side;
  public int protocolVersion;
  public long lastPacket;

  public void set(NetworkPhase phase, NetworkSide side, int protocolVersion) {
    this.phase = phase;
    this.side = side;
    this.protocolVersion = protocolVersion;
  }
}
