package net.paradise_client.inject.mixin.network.connection;

import io.netty.channel.Channel;
import net.minecraft.network.ClientConnection;
import net.paradise_client.inject.accessor.ClientConnectionAccessor;
import org.spongepowered.asm.mixin.*;

@Mixin(ClientConnection.class) public class ClientConnection_2Mixin implements ClientConnectionAccessor {
  @Shadow private Channel channel;

  @Override public Channel paradiseClient$getChannel() {
    return channel;
  }
}
