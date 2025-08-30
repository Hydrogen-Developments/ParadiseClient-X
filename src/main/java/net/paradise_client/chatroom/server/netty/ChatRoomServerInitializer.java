package net.paradise_client.chatroom.server.netty;

import io.netty.channel.*;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.haproxy.HAProxyMessageDecoder;
import net.paradise_client.chatroom.common.netty.CommonChannelInitializer;
import net.paradise_client.chatroom.server.Main;

public class ChatRoomServerInitializer extends ChannelInitializer<SocketChannel> {
  @Override public void initChannel(SocketChannel ch) throws Exception {
    ChannelPipeline pipeline = ch.pipeline();

    CommonChannelInitializer.init(pipeline);

    if (Main.CONFIG.getServer().useHAProxy()) {
      pipeline.addFirst(new HAProxyMessageDecoder());
      pipeline.addLast(new HaProxyMessageHandler());
    }
    pipeline.addLast(new ChatRoomServerHandler());
  }
}
