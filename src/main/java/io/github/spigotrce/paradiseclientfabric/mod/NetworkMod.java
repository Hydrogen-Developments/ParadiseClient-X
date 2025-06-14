package io.github.spigotrce.paradiseclientfabric.mod;

import io.github.spigotrce.paradiseclientfabric.inject.accessor.PayloadTypeRegistryImplAccessor;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;

import java.util.ArrayList;

/**
 * Manages network connection state and server information.
 * <p>
 * This class tracks whether the client is connected to a server and stores the server's IP address.
 * </p>
 *
 * @author SpigotRCE
 * @since 2.17
 */
@SuppressWarnings("unused")
public class NetworkMod {
    /**
     * Indicates whether the client is currently connected to a server.
     */
    public boolean isConnected = false;

    /**
     * The IP address of the server the client is connected to.
     */
    public String serverIP = "";

    /**
     * The registered channels by their names.
     */
    public ArrayList<String> getRegisteredChannelsByName() {
        return ((PayloadTypeRegistryImplAccessor) PayloadTypeRegistry.playC2S()).paradiseClient_Fabric$getRegisteredChannelsByName();
    }
}
