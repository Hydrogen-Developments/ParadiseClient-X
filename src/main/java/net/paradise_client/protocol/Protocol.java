package net.paradise_client.protocol;

import com.google.common.base.Preconditions;
import com.google.common.collect.Iterables;
import java.util.Arrays;
import java.util.function.Supplier;

import gnu.trove.map.*;
import gnu.trove.map.hash.*;
import net.paradise_client.protocol.packet.AbstractPacket;
import net.paradise_client.protocol.packet.impl.*;

/**
 * Minimal implementation of BungeeCord protocol.
 *
 * @author SpigotRCE
 */
public enum Protocol {
  HANDSHAKE {
    {
      this.TO_SERVER.registerPacket(HandshakePacket.class, HandshakePacket::new, map(47, 0));
    }
  }, GAME {
    {
      this.TO_CLIENT.registerPacket(PluginMessagePacket.class,
        PluginMessagePacket::new,
        map(47, 63),
        map(107, 24),
        map(393, 25),
        map(477, 24),
        map(573, 25),
        map(735, 24),
        map(751, 23),
        map(755, 24),
        map(759, 21),
        map(760, 22),
        map(761, 21),
        map(762, 23),
        map(764, 24),
        map(766, 25),
        map(770, 24));
      this.TO_SERVER.registerPacket(PluginMessagePacket.class,
        PluginMessagePacket::new,
        map(47, 23),
        map(107, 9),
        map(335, 10),
        map(338, 9),
        map(393, 10),
        map(477, 11),
        map(755, 10),
        map(759, 12),
        map(760, 13),
        map(761, 12),
        map(762, 13),
        map(764, 15),
        map(765, 16),
        map(766, 18),
        map(768, 20));
    }
  }, STATUS {
  }, LOGIN {
  }, CONFIGURATION {
    {
      this.TO_CLIENT.registerPacket(PluginMessagePacket.class,
        PluginMessagePacket::new,
        map(764, 0),
        map(766, 1));
      this.TO_SERVER.registerPacket(PluginMessagePacket.class,
        PluginMessagePacket::new,
        map(764, 1),
        map(766, 2));
    }
  };

  public static final int MAX_PACKET_ID = 255;
  public final DirectionData TO_SERVER;
  public final DirectionData TO_CLIENT;

  Protocol() {
    this.TO_SERVER = new DirectionData(this, ProtocolVersion.Direction.TO_SERVER);
    this.TO_CLIENT = new DirectionData(this, ProtocolVersion.Direction.TO_CLIENT);
  }

  public static void main(String[] args) {
    for (int version : ProtocolVersion.SUPPORTED_VERSION_IDS) {
      dump(version);
    }

  }

  private static void dump(int version) {
    for (Protocol protocol : values()) {
      dump(version, protocol);
    }

  }

  private static void dump(int version, Protocol protocol) {
    dump(version, protocol.TO_CLIENT);
    dump(version, protocol.TO_SERVER);
  }

  private static void dump(int version, DirectionData data) {
    for (int id = 0; id < 255; ++id) {
      AbstractPacket packet = data.createPacket(id, version);
      if (packet != null) {
        System.out.println(version +
          " " +
          data.protocolPhase +
          " " +
          data.direction +
          " " +
          id +
          " " +
          packet.getClass().getSimpleName());
      }
    }

  }

  private static ProtocolMapping map(int protocol, int id) {
    return new ProtocolMapping(protocol, id);
  }

  private static class ProtocolData {
    private final int protocolVersion;
    private final TObjectIntMap<Class<? extends AbstractPacket>> packetMap = new TObjectIntHashMap<>(255);
    private final Supplier<? extends AbstractPacket>[] packetConstructors = new Supplier[255];


    public ProtocolData(int protocolVersion) {
      this.protocolVersion = protocolVersion;
    }


    public int getProtocolVersion() {
      return this.protocolVersion;
    }


    public TObjectIntMap<Class<? extends AbstractPacket>> getPacketMap() {
      return this.packetMap;
    }


    public Supplier<? extends AbstractPacket>[] getPacketConstructors() {
      return this.packetConstructors;
    }


    public boolean equals(Object o) {
      if (o == this) {
        return true;
      } else if (!(o instanceof ProtocolData other)) {
        return false;
      } else {
        if (!other.canEqual(this)) {
          return false;
        } else if (this.getProtocolVersion() != other.getProtocolVersion()) {
          return false;
        } else {
          Object this$packetMap = this.getPacketMap();
          Object other$packetMap = other.getPacketMap();
          if (this$packetMap == null) {
            if (other$packetMap != null) {
              return false;
            }
          } else if (!this$packetMap.equals(other$packetMap)) {
            return false;
          }

          return Arrays.deepEquals(this.getPacketConstructors(), other.getPacketConstructors());
        }
      }
    }


    protected boolean canEqual(Object other) {
      return other instanceof ProtocolData;
    }


