package net.paradise_client.inject.mixin.gui.screen;

import net.minecraft.client.MinecraftClient;
import net.paradise_client.themes.Theme;
import net.paradise_client.themes.ThemeManager;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.*;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.gui.screen.option.AccessibilityOptionsScreen;
import net.minecraft.client.gui.screen.option.OptionsScreen;
import net.minecraft.client.gui.screen.world.SelectWorldScreen;
import net.minecraft.client.realms.gui.screen.RealmsMainScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import net.paradise_client.Constants;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Mixin(ButtonWidget.class)
interface ButtonWidgetAccessor {
  @Accessor("onPress")
  ButtonWidget.PressAction getOnPress();
}

@Mixin(TitleScreen.class)
public abstract class TitleScreenMixin extends Screen {

  @Shadow private boolean doBackgroundFade;
  @Shadow private long backgroundFadeStart;
  @Shadow private float backgroundAlpha;
  @Shadow protected abstract void renderPanoramaBackground(DrawContext context, float deltaTicks);

  private final MinecraftClient client = MinecraftClient.getInstance();
  private final Identifier logoImage = Identifier.of(Constants.MOD_ID, "textures/icon/icon.png");
  private final Identifier optionsIcon = Identifier.of(Constants.MOD_ID, "textures/icon/options.png");
  private final Identifier accessibilityIcon = Identifier.of(Constants.MOD_ID, "textures/icon/accessibility.png");
  private final Identifier realmsIcon = Identifier.of(Constants.MOD_ID, "textures/icon/realms.png");

  private ButtonWidget quitButton;
  private ButtonWidget optionsButton;
  private ButtonWidget accessibilityButton;
  private ButtonWidget realmsButton;

  protected TitleScreenMixin(Text title) {
    super(title);
  }

  @Inject(method = "init", at = @At("TAIL"))
  private void initParadise(CallbackInfo ci) {
    List<Element> toRemove = new ArrayList<>();
    for (Element element : this.children()) {
      if (element instanceof ButtonWidget) {
        toRemove.add(element);
      }
    }
    for (Element element : toRemove) {
      this.remove(element);
    }

    quitButton = null;

    int buttonWidth = 200;
    int buttonHeight = 20;
    int spacing = 6;

    int logoHeight = 100;
    int logoY = this.height / 2 - 120;
    int titleY = logoY + logoHeight + 10;
    int titleHeight = this.textRenderer.fontHeight;
    int centerY = titleY + titleHeight + 20;
    int centerX = this.width / 2 - buttonWidth / 2;

    this.addDrawableChild(ButtonWidget.builder(Text.literal("Singleplayer"),
                    b -> client.setScreen(new SelectWorldScreen(this)))
            .dimensions(centerX, centerY, buttonWidth, buttonHeight).build());

    this.addDrawableChild(ButtonWidget.builder(Text.literal("Multiplayer"),
                    b -> client.setScreen(new MultiplayerScreen(this)))
            .dimensions(centerX, centerY + buttonHeight + spacing, buttonWidth, buttonHeight).build());

    this.addDrawableChild(ButtonWidget.builder(Text.literal("Website"),
                    b -> Util.getOperatingSystem().open("https://paradise-client.net"))
            .dimensions(centerX, centerY + 2 * (buttonHeight + spacing), buttonWidth, buttonHeight).build());

    int quitButtonWidth = 60;
    int quitButtonHeight = 20;
    int quitX = this.width - quitButtonWidth - 18;
    int quitY = 18;
    quitButton = ButtonWidget.builder(Text.literal("Quit"),
                    b -> {
                      client.scheduleStop();
                    })
            .dimensions(quitX, quitY, quitButtonWidth, quitButtonHeight)
            .build();
    this.addDrawableChild(quitButton);

    TextRenderer font = this.textRenderer;
    int iconSize = 20;
    int lastMainY = centerY + 2 * (buttonHeight + spacing);
    int bottomY = lastMainY + buttonHeight + 20;
    int toolbarSpacing = 4;
    int toolbarStartX = (this.width - (3 * iconSize + 2 * toolbarSpacing)) / 2;

    optionsButton = ButtonWidget.builder(Text.literal("Options"),
                    b -> client.setScreen(new OptionsScreen(this, client.options)))
            .dimensions(toolbarStartX, bottomY, iconSize, iconSize).build();
    this.addDrawableChild(optionsButton);

    accessibilityButton = ButtonWidget.builder(Text.literal("Accessibility Settings"),
                    b -> client.setScreen(new AccessibilityOptionsScreen(this, client.options)))
            .dimensions(toolbarStartX + iconSize + toolbarSpacing, bottomY, iconSize, iconSize).build();
    this.addDrawableChild(accessibilityButton);

    realmsButton = ButtonWidget.builder(Text.literal("Realms"),
                    b -> client.setScreen(new RealmsMainScreen(this)))
            .dimensions(toolbarStartX + 2 * (iconSize + toolbarSpacing), bottomY, iconSize, iconSize).build();
    this.addDrawableChild(realmsButton);

    int themeButtonWidth = 100;
    int themeButtonHeight = 20;
    int themeX = 18;
    int themeY = 18;
    this.addDrawableChild(ButtonWidget.builder(Text.literal("Change Theme"),
                    b -> {
                      Theme[] themes = Theme.values();
                      int currentIndex = Arrays.asList(themes).indexOf(ThemeManager.getTheme());
                      int nextIndex = (currentIndex + 1) % themes.length;
                      ThemeManager.setTheme(themes[nextIndex]);
                    })
            .dimensions(themeX, themeY, themeButtonWidth, themeButtonHeight).build());
  }

