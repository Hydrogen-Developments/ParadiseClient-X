package net.paradise_client.inject.mixin.gui.screen;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.*;
import net.minecraft.client.gui.screen.*;
import net.minecraft.client.gui.widget.*;
import net.minecraft.client.realms.gui.screen.RealmsNotificationsScreen;
import net.minecraft.text.Text;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import net.paradise_client.*;
import net.paradise_client.wallpaper.*;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Mixin class to customize the behavior of the Title Screen in Minecraft.
 * <p>
 * This class modifies the Title Screen to include a custom button recommending the installation of "ViaFabricPlus" and
 * customizes the background fade effect. It also displays additional information about the client and game version.
 * </p>
 *
 * @author SpigotRCE
 * @since 2.9
 */
@SuppressWarnings("unused") @Mixin(TitleScreen.class) public abstract class TitleScreenMixin extends Screen {

  /**
   * The splash text renderer used to display splash texts on the Title Screen.
   */
  @Nullable @Shadow private SplashTextRenderer splashText;

  /**
   * The Realms Notifications Screen displayed on the Title Screen if active.
   */
  @Nullable @Shadow private RealmsNotificationsScreen realmsNotificationGui;

  /**
   * Alpha value for the background fade effect on the Title Screen.
   */
  @Mutable @Shadow private float backgroundAlpha;

  /**
   * Flag indicating whether the background fade effect is enabled.
   */
  @Mutable @Shadow private boolean doBackgroundFade;

  /**
   * The start time for the background fade effect, in milliseconds.
   */
  @Mutable @Shadow private long backgroundFadeStart;

  /**
   * The logo drawer used to render the logo on the Title Screen.
   */
  @Final @Shadow private LogoDrawer logoDrawer;

  /**
   * Constructs a new instance of the TitleScreenMixin.
   *
   * @param title The title of the screen.
   */
  protected TitleScreenMixin(Text title) {
    super(title);
  }

  /**
   * Injects a custom button into the Title Screen if "viafabricplus" is not loaded. The button directs the user to a
   * URL for installation.
   *
   * @param ci Callback information.
   */
  @Inject(method = "init", at = @At(value = "TAIL")) public void init(CallbackInfo ci) {
    Text updateMessage1 = Helper.parseColoredText("&2Current version: &1" +
      Constants.VERSION +
      " &2Latetst version: &1" +
      ParadiseClient.MISC_MOD.latestVersion +
      " &fClick to download");
    if (ParadiseClient.MISC_MOD.isClientOutdated) {
      this.addDrawableChild(new PressableTextWidget(this.width - this.textRenderer.getWidth(updateMessage1) - 2,
        this.height - 20,
        this.textRenderer.getWidth(updateMessage1),
        10,
        updateMessage1,
        (button) -> {
          Util.getOperatingSystem().open("https://paradise-client.net/downloads");
          MinecraftClient.getInstance().setScreen(new TitleScreen());
        },
        this.textRenderer));
    }

    // Adding a button to switch themes dynamically
    // This button toggles between "hack" and "particle" themes
    Theme currentTheme = ThemeRenderer.getTheme();

    this.addDrawableChild(ButtonWidget.builder(Text.literal("Theme: " + currentTheme.getName()), onPress -> {
      Theme[] themes = Theme.values();
      int nextOrdinal = (ThemeRenderer.getTheme().ordinal() + 1) % themes.length;
      Theme nextTheme = themes[nextOrdinal];
      ThemeRenderer.setTheme(nextTheme);
      onPress.setMessage(Text.literal("Theme: " + nextTheme.getName()));
    }).width(150).position(this.width / 2 - 75, this.height / 4 + 160).build());

  }

  /**
   * Renders the Title Screen with custom background and additional information. This method handles background fading
   * and custom text rendering.
   *
   * @param context The draw context used for rendering.
   * @param mouseX  The mouse X position.
   * @param mouseY  The mouse Y position.
   * @param delta   The delta time since the last frame.
   * @param ci      Callback information.
   */
  @Inject(method = "render", at = @At("HEAD"), cancellable = true) public void render(DrawContext context,
    int mouseX,
    int mouseY,
    float delta,
    CallbackInfo ci) {
    if (this.backgroundFadeStart == 0L && this.doBackgroundFade) {
      this.backgroundFadeStart = Util.getMeasuringTimeMs();
    }

    float f = 1.0F;
    if (this.doBackgroundFade) {
      float g = (float) (Util.getMeasuringTimeMs() - this.backgroundFadeStart) / 2000.0F;
      if (g > 1.0F) {
        this.doBackgroundFade = false;
        this.backgroundAlpha = 1.0F;
      } else {
        g = MathHelper.clamp(g, 0.0F, 1.0F);
        f = MathHelper.clampedMap(g, 0.5F, 1.0F, 0.0F, 1.0F);
        this.backgroundAlpha = MathHelper.clampedMap(g, 0.0F, 0.5F, 0.0F, 1.0F);
      }

      this.setWidgetAlpha(f);
    }

    this.renderPanoramaBackground(context, delta);
    int i = MathHelper.ceil(f * 255.0F) << 24;
    if ((i & -67108864) != 0) {
      super.render(context, mouseX, mouseY, delta);
      this.logoDrawer.draw(context, this.width, f);
      if (this.splashText != null) {
        if (!(Boolean) this.client.options.getHideSplashTexts().getValue()) {
          this.splashText.render(context, this.width, this.textRenderer, i);
        }
      }
      context.drawTextWithShadow(this.textRenderer, Constants.getWindowTitle(), 2, this.height - 10, 16777215 | i);
      if (this.isRealmsNotificationsGuiDisplayed() && f >= 1.0F) {
        this.realmsNotificationGui.render(context, mouseX, mouseY, delta);
      }
    }
    super.render(context, mouseX, mouseY, delta);
    ci.cancel();
  }

  /**
   * Sets the alpha value for widgets. This method is shadowed from the original TitleScreen class.
   *
   * @param alpha The alpha value to set.
   */
  @Shadow private void setWidgetAlpha(float alpha) {
  }

  /**
   * Checks if the Realms Notifications GUI is displayed. This method is shadowed from the original TitleScreen class.
   *
   * @return True if the Realms Notifications GUI is displayed, false otherwise.
   */
  @Shadow private boolean isRealmsNotificationsGuiDisplayed() {
    return false;
  }
}