    public int hashCode() {
      int PRIME = 59;
      int result = 1;
      result = result * 59 + this.getProtocolVersion();
      Object $packetMap = this.getPacketMap();
      result = result * 59 + ($packetMap == null ? 43 : $packetMap.hashCode());
      result = result * 59 + Arrays.deepHashCode(this.getPacketConstructors());
      return result;
    }


    public String toString() {
      return "Protocol.ProtocolData(protocolVersion=" +
        this.getProtocolVersion() +
        ", packetMap=" +
        this.getPacketMap() +
        ", packetConstructors=" +
        Arrays.deepToString(this.getPacketConstructors()) +
        ")";
    }
  }

  private record ProtocolMapping(int protocolVersion, int packetID) {
    public boolean equals(Object o) {
        if (o == this) {
          return true;
        } else if (!(o instanceof ProtocolMapping other)) {
          return false;
        } else {
          if (!other.canEqual(this)) {
            return false;
          } else if (this.protocolVersion() != other.protocolVersion()) {
            return false;
          } else {
            return this.packetID() == other.packetID();
          }
        }
      }


      private boolean canEqual(Object other) {
        return other instanceof ProtocolMapping;
      }

      public int hashCode() {
        int PRIME = 59;
        int result = 1;
        result = result * 59 + this.protocolVersion();
        result = result * 59 + this.packetID();
        return result;
      }


      public String toString() {
        return "Protocol.ProtocolMapping(protocolVersion=" +
          this.protocolVersion() +
          ", packetID=" +
          this.packetID() +
          ")";
      }
    }

  public static final class DirectionData {
    private final TIntObjectMap<ProtocolData> protocols = new TIntObjectHashMap<>();
    private final Protocol protocolPhase;
    private final ProtocolVersion.Direction direction;

    public DirectionData(Protocol protocolPhase, ProtocolVersion.Direction direction) {
      this.protocolPhase = protocolPhase;
      this.direction = direction;

      for (int protocol : ProtocolVersion.SUPPORTED_VERSION_IDS) {
        this.protocols.put(protocol, new ProtocolData(protocol));
      }

    }

    private ProtocolData getProtocolData(int version) {
      ProtocolData protocol = this.protocols.get(version);
      if (protocol == null && this.protocolPhase != Protocol.GAME) {
        protocol = (ProtocolData) Iterables.getFirst(this.protocols.valueCollection(), (Object) null);
      }

      return protocol;
    }

    public final AbstractPacket createPacket(int id, int version) {
      ProtocolData protocolData = this.getProtocolData(version);
      if (protocolData == null) {
        throw new BadPacketException("Unsupported protocol version " + version);
      } else if (id <= 255 && id >= 0) {
        Supplier<? extends AbstractPacket> constructor = protocolData.packetConstructors[id];
        return constructor == null ? null : constructor.get();
      } else {
        throw new BadPacketException("Packet with id " + id + " outside of range");
      }
    }

    private void registerPacket(Class<? extends AbstractPacket> packetClass,
      Supplier<? extends AbstractPacket> constructor,
      ProtocolMapping... mappings) {
      int mappingIndex = 0;
      ProtocolMapping mapping = mappings[mappingIndex];

      for (int protocol : ProtocolVersion.SUPPORTED_VERSION_IDS) {
        if (protocol >= mapping.protocolVersion) {
          if (mapping.protocolVersion < protocol && mappingIndex + 1 < mappings.length) {
            ProtocolMapping nextMapping = mappings[mappingIndex + 1];
            if (nextMapping.protocolVersion == protocol) {
              Preconditions.checkState(nextMapping.packetID != mapping.packetID,
                "Duplicate packet mapping (%s, %s)",
                mapping.protocolVersion,
                nextMapping.protocolVersion);
              mapping = nextMapping;
              ++mappingIndex;
            }
          }

          if (mapping.packetID < 0) {
            break;
          }

          ProtocolData data = (ProtocolData) this.protocols.get(protocol);
          Preconditions.checkState(data.packetConstructors[mapping.packetID] == null,
            "Duplicate packet mapping (%s)",
            mapping.packetID);
          data.packetMap.put(packetClass, mapping.packetID);
          data.packetConstructors[mapping.packetID] = constructor;
        }
      }

    }

    public boolean hasPacket(Class<? extends AbstractPacket> packet, int version) {
      ProtocolData protocolData = this.getProtocolData(version);
      if (protocolData == null) {
        throw new BadPacketException("Unsupported protocol version");
      } else {
        return protocolData.packetMap.containsKey(packet);
      }
    }

    public int getId(Class<? extends AbstractPacket> packet, int version) {
      ProtocolData protocolData = this.getProtocolData(version);
      if (protocolData == null) {
        throw new BadPacketException("Unsupported protocol version");
      } else {
        Preconditions.checkArgument(protocolData.packetMap.containsKey(packet),
          "Cannot get ID for packet %s in phase %s with direction %s",
          packet,
          this.protocolPhase,
          this.direction);
        return protocolData.packetMap.get(packet);
      }
    }

    public ProtocolVersion.Direction getDirection() {
      return this.direction;
    }
  }
}
