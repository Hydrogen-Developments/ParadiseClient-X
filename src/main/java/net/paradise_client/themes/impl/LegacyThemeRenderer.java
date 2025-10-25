package net.paradise_client.themes.impl;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.ColorHelper;
import net.paradise_client.Constants;
import net.paradise_client.themes.AbstractThemeRenderer;

public class LegacyThemeRenderer extends AbstractThemeRenderer {

  @Override public String getThemeName() {
    return "Legacy";
  }

  @Override public void renderBackground(DrawContext context, int width, int height) {
    try {
      context.drawTexture(RenderLayer::getGuiTextured,
        Identifier.of(Constants.MOD_ID, "textures/wallpaper/wallpaper.png"),
        0,
        0,
        0.0F,
        0.0F,
        width,
        height,
        width,
        height,
        ColorHelper.getWhite(1.0F));
    } catch (Exception e) {
      context.fill(0, 0, width, height, 0xFF2C2C2C);
    }
  }

  @Override
  public void renderButton(DrawContext context,
    int x,
    int y,
    int width,
    int height,
    boolean hovered,
    boolean pressed,
    String text,
    TextRenderer font) {
    int bgColor = pressed ? 0xCC1C1C1C : hovered ? 0xCC3C3C3C : 0xCC2C2C2C;
    context.fill(x, y, x + width, y + height, bgColor);
    context.drawBorder(x, y, width, height, hovered ? 0xFF5C5C5C : 0xFF4C4C4C);

    if (text != null && font != null) {
      int textX = x + (width - font.getWidth(text)) / 2;
      int textY = y + (height - 8) / 2;
      context.drawText(font, text, textX, textY, 0xFFFFFFFF, true);
    }
  }

  @Override public void renderHudPanel(DrawContext context, int x, int y, int width, int height) {
    context.fill(x, y, x + width, y + height, 0xCC2C2C2C);
    context.drawBorder(x, y, width, height, 0xFF4C4C4C);
  }

  @Override
  public void renderTitleBar(DrawContext context,
    int x,
    int y,
    int width,
    int height,
    String title,
    TextRenderer font,
    boolean active) {
    context.fill(x, y, x + width, y + height, 0xDD1C1C1C);
    if (title != null && font != null) {
      context.drawText(font, title, x + 8, y + (height - 8) / 2, 0xFFFFFFFF, true);
    }
  }

  @Override public void renderTaskbar(DrawContext context, int screenWidth, int screenHeight, int height) {
    int y = screenHeight - height;
    context.fill(0, y, screenWidth, y + height, 0xDD1C1C1C);
  }

  @Override public void renderPanel(DrawContext context, int x, int y, int width, int height) {
    context.fill(x, y, x + width, y + height, 0xCC2C2C2C);
  }

  @Override public void renderTextField(DrawContext context, int x, int y, int width, int height, boolean focused) {
    context.fill(x, y, x + width, y + height, 0xFF1C1C1C);
    context.drawBorder(x, y, width, height, focused ? 0xFF6C6C6C : 0xFF4C4C4C);
  }

  @Override public void renderSelection(DrawContext context, int x, int y, int width, int height) {
    context.fill(x, y, x + width, y + height, 0x884C4C4C);
  }

  @Override public void renderProgressBar(DrawContext context, int x, int y, int width, int height, float progress) {
    context.fill(x, y, x + width, y + height, 0xFF1C1C1C);
    int fillWidth = (int) (width * progress);
    context.fill(x, y, x + fillWidth, y + height, 0xFF4C4C4C);
    context.drawBorder(x, y, width, height, 0xFF3C3C3C);
  }

  @Override
  public void renderSlider(DrawContext context, int x, int y, int width, int height, float value, boolean hovered) {
    context.fill(x, y + height / 2 - 1, x + width, y + height / 2 + 1, 0xFF2C2C2C);
    int thumbX = x + (int) ((width - 8) * value);
    renderButton(context, thumbX, y, 8, height, hovered, false, null, null);
  }

  @Override public void renderCheckbox(DrawContext context, int x, int y, int size, boolean checked, boolean hovered) {
    context.fill(x, y, x + size, y + size, 0xFF1C1C1C);
    context.drawBorder(x, y, size, size, 0xFF4C4C4C);
    if (checked) {
      context.fill(x + 2, y + 2, x + size - 2, y + size - 2, 0xFF6C6C6C);
    }
  }

  @Override public void renderSeparator(DrawContext context, int x, int y, int width) {
    context.fill(x, y, x + width, y + 1, 0xFF4C4C4C);
  }
}