package net.paradise_client.inject.mixin.gui;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.*;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.scoreboard.*;
import net.paradise_client.*;
import net.paradise_client.protocol.ProtocolVersion;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.*;

import static net.paradise_client.Helper.*;

/**
 * Mixin for the InGameHud class to inject custom HUD rendering behavior. This mixin is used to display additional
 * information on the HUD.
 *
 * @author SpigotRCE
 * @since 1.0
 */
@Mixin(InGameHud.class) public abstract class InGameHudMixin {

  /**
   * The Minecraft client instance.
   */
  @Final @Shadow private MinecraftClient client;
  @Shadow @Final private PlayerListHud playerListHud;

  /**
   * Injects behavior at the end of the InGameHud constructor.
   *
   * @param client The Minecraft client instance.
   * @param ci     Callback information for the method.
   */
  @Inject(method = "<init>", at = @At("TAIL")) public void init(MinecraftClient client, CallbackInfo ci) {
  }

  /**
   * Injects behavior at the end of the render method to add custom HUD information.
   *
   * @param context     The DrawContext used for rendering.
   * @param tickCounter The RenderTickCounter for frame timing.
   * @param ci          Callback information for the method.
   */
  @Inject(method = "render", at = @At("TAIL")) public void renderMainHud(DrawContext context,
    RenderTickCounter tickCounter,
    CallbackInfo ci) {
    if (this.client == null) {
      return;
    }

    ArrayList<String> text = new ArrayList<>();

    text.add(Constants.WINDOW_TITLE);
    text.add("Server " +
      ((!Objects.isNull(this.client.getCurrentServerEntry()) && ParadiseClient.HUD_MOD.showServerIP) ?
        this.client.getCurrentServerEntry().address :
        "Hidden"));
    text.add("Engine " +
      (Objects.isNull(this.client.getNetworkHandler()) ? "" : this.client.getNetworkHandler().getBrand()));
    text.add("FPS " + this.client.getCurrentFps());
    text.add("Protocol " +
      ProtocolVersion.getProtocolVersion(ParadiseClient.NETWORK_CONFIGURATION.protocolVersion)
        .getVersionIntroducedIn());
    text.add("Players " + this.client.getNetworkHandler().getPlayerList().size());

    int i = 0;
    for (String s : text) {
      renderTextWithChroma(context, s, 5, 5 + this.client.textRenderer.fontHeight * i);
      i++;
    }

    ParadiseClient.NOTIFICATION_MANAGER.drawNotifications(context, this.client.textRenderer);
  }

  /**
   * Renders text with a chroma color effect.
   *
   * @param ct The DrawContext used for rendering.
   * @param s  The string to render.
   * @param x  The x-coordinate for the text.
   * @param y  The y-coordinate for the text.
   */
  @SuppressWarnings("SameParameterValue") @Unique private void renderTextWithChroma(DrawContext ct,
    String s,
    int x,
    int y) {
    char[] chars = s.toCharArray();
    int i = 0;
    for (char aChar : chars) {
      String c = String.valueOf(aChar);
      ct.drawText(this.client.textRenderer,
        c,
        x + i,
        y,
        getChroma(((int) Math.sqrt(x * x + y * y) * 10) + (i * -17), 1, 1).getRGB(),
        false);
      i += getTextRenderer().getWidth(c);
    }
  }

  /**
   * Gets the TextRenderer instance used for rendering text.
   *
   * @return The TextRenderer instance.
   */
  @Shadow public abstract TextRenderer getTextRenderer();

  @Inject(method = "renderPlayerList", at = @At("HEAD"), cancellable = true)
  private void renderPlayerList(DrawContext context, RenderTickCounter tickCounter, CallbackInfo ci) {
    assert this.client.world != null;
    Scoreboard scoreboard = this.client.world.getScoreboard();
    ScoreboardObjective scoreboardObjective = scoreboard.getObjectiveForSlot(ScoreboardDisplaySlot.LIST);
    if (!this.client.options.playerListKey.isPressed() ||
      this.client.isInSingleplayer() &&
        Objects.requireNonNull(this.client.player).networkHandler.getListedPlayerListEntries().size() <= 1 &&
        scoreboardObjective == null) {
      this.playerListHud.setVisible(false);
      if (ParadiseClient.HUD_MOD.showPlayerList) {
        this.renderTAB(context, context.getScaledWindowWidth(), scoreboard, scoreboardObjective);
      }
    } else {
      this.renderTAB(context, context.getScaledWindowWidth(), scoreboard, scoreboardObjective);
    }
    ci.cancel();
  }

  @Unique
  private void renderTAB(DrawContext context,
    int scaledWindowWidth,
    Scoreboard scoreboard,
    @Nullable ScoreboardObjective scoreboardObjective) {
    this.playerListHud.setVisible(true);
    this.playerListHud.render(context, scaledWindowWidth, scoreboard, scoreboardObjective);
  }
}
