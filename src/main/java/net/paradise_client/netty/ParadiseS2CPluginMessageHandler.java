package net.paradise_client.netty;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import net.md_5.bungee.protocol.DefinedPacket;
import net.md_5.bungee.protocol.Protocol;
import net.md_5.bungee.protocol.ProtocolConstants;
import net.md_5.bungee.protocol.packet.PluginMessage;
import net.minecraft.network.PacketByteBuf;
import net.paradise_client.Constants;
import net.paradise_client.Helper;
import net.paradise_client.ParadiseClient;
import net.paradise_client.event.channel.PluginMessageEvent;

import java.nio.charset.Charset;
import java.util.List;
import java.util.Objects;

public class ParadiseS2CPluginMessageHandler extends MessageToMessageDecoder<ByteBuf> {
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        PacketByteBuf b = Helper.byteBufToPacketBuf(ctx.alloc().buffer().writeBytes(in));

        Protocol protocol = Helper.getBungeeProtocolForCurrentPhase();

        if (protocol != null) {
            int id = b.readVarInt();
            int protocolVersion = ParadiseClient.NETWORK_CONFIGURATION.protocolVersion;

            DefinedPacket packet = protocol.TO_CLIENT.createPacket(id, protocolVersion);
            if (packet instanceof PluginMessage message) {
                message.read(b.asByteBuf(), ProtocolConstants.Direction.TO_CLIENT, id);
                notifyChannels(message);

                PluginMessageEvent event = new PluginMessageEvent(
                        message.getTag(),
                        new PacketByteBuf(
                                Unpooled.buffer()
                                        .writeBytes(message.getData())
                        )
                );
                ParadiseClient.EVENT_MANAGER.fireEvent(event);

                if (event.isCancel()) {
                    return;
                }
            }
        }

        out.add(in.resetReaderIndex().retain());
    }

    private void notifyChannels(PluginMessage message) {
        String channelName = message.getTag();
        PacketByteBuf buf = Helper.byteBufToPacketBuf(
                Unpooled.buffer().writeBytes(message.getData())
        );

        Helper.printChatMessage("&8&m-----------------------------------------------------", false);
        if (Objects.equals(channelName, "minecraft:register")
                || Objects.equals(channelName, "REGISTER")) {
            Helper.printChatMessage("&b&l[Channel Registration]");
            String[] channels = buf.toString(Charset.defaultCharset()).split("\000");

            for (String channel : channels) {
                boolean isKnown = ParadiseClient.NETWORK_MOD.getRegisteredChannelsByName().contains(channel);
                String color = isKnown ? "&c" : "&a";
                Helper.printChatMessage("&7 - &fChannel: " + color + channel);

                if (isKnown) {
                    Helper.showNotification("Exploit Detected!", "Channel: " + channel);
                }
            }
        } else {
            Helper.printChatMessage("&b&l[Channel Data]");
            String color = ParadiseClient.NETWORK_MOD.getRegisteredChannelsByName()
                    .contains(channelName) ? "&c" : "&a";
            Helper.printChatMessage("&7 - &fChannel: " + color + channelName);
            Helper.printChatMessage("&7 - &fRaw Data: " + color + buf.toString(Charset.defaultCharset()));
        }

        Helper.printChatMessage("&8&m-----------------------------------------------------", false);
    }
}
