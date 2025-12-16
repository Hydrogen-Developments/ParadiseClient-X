package net.paradise_client.event.bus;

import net.paradise_client.event.impl.chat.*;
import net.paradise_client.event.impl.minecraft.*;
import net.paradise_client.event.impl.network.*;
import net.paradise_client.event.impl.network.message.*;
import net.paradise_client.event.impl.network.packet.incoming.*;
import net.paradise_client.event.impl.network.packet.outgoing.*;

import java.util.*;

public class EventBus {
  // Channels
  public static final EventChannel<ChatPreEvent> CHAT_PRE_EVENT_CHANNEL = new EventChannel<>(new ChatPreEvent());
  public static final EventChannel<ChatPostEvent> CHAT_POST_EVENT_CHANNEL = new EventChannel<>(new ChatPostEvent());
  public static final EventChannel<ClientShutdownEvent> CLIENT_SHUTDOWN_EVENT_CHANNEL =
    new EventChannel<>(new ClientShutdownEvent());
  public static final EventChannel<PluginMessageEvent> PLUGIN_MESSAGE_EVENT_CHANNEL =
    new EventChannel<>(new PluginMessageEvent());
  public static final EventChannel<ServerChannelRegisterEvent> SERVER_CHANNEL_REGISTER_EVENT_EVENT_CHANNEL =
    new EventChannel<>(new ServerChannelRegisterEvent());
  public static final EventChannel<PacketIncomingPreEvent> PACKET_INCOMING_PRE_EVENT_CHANNEL =
    new EventChannel<>(new PacketIncomingPreEvent());
  public static final EventChannel<PacketIncomingPostEvent> PACKET_INCOMING_POST_EVENT_CHANNEL =
    new EventChannel<>(new PacketIncomingPostEvent());
  public static final EventChannel<PacketOutgoingPreEvent> PACKET_OUTGOING_PRE_EVENT_CHANNEL =
    new EventChannel<>(new PacketOutgoingPreEvent());
  public static final EventChannel<PacketOutgoingPostEvent> PACKET_OUTGOING_POST_EVENT_CHANNEL =
    new EventChannel<>(new PacketOutgoingPostEvent());
  public static final EventChannel<LoginEvent> LOGIN_EVENT_CHANNEL = new EventChannel<>(new LoginEvent());
  public static final EventChannel<PhaseChangeEvent> PHASE_CHANGE_EVENT_CHANNEL =
    new EventChannel<>(new PhaseChangeEvent());
  public static final EventChannel<HudStartRenderEvent> HUD_START_RENDER_EVENT_CHANNEL =
    new EventChannel<>(new HudStartRenderEvent());

  public static <T> ListenerContext<T> fire(EventChannel<T> channel, T event) {
    return channel.fire(event);
  }

  @FunctionalInterface public interface EventHandler<T> {
    void handle(T event, ListenerContext<T> context);
  }

  public interface Cancelable {
    boolean isCancelled();

    void cancel();
  }

  public static class EventChannel<T> {
    private final List<EventHandler<T>> listeners = new ArrayList<>();
    private final T superEvent;

    public EventChannel(T superEvent) {
      this.superEvent = superEvent;
    }

    public static <T> ListenerContext<T> fire(EventChannel<T> channel, T event) {
      return channel.fire(event);
    }

    public ListenerContext<T> fire(T event) {
      ListenerContext<T> context = new ListenerContext<>(event);

      for (EventHandler<T> handler : listeners) {
        handler.handle(event, context);
      }

      return context;
    }

    public void on(EventHandler<T> handler) {
      listeners.add(handler);
    }

    public T getSuperEvent() {
      return superEvent;
    }
  }

  public static class ListenerContext<T> {
    private final T event;
    private boolean cancelled = false;

    public ListenerContext(T event) {
      this.event = event;
    }

    public T getEvent() {
      return event;
    }

    public boolean isCancelled() {
      return cancelled;
    }

    public void cancel() {
      this.cancelled = true;
    }
  }

  public record EventResult<T>(T event) {
    public boolean isCancelled() {
      return event instanceof Cancelable c && c.isCancelled();
    }

    public T getData() {
      return event;
    }
  }
}
