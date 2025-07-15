package net.paradise_client.protocol;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public enum ProtocolVersion {
    UNKNOWN(-1, "Unknown"),
    LEGACY(-2, "Legacy"),
    MINECRAFT_1_8(47,
            "1.8", "1.8.1", "1.8.2", "1.8.3", "1.8.4", "1.8.5", "1.8.6", "1.8.7", "1.8.8", "1.8.9"),
    MINECRAFT_1_9(107, "1.9"),
    MINECRAFT_1_9_1(108, "1.9.1"),
    MINECRAFT_1_9_2(109, "1.9.2"),
    MINECRAFT_1_9_4(110, "1.9.3", "1.9.4"),
    MINECRAFT_1_10(210, "1.10", "1.10.1", "1.10.2"),
    MINECRAFT_1_11(315, "1.11"),
    MINECRAFT_1_11_1(316, "1.11.1", "1.11.2"),
    MINECRAFT_1_12(335, "1.12"),
    MINECRAFT_1_12_1(338, "1.12.1"),
    MINECRAFT_1_12_2(340, "1.12.2"),
    MINECRAFT_1_13(393, "1.13"),
    MINECRAFT_1_13_1(401, "1.13.1"),
    MINECRAFT_1_13_2(404, "1.13.2"),
    MINECRAFT_1_14(477, "1.14"),
    MINECRAFT_1_14_1(480, "1.14.1"),
    MINECRAFT_1_14_2(485, "1.14.2"),
    MINECRAFT_1_14_3(490, "1.14.3"),
    MINECRAFT_1_14_4(498, "1.14.4"),
    MINECRAFT_1_15(573, "1.15"),
    MINECRAFT_1_15_1(575, "1.15.1"),
    MINECRAFT_1_15_2(578, "1.15.2"),
    MINECRAFT_1_16(735, "1.16"),
    MINECRAFT_1_16_1(736, "1.16.1"),
    MINECRAFT_1_16_2(751, "1.16.2"),
    MINECRAFT_1_16_3(753, "1.16.3"),
    MINECRAFT_1_16_4(754, "1.16.4", "1.16.5"),
    MINECRAFT_1_17(755, "1.17"),
    MINECRAFT_1_17_1(756, "1.17.1"),
    MINECRAFT_1_18(757, "1.18", "1.18.1"),
    MINECRAFT_1_18_2(758, "1.18.2"),
    MINECRAFT_1_19(759, "1.19"),
    MINECRAFT_1_19_1(760, "1.19.1", "1.19.2"),
    MINECRAFT_1_19_3(761, "1.19.3"),
    MINECRAFT_1_19_4(762, "1.19.4"),
    MINECRAFT_1_20(763, "1.20", "1.20.1"),
    MINECRAFT_1_20_2(764, "1.20.2"),
    MINECRAFT_1_20_3(765, "1.20.3", "1.20.4"),
    MINECRAFT_1_20_5(766, "1.20.5", "1.20.6"),
    MINECRAFT_1_21(767, "1.21", "1.21.1"),
    MINECRAFT_1_21_2(768, "1.21.2", "1.21.3"),
    MINECRAFT_1_21_4(769, "1.21.4"),
    MINECRAFT_1_21_5(770, "1.21.5"),
    MINECRAFT_1_21_6(771, "1.21.6"),
    MINECRAFT_1_21_7(772, "1.21.7");

    public static final ProtocolVersion MINIMUM_VERSION = MINECRAFT_1_8;
    public static final ProtocolVersion MAXIMUM_VERSION = values()[values().length - 1];
    public static final ImmutableMap<Integer, ProtocolVersion> ID_TO_PROTOCOL_CONSTANT;

    static {
        Map<Integer, ProtocolVersion> versions = new HashMap<>();
        for (ProtocolVersion version : values()) {
            versions.putIfAbsent(version.protocol, version);
        }

        ID_TO_PROTOCOL_CONSTANT = ImmutableMap.copyOf(versions);
    }

    private final int protocol;
    private final String[] names;

    ProtocolVersion(int protocol, String... names) {
        this.protocol = protocol;
        this.names = names;
    }

    public static boolean isSupported(ProtocolVersion protocolVersion) {
        return isSupported(protocolVersion.getProtocol());
    }

    public static boolean isSupported(int protocol) {
        return protocol >= MINIMUM_VERSION.getProtocol() && protocol <= MAXIMUM_VERSION.getProtocol();
    }

    public static ProtocolVersion getProtocolVersion(int protocol) {
        return ID_TO_PROTOCOL_CONSTANT.getOrDefault(protocol, UNKNOWN);
    }

    public int getProtocol() {
        return protocol;
    }

    public String getVersionIntroducedIn() {
        return names[0];
    }

    public String getMostRecentSupportedVersion() {
        return names[names.length - 1];
    }

    public List<String> getVersionsSupportedBy() {
        return ImmutableList.copyOf(names);
    }

    @Override
    public String toString() {
        return getVersionIntroducedIn();
    }
}
