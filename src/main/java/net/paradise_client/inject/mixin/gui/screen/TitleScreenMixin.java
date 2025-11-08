package net.paradise_client.inject.mixin.gui.screen;

import net.minecraft.client.MinecraftClient;
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
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
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

  private ButtonWidget quitButton;

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

    int quitButtonSize = 24;
    int quitX = this.width - quitButtonSize - 18;
    int quitY = 18; 
    quitButton = ButtonWidget.builder(Text.literal("Quit"),
                    b -> {
                      client.scheduleStop();
                    })
            .dimensions(quitX, quitY, quitButtonSize, quitButtonSize)
            .build();
    this.addDrawableChild(quitButton);

    int iconSize = 20;
    int padding = 8;
    int bottomY = this.height / 2 + 100;
    int toolbarWidth = 3 * (iconSize + padding) - padding;
    int startX = (this.width - toolbarWidth) / 2;

    this.addDrawableChild(ButtonWidget.builder(Text.literal("Options"),
                    b -> client.setScreen(new OptionsScreen(this, client.options)))
            .dimensions(startX, bottomY, iconSize, iconSize).build());

    this.addDrawableChild(ButtonWidget.builder(Text.literal("Accessibility Settings"),
                    b -> client.setScreen(new AccessibilityOptionsScreen(this, client.options)))
            .dimensions(startX + iconSize + padding, bottomY, iconSize, iconSize).build());

    this.addDrawableChild(ButtonWidget.builder(Text.literal("Realms"),
                    b -> client.setScreen(new RealmsMainScreen(this)))
            .dimensions(startX + 2 * (iconSize + padding), bottomY, iconSize, iconSize).build());
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

    this.renderPanoramaBackground(context, delta);
    
    context.fill(0, 0, this.width, this.height, 0x66000000);
    
    int sunX = 0;
    int sunY = 0;
    int sunSize = 300;
    for (int i = 0; i < 5; i++) {
      int glowAlpha = 20 - (i * 4);
      int glowColor = (glowAlpha << 24) | 0xFFFFAA;
      fillRoundedRect(context, sunX - sunSize / 2 + i * 20, sunY - sunSize / 2 + i * 20, 
              sunSize - i * 40, sunSize - i * 40, sunSize / 2, glowColor);
    }


    int logoWidth = 100;
    int logoHeight = 100;
    int logoX = (this.width - logoWidth) / 2;
    int logoY = this.height / 2 - 120;

    context.drawTexture(RenderLayer::getGuiTextured,
            logoImage,
            logoX,
            logoY,
            0.0F,
            0.0F,
            logoWidth,
            logoHeight,
            logoWidth,
            logoHeight);

    TextRenderer font = this.textRenderer;
    String title = "PARADISE CLIENT";
    float scale = 1.15f;
    int titleWidth = (int) (font.getWidth(title) * scale);
    int titleY = logoY + logoHeight + 10;
    int titleX = (this.width - titleWidth) / 2;

    context.getMatrices().push();
    context.getMatrices().translate(titleX, titleY, 0);
    context.getMatrices().scale(scale, scale, 1.0f);
    
    context.drawTextWithShadow(font, title, 0, 0, 0xFFFFFFFF);
    
    context.getMatrices().pop();

    this.renderCustomButtons(context, mouseX, mouseY);

    this.renderQuitButton(context, mouseX, mouseY);

    this.renderBottomToolbar(context, mouseX, mouseY);

    String versionText = "Paradise Client " + Constants.VERSION;
    context.drawText(font, versionText, 8, this.height - font.fontHeight - 8, 0x88FFFFFF, false);

    String disclaimer = "Not affiliated with Mojang or Microsoft. Do not distribute!";
    int disclaimerWidth = font.getWidth(disclaimer);
    context.drawText(font, disclaimer, this.width - disclaimerWidth - 8, 
            this.height - font.fontHeight - 8, 0x88FFFFFF, false);


    ci.cancel();
  }


  private void renderCustomButtons(DrawContext context, int mouseX, int mouseY) {
    TextRenderer font = this.textRenderer;
    
    for (Element element : this.children()) {
      if (element instanceof ButtonWidget button) {
        String label = button.getMessage().getString();
        if (label.equals("Singleplayer") || label.equals("Multiplayer") || label.equals("Website")) {
          int x = button.getX();
          int y = button.getY();
          int width = button.getWidth();
          int height = button.getHeight();
          boolean hovered = button.isHovered();
          
          int cornerRadius = 8; 
          
          int baseColor;
          if (label.equals("Website")) {
            baseColor = hovered ? 0xAA2A5AC2 : 0xAA3A7AE2;
          } else {
            baseColor = hovered ? 0xAA1A1A1A : 0xAA222222; 
          }
          
          fillRoundedRect(context, x, y, width, height, cornerRadius, baseColor);
          
          drawVerticalGradient(context, x, y, width, height / 2, 
                  0x22FFFFFF, 0x00000000);
          
          if (hovered) {
            int glowColor = 0x22FFFFFF;
            fillRoundedRect(context, x - 2, y - 2, width + 4, height + 4, cornerRadius + 2, glowColor);
            fillRoundedRect(context, x, y, width, height, cornerRadius, baseColor);
          }
          
          drawRoundedBorder(context, x, y, width, height, cornerRadius, 0x33FFFFFF, 1);
          
          int textX = x + (width - font.getWidth(label)) / 2;
          int textY = y + (height - font.fontHeight) / 2;
          context.drawText(font, label, textX, textY, 0xFFFFFF, false);
        }
      }
    }
  }

  private void renderQuitButton(DrawContext context, int mouseX, int mouseY) {
    if (quitButton == null) return;
    
    TextRenderer font = this.textRenderer;
    int x = quitButton.getX();
    int y = quitButton.getY() + 20;
    int size = quitButton.getWidth();
    boolean hovered = quitButton.isHovered();
    
    int baseColor = hovered ? 0xAA444444 : 0xAA333333;
    fillCircle(context, x + size / 2, y + size / 2, size / 2, baseColor);
    
    if (hovered) {
        int glowColor = 0x22FFFFFF;
        fillCircle(context, x + size / 2, y + size / 2, size / 2 + 2, glowColor);
        fillCircle(context, x + size / 2, y + size / 2, size / 2, baseColor);
    }
    
    int centerX = x + size / 2;
    int centerY = y + size / 2;
    int lineLength = size / 3;
    int lineThickness = 2;
    
    drawLine(context, centerX - lineLength / 2, centerY - lineLength / 2,
            centerX + lineLength / 2, centerY + lineLength / 2, 0xFFFFFFFF, lineThickness);
    drawLine(context, centerX + lineLength / 2, centerY - lineLength / 2,
            centerX - lineLength / 2, centerY + lineLength / 2, 0xFFFFFFFF, lineThickness);
    
    if (hovered) {
        String label = "Quit";
        int textX = x + (size - font.getWidth(label)) / 2;
        int textY = y + size + 4;
        fillRoundedRect(context, textX - 4, textY - 2, font.getWidth(label) + 8, 
                font.fontHeight + 4, 4, 0xDD000000);
        context.drawText(font, label, textX, textY, 0xFFFFFF, false);
    }
}


  private void renderBottomToolbar(DrawContext context, int mouseX, int mouseY) {
    int iconSize = 20;
    
    List<ButtonWidget> toolbarButtons = new ArrayList<>();
    for (Element element : this.children()) {
      if (element instanceof ButtonWidget button) {
        String label = button.getMessage().getString();
        if (label.equals("Options") || label.equals("Accessibility Settings") || label.equals("Realms")) {
          toolbarButtons.add(button);
        }
      }
    }
    
    int totalIcons = toolbarButtons.size();
    if (totalIcons == 0) return;
    
    TextRenderer font = this.textRenderer;
    
    for (int i = 0; i < toolbarButtons.size(); i++) {
      ButtonWidget button = toolbarButtons.get(i);
      int x = button.getX();
      int y = button.getY();
      boolean hovered = button.isHovered();
      String label = button.getMessage().getString();
      
      int bgColor = hovered ? 0xAA444444 : 0xAA333333;
      fillCircle(context, x + iconSize / 2, y + iconSize / 2, iconSize / 2, bgColor);
      
      if (hovered) {
        int glowColor = 0x22FFFFFF;
        fillCircle(context, x + iconSize / 2, y + iconSize / 2, iconSize / 2 + 1, glowColor);
        fillCircle(context, x + iconSize / 2, y + iconSize / 2, iconSize / 2, bgColor);
      }
      
      int centerX = x + iconSize / 2;
      int centerY = y + iconSize / 2;
      int iconColor = 0xFFFFFFFF;
      
      if (label.equals("Options")) {
        drawGearIcon(context, centerX, centerY, iconSize / 3, iconColor);
      } else if (label.equals("Accessibility Settings")) {
        drawAccessibilityIcon(context, centerX, centerY, iconSize / 3, iconColor);
      } else if (label.equals("Realms")) {
        drawRealmsIcon(context, centerX, centerY, iconSize / 3, iconColor);
      }
      
      if (hovered) {
        int textX = x + (iconSize - font.getWidth(label)) / 2;
        int textY = y - font.fontHeight - 4;
        fillRoundedRect(context, textX - 4, textY - 2, font.getWidth(label) + 8, 
                font.fontHeight + 4, 4, 0xDD000000);
        context.drawText(font, label, textX, textY, 0xFFFFFF, false);
      }
    }
  }

  private void drawGearIcon(DrawContext context, int cx, int cy, int size, int color) {
    int radius = size;
    for (int angle = 0; angle < 360; angle += 10) {
      double rad = Math.toRadians(angle);
      int px = (int) (cx + Math.cos(rad) * radius);
      int py = (int) (cy + Math.sin(rad) * radius);
      context.fill(px - 1, py - 1, px + 1, py + 1, color);
    }
    int innerRadius = radius / 2;
    for (int angle = 0; angle < 360; angle += 10) {
      double rad = Math.toRadians(angle);
      int px = (int) (cx + Math.cos(rad) * innerRadius);
      int py = (int) (cy + Math.sin(rad) * innerRadius);
      context.fill(px - 1, py - 1, px + 1, py + 1, color);
    }
    context.fill(cx - 1, cy - 1, cx + 1, cy + 1, color);
  }

  private void drawAccessibilityIcon(DrawContext context, int cx, int cy, int size, int color) {
    int radius = size;
    for (int angle = 0; angle < 360; angle += 10) {
      double rad = Math.toRadians(angle);
      int px = (int) (cx + Math.cos(rad) * (radius / 3));
      int py = (int) (cy - radius / 2 + Math.sin(rad) * (radius / 3));
      context.fill(px - 1, py - 1, px + 1, py + 1, color);
    }
    context.fill(cx - 1, cy - radius / 3, cx + 1, cy + radius / 2, color);
    context.fill(cx - radius / 2, cy, cx + radius / 2, cy + 1, color);
    context.fill(cx - 2, cy + radius / 2, cx, cy + radius, color);
    context.fill(cx, cy + radius / 2, cx + 2, cy + radius, color);
  }

  private void drawRealmsIcon(DrawContext context, int cx, int cy, int size, int color) {
    int radius = size;
    context.fill(cx - radius / 2, cy + radius / 3, cx + radius / 2, cy + radius / 2, color);
    int[] pointsX = {cx - radius / 2, cx - radius / 4, cx, cx + radius / 4, cx + radius / 2};
    int[] pointsY = {cy + radius / 3, cy - radius / 3, cy - radius / 2, cy - radius / 3, cy + radius / 3};
    for (int i = 0; i < pointsX.length - 1; i++) {
      drawLine(context, pointsX[i], pointsY[i], pointsX[i + 1], pointsY[i + 1], color, 1);
    }
    context.fill(cx - 1, cy - radius / 2, cx + 1, cy - radius / 2 + 2, color);
  }

  private void fillRoundedRect(DrawContext context, int x, int y, int width, int height, int radius, int color) {
    if (radius <= 0 || width <= 0 || height <= 0) {
      context.fill(x, y, x + width, y + height, color);
      return;
    }
    
    radius = Math.min(radius, Math.min(width / 2, height / 2));
    
    context.fill(x + radius, y, x + width - radius, y + height, color);
    context.fill(x, y + radius, x + radius, y + height - radius, color);
    context.fill(x + width - radius, y + radius, x + width, y + height - radius, color);
    
    fillCircleQuarter(context, x + radius, y + radius, radius, color, 0); 
    fillCircleQuarter(context, x + width - radius, y + radius, radius, color, 1); 
    fillCircleQuarter(context, x + radius, y + height - radius, radius, color, 2);
    fillCircleQuarter(context, x + width - radius, y + height - radius, radius, color, 3);
  }

  private void fillCircleQuarter(DrawContext context, int cx, int cy, int radius, int color, int quadrant) {
    int radiusSq = radius * radius;
    for (int dy = -radius; dy <= radius; dy++) {
      for (int dx = -radius; dx <= radius; dx++) {
        int distSq = dx * dx + dy * dy;
        if (distSq <= radiusSq) {
          boolean inQuadrant = false;
          switch (quadrant) {
            case 0: inQuadrant = (dx <= 0 && dy <= 0); break; 
            case 1: inQuadrant = (dx >= 0 && dy <= 0); break; 
            case 2: inQuadrant = (dx <= 0 && dy >= 0); break;
            case 3: inQuadrant = (dx >= 0 && dy >= 0); break;
          }
          if (inQuadrant) {
            context.fill(cx + dx, cy + dy, cx + dx + 1, cy + dy + 1, color);
          }
        }
      }
    }
  }

  private void drawRoundedBorder(DrawContext context, int x, int y, int width, int height, int radius, int color, int thickness) {
    if (radius <= 0 || width <= 0 || height <= 0) return;
    
    radius = Math.min(radius, Math.min(width / 2, height / 2));
    
    for (int i = 0; i < thickness; i++) {
      context.fill(x + radius, y + i, x + width - radius, y + i + 1, color);
      context.fill(x + radius, y + height - i - 1, x + width - radius, y + height - i, color);
      context.fill(x + i, y + radius, x + i + 1, y + height - radius, color);
      context.fill(x + width - i - 1, y + radius, x + width - i, y + height - radius, color);
    }
  }

  private void drawVerticalGradient(DrawContext context, int x, int y, int width, int height, int colorTop, int colorBottom) {
    for (int i = 0; i < height; i++) {
      float t = (float) i / height;
      int r1 = (colorTop >> 16) & 0xFF;
      int g1 = (colorTop >> 8) & 0xFF;
      int b1 = colorTop & 0xFF;
      int a1 = (colorTop >> 24) & 0xFF;
      
      int r2 = (colorBottom >> 16) & 0xFF;
      int g2 = (colorBottom >> 8) & 0xFF;
      int b2 = colorBottom & 0xFF;
      int a2 = (colorBottom >> 24) & 0xFF;
      
      int r = (int) (r1 + (r2 - r1) * t);
      int g = (int) (g1 + (g2 - g1) * t);
      int b = (int) (b1 + (b2 - b1) * t);
      int a = (int) (a1 + (a2 - a1) * t);
      
      int color = (a << 24) | (r << 16) | (g << 8) | b;
      context.fill(x, y + i, x + width, y + i + 1, color);
    }
  }

  private void fillCircle(DrawContext context, int cx, int cy, int radius, int color) {
    for (int dy = -radius; dy <= radius; dy++) {
      for (int dx = -radius; dx <= radius; dx++) {
        if (dx * dx + dy * dy <= radius * radius) {
          context.fill(cx + dx, cy + dy, cx + dx + 1, cy + dy + 1, color);
        }
      }
    }
  }

  private void drawLine(DrawContext context, int x1, int y1, int x2, int y2, int color, int thickness) {
    int dx = Math.abs(x2 - x1);
    int dy = Math.abs(y2 - y1);
    int sx = x1 < x2 ? 1 : -1;
    int sy = y1 < y2 ? 1 : -1;
    int err = dx - dy;
    
    int x = x1;
    int y = y1;
    
    while (true) {
      for (int i = -thickness / 2; i <= thickness / 2; i++) {
        for (int j = -thickness / 2; j <= thickness / 2; j++) {
          context.fill(x + i, y + j, x + i + 1, y + j + 1, color);
        }
      }
      
      if (x == x2 && y == y2) break;
      
      int e2 = 2 * err;
      if (e2 > -dy) {
        err -= dy;
        x += sx;
      }
      if (e2 < dx) {
        err += dx;
        y += sy;
      }
    }
  }
}