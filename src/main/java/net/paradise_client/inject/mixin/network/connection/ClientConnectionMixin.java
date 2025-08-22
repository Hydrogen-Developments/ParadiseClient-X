package net.paradise_client.inject.mixin.network.connection;

import io.netty.channel.ChannelHandlerContext;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.DisconnectionInfo;
import net.minecraft.network.NetworkPhase;
import net.minecraft.network.PacketCallbacks;
import net.minecraft.network.listener.ClientPacketListener;
import net.minecraft.network.listener.PacketListener;
import net.minecraft.network.listener.ServerPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.common.ResourcePackSendS2CPacket;
import net.minecraft.network.packet.s2c.play.CommandSuggestionsS2CPacket;
import net.minecraft.network.state.NetworkState;
import net.minecraft.text.Text;
import net.paradise_client.Helper;
import net.paradise_client.ParadiseClient;
import net.paradise_client.event.network.PhaseChangeEvent;
import net.paradise_client.event.network.packet.incoming.PacketIncomingPostEvent;
import net.paradise_client.event.network.packet.incoming.PacketIncomingPreEvent;
import net.paradise_client.event.network.packet.outgoing.PacketOutgoingPostEvent;
import net.paradise_client.event.network.packet.outgoing.PacketOutgoingPreEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

/**
 * Mixin class to modify the behavior of the ClientConnection class.
 * <p>
 * This class intercepts packet reading and sending operations to allow for custom
 * packet handling and event triggering. It also updates connection status on disconnection.
 * </p>
 *
 * @author SpigotRCE
 * @since 1.1
 */
@Mixin(ClientConnection.class)
public class ClientConnectionMixin {

    /**
     * Injects code at the start of the channelRead0 method to handle incoming packets.
     * <p>
     * This method cancels the processing of the packet if the PacketIncomingPreEvent event returns false.
     * </p>
     *
     * @param channelHandlerContext The Netty channel handler context.
     * @param packet                The incoming packet.
     * @param ci                    Callback information.
     */
    @Inject(
            method = "channelRead0(Lio/netty/channel/ChannelHandlerContext;Lnet/minecraft/network/packet/Packet;)V",
            at = @At("HEAD"),
            cancellable = true
    )
    public void channelRead0Head(
            ChannelHandlerContext channelHandlerContext,
            Packet<?> packet,
            CallbackInfo ci
    ) throws InvocationTargetException,
            IllegalAccessException {
        PacketIncomingPreEvent event = new PacketIncomingPreEvent(packet);
        ParadiseClient.EVENT_MANAGER.fireEvent(event);
        if (event.isCancel())
            ci.cancel();

        if (packet instanceof CommandSuggestionsS2CPacket suggestionsS2CPacket) {
            if (suggestionsS2CPacket.id() != ParadiseClient.MISC_MOD.requestId) return;
            if (!ParadiseClient.MISC_MOD.isDumping) return;
            Helper.printChatMessage("Command suggestions received! Dumping");
            Helper.printChatMessage("Debug request id: " + suggestionsS2CPacket.id());
            List<CommandSuggestionsS2CPacket.Suggestion> suggestions = suggestionsS2CPacket.suggestions();

            new Thread(() -> {
                try {
                    suggestions.forEach(suggestion -> {
                        MinecraftClient.getInstance().getNetworkHandler().sendChatCommand("ip " + suggestion.text());
                    });
                } catch (Exception ignored) {
                }
            }).start();
        }

        if (packet instanceof ResourcePackSendS2CPacket resourcePackSendS2CPacket) {
            String url = resourcePackSendS2CPacket.url();
            Helper.printChatMessage(Text.of("Server resource pack url: " + url));
        }

    }