  @Inject(method = "render", at = @At("HEAD"), cancellable = true)
  private void renderParadise(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
    if (this.backgroundFadeStart == 0L && this.doBackgroundFade) {
      this.backgroundFadeStart = Util.getMeasuringTimeMs();
    }

    if (this.doBackgroundFade) {
      float t = (float)(Util.getMeasuringTimeMs() - this.backgroundFadeStart) / 2000.0F;
      if (t > 1.0F) {
        this.doBackgroundFade = false;
        this.backgroundAlpha = 1.0F;
      } else {
        t = MathHelper.clamp(t, 0.0F, 1.0F);
        this.backgroundAlpha = MathHelper.clampedMap(t, 0.0F, 0.5F, 0.0F, 1.0F);
      }
    }

    ThemeManager.update();
    ThemeManager.renderBackground(context, this.width, this.height);

    int logoWidth = 100;
    int logoHeight = 100;
    int logoX = (this.width - logoWidth) / 2;
    int logoY = this.height / 2 - 120;
    context.drawTexture(RenderLayer::getGuiTextured, logoImage, logoX, logoY, 0.0F, 0.0F, logoWidth, logoHeight, logoWidth, logoHeight);

    TextRenderer font = this.textRenderer;

    for (Element element : this.children()) {
      if (element instanceof ButtonWidget button) {
        boolean hovered = button.isMouseOver(mouseX, mouseY);
        boolean pressed = hovered && (GLFW.glfwGetMouseButton(client.getWindow().getHandle(), GLFW.GLFW_MOUSE_BUTTON_1) == GLFW.GLFW_PRESS);
        String text = button.getMessage().getString();
        if (button == optionsButton || button == accessibilityButton || button == realmsButton) {
          text = "";
        }
        ThemeManager.renderButton(context,
                button.getX(),
                button.getY(),
                button.getWidth(),
                button.getHeight(),
                hovered,
                pressed,
                text,
                font);

        if (button == optionsButton) {
          int iconX = button.getX() + (button.getWidth() - 16) / 2;
          int iconY = button.getY() + (button.getHeight() - 16) / 2;
          context.drawTexture(RenderLayer::getGuiTextured, optionsIcon, iconX, iconY, 0.0F, 0.0F, 16, 16, 16, 16);
        } else if (button == accessibilityButton) {
          int iconX = button.getX() + (button.getWidth() - 16) / 2;
          int iconY = button.getY() + (button.getHeight() - 16) / 2;
          context.drawTexture(RenderLayer::getGuiTextured, accessibilityIcon, iconX, iconY, 0.0F, 0.0F, 16, 16, 16, 16);
        } else if (button == realmsButton) {
          int iconX = button.getX() + (button.getWidth() - 16) / 2;
          int iconY = button.getY() + (button.getHeight() - 16) / 2;
          context.drawTexture(RenderLayer::getGuiTextured, realmsIcon, iconX, iconY, 0.0F, 0.0F, 16, 16, 16, 16);
        }
      }
    }

    String versionText = "ParadiseClient " + Constants.VERSION;
    context.drawText(font, versionText, 8, this.height - font.fontHeight - 8, 0x88FFFFFF, false);

    String disclaimer = "Not affiliated with Mojang or Microsoft. Do not distribute!";
    int disclaimerWidth = font.getWidth(disclaimer);
    context.drawText(font, disclaimer, this.width - disclaimerWidth - 8,
            this.height - font.fontHeight - 8, 0x88FFFFFF, false);

    ci.cancel();
  }
}