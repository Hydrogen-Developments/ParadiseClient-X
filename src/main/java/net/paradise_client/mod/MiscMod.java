package net.paradise_client.mod;

import net.minecraft.text.Text;

import java.util.ArrayList;

/**
 * Manages miscellaneous data related to network packets and screen state.
 * <p>
 * This class keeps track of various network packet details and the current screen being displayed.
 * </p>
 *
 * @author SpigotRCE
 * @since 1.1
 */
public class MiscMod {
    /**
     * Message which needs to be delayed before sending.
     */
    public final ArrayList<Text> delayedMessages = new ArrayList<>();

    /**
     * Weather the client is outdated or not.
     */
    public boolean isClientOutdated = false;

    /**
     * Latest version retrieved from the API.
     */
    public String latestVersion;

    /**
     * Command suggestions request id.
     */
    public int requestId;
}