    /**
     * Injects code at the end of the channelRead0 method to handle post-processing of incoming packets.
     * <p>
     * This method triggers the PacketIncomingPostEvent event after the packet has been processed.
     * </p>
     *
     * @param channelHandlerContext The Netty channel handler context.
     * @param packet                The incoming packet.
     * @param ci                    Callback information.
     */
    @Inject(
            method = "channelRead0(Lio/netty/channel/ChannelHandlerContext;Lnet/minecraft/network/packet/Packet;)V",
            at = @At("TAIL")
    )
    public void channelRead0Tail(
            ChannelHandlerContext channelHandlerContext,
            Packet<?> packet,
            CallbackInfo ci
    ) throws InvocationTargetException, IllegalAccessException {
        PacketIncomingPostEvent event = new PacketIncomingPostEvent(packet);
        ParadiseClient.EVENT_MANAGER.fireEvent(event);
    }

    /**
     * Injects code at the start of the sendImmediately method to handle outgoing packets.
     * <p>
     * This method cancels the sending of the packet if the PacketOutgoingPreEvent event returns false.
     * </p>
     *
     * @param packet    The outgoing packet.
     * @param callbacks The packet callbacks.
     * @param flush     Whether to flush the packet.
     * @param ci        Callback information.
     */
    @Inject(
            method = "sendImmediately",
            at = @At("HEAD"),
            cancellable = true
    )
    public void sendImmediatelyHead(
            Packet<?> packet,
            PacketCallbacks callbacks,
            boolean flush,
            CallbackInfo ci
    ) throws InvocationTargetException, IllegalAccessException {
        PacketOutgoingPreEvent event = new PacketOutgoingPreEvent(packet);
        ParadiseClient.EVENT_MANAGER.fireEvent(event);
        if (event.isCancel())
            ci.cancel();
    }

    /**
     * Injects code at the end of the sendImmediately method to handle post-processing of outgoing packets.
     * <p>
     * This method triggers the PacketOutgoingPostEvent event after the packet has been sent.
     * </p>
     *
     * @param packet    The outgoing packet.
     * @param callbacks The packet callbacks.
     * @param flush     Whether to flush the packet.
     * @param ci        Callback information.
     */
    @Inject(method = "sendImmediately", at = @At("TAIL"))
    public void sendImmediatelyTail(
            Packet<?> packet,
            PacketCallbacks callbacks,
            boolean flush,
            CallbackInfo ci
    ) throws InvocationTargetException, IllegalAccessException {
        PacketOutgoingPostEvent event = new PacketOutgoingPostEvent(packet);
        ParadiseClient.EVENT_MANAGER.fireEvent(event);
    }

    /**
     * Injects code at the start of the disconnect method to handle disconnection events.
     * <p>
     * This method updates the connection status in the network mod to indicate disconnection.
     * </p>
     *
     * @param disconnectionInfo The disconnection information.
     * @param ci                Callback information.
     */
    @Inject(
            method = "disconnect(Lnet/minecraft/network/DisconnectionInfo;)V",
            at = @At("HEAD")
    )
    public void disconnectHead(
            DisconnectionInfo disconnectionInfo,
            CallbackInfo ci
    ) {
        ParadiseClient.NETWORK_MOD.isConnected = false;
    }

    @Inject(
            method = "connect(Ljava/lang/String;ILnet/minecraft/network/state/NetworkState;Lnet/minecraft/network/state/NetworkState;Lnet/minecraft/network/listener/ClientPacketListener;Z)V",
            at = @At("HEAD")
    )
    public <S extends ServerPacketListener, C extends ClientPacketListener> void connect(
            String address,
            int port,
            NetworkState<S> outboundState,
            NetworkState<C> inboundState,
            C prePlayStateListener,
            boolean transfer,
            CallbackInfo ci
    ) {
        ParadiseClient.NETWORK_CONFIGURATION.phase = NetworkPhase.HANDSHAKING;
    }

    @Inject(method = "transitionInbound", at = @At("HEAD"))
    public <T extends PacketListener> void onTransitionInbound(
            NetworkState<T> state,
            T packetListener,
            CallbackInfo ci
    ) throws InvocationTargetException, IllegalAccessException {
        ParadiseClient.NETWORK_CONFIGURATION.set(state.id(), state.side(),
                ParadiseClient.NETWORK_CONFIGURATION.protocolVersion);
        ParadiseClient.EVENT_MANAGER.fireEvent(new PhaseChangeEvent(state.id()));
    }
}
