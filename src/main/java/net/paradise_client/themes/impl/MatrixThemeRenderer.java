package net.paradise_client.themes.impl;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.paradise_client.Helper;
import net.paradise_client.themes.AbstractThemeRenderer;

import java.util.Random;

/**
 * Matrix theme
 */
public class MatrixThemeRenderer extends AbstractThemeRenderer {
  private static final Random random = new Random();
  private static final int MAX_DROPS = 400;
  private static final MatrixDrop[] drops = new MatrixDrop[MAX_DROPS];
  private static final String MATRIX_CHARS = "ｱｲｳｴｵｶｷｸｹｺｻｼｽｾｿﾀﾁﾂﾃﾄﾅﾆﾇﾈﾉﾊﾋﾌﾍﾎﾏﾐﾑﾒﾓﾔﾕﾖﾗﾘﾙﾚﾛﾜﾝ01234567890Z:・.\"=*+-<>¦|ｰ";
  private static final int COLOR_MATRIX_BRIGHT = 0xFF00FF00;
  private static final int COLOR_MATRIX_MEDIUM = 0xFF00AA00;
  private static final int COLOR_MATRIX_DARK = 0xFF005500;
  private static final int COLOR_BG_BLACK = 0xFF000000;
  private static final int COLOR_BG_DARK_GREEN = 0xFF001100;
  private static final int COLOR_BG_MEDIUM_GREEN = 0xFF002200;
  private int animationFrame = 0;
  private int scanlinePosition = 0;

  static {
    for (int i = 0; i < MAX_DROPS; i++) {
      drops[i] = new MatrixDrop(i * 6);
    }
  }

  @Override public String getThemeName() {
    return "Matrix";
  }

  @Override public void renderBackground(DrawContext context, int width, int height) {
    context.fill(0, 0, width, height, COLOR_BG_BLACK);

    TextRenderer font = MinecraftClient.getInstance().textRenderer;

    for (int i = 0; i < drops.length; i++) {
      MatrixDrop drop = drops[i];
      int x = (i * 6) % width;

      for (int j = 0; j < drop.length; j++) {
        int y = (drop.y - j * 10) % height;
        if (y < 0) {
          y += height;
        }

        int alpha;
        int color;
        if (j == 0) {
          alpha = 255;
          color = 0xFFFFFF;
        } else if (j < 3) {
          alpha = 255 - (j * 40);
          color = COLOR_MATRIX_BRIGHT;
        } else if (j < 8) {
          alpha = 180 - (j * 20);
          color = COLOR_MATRIX_MEDIUM;
        } else {
          alpha = Math.max(20, 150 - (j * 15));
          color = COLOR_MATRIX_DARK;
        }

        int finalColor = (alpha << 24) | (color & 0xFFFFFF);

        context.drawText(font, String.valueOf(drop.chars[j]), x, y, finalColor, false);
      }
      drop.update(height);
    }

    scanlinePosition = (scanlinePosition + 2) % height;
    for (int y = 0; y < height; y += 4) {
      int scanAlpha = Math.abs((y - scanlinePosition) % height) < 2 ? 0x11 : 0x08;
      context.fill(0, y, width, y + 2, (scanAlpha << 24) | 0x00FF00);
    }

    if (random.nextDouble() < 0.05) {
      int glitchY = random.nextInt(height);
      int glitchHeight = random.nextInt(3) + 1;
      context.fill(0, glitchY, width, glitchY + glitchHeight, 0x22FFFFFF);
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

    int bgColor = pressed ? COLOR_BG_MEDIUM_GREEN : hovered ? COLOR_BG_DARK_GREEN : 0xDD000000;
    context.fill(x, y, x + width, y + height, bgColor);

    context.fill(x + 1, y + 1, x + width - 1, y + 2, 0x33000000);

    if (hovered) {
      float glitch = (float) Math.sin(animationFrame * 0.3) * 0.15f + 0.15f;
      int glitchColor = (int) (glitch * 255) << 24 | 0x00FF00;
      context.fill(x, y, x + width, y + height, glitchColor);

      if (random.nextDouble() < 0.1) {
        int glitchY = y + random.nextInt(height);
        context.fill(x, glitchY, x + width, glitchY + 1, 0x88FFFFFF);
      }
    }

    if (hovered) {
      context.fill(x - 1, y - 1, x + width + 1, y, 0x44FFFFFF & COLOR_MATRIX_BRIGHT);
      context.fill(x - 1, y + height, x + width + 1, y + height + 1, 0x44FFFFFF & COLOR_MATRIX_BRIGHT);
      context.fill(x - 1, y, x, y + height, 0x44FFFFFF & COLOR_MATRIX_BRIGHT);
      context.fill(x + width, y, x + width + 1, y + height, 0x44FFFFFF & COLOR_MATRIX_BRIGHT);
    }

    int borderColor = hovered ? COLOR_MATRIX_BRIGHT : COLOR_MATRIX_MEDIUM;
    drawMatrixBorder(context, x, y, width, height, borderColor);

    if (text != null && font != null) {
      int textX = x + (width - font.getWidth(text)) / 2;
      int textY = y + (height - font.fontHeight) / 2;
      context.drawText(font, text, textX + 1, textY + 1, 0x88000000, false);
      int textColor = pressed ? COLOR_MATRIX_MEDIUM : hovered ? 0xFFFFFFFF : COLOR_MATRIX_BRIGHT;
      context.drawText(font, text, textX, textY, textColor, false);

      if (hovered && random.nextDouble() < 0.05) {
        int glitchOffset = random.nextInt(3) - 1;
        context.drawText(font, text, textX + glitchOffset, textY, 0x44FF0000, false);
      }
    }
  }

  @Override public void renderHudPanel(DrawContext context, int x, int y, int width, int height) {
    context.fill(x, y, x + width, y + height, 0xCC000000);

    context.fill(x + 1, y + 1, x + width - 1, y + 2, 0x22FFFFFF & COLOR_MATRIX_DARK);

    int scanY = (animationFrame * 2) % height;
    context.fill(x + 2, y + scanY, x + width - 2, y + scanY + 1, 0x33FFFFFF & COLOR_MATRIX_MEDIUM);

    int cornerSize = 6;
    drawCornerAccent(context, x, y, cornerSize, true, true);
    drawCornerAccent(context, x + width - cornerSize, y, cornerSize, false, true);
    drawCornerAccent(context, x, y + height - cornerSize, cornerSize, true, false);
    drawCornerAccent(context, x + width - cornerSize, y + height - cornerSize, cornerSize, false, false);

    float pulse = (float) Math.sin(animationFrame * 0.05) * 0.1f + 0.3f;
    int glowAlpha = (int) (pulse * 255);
    int glowColor = (glowAlpha << 24) | (COLOR_MATRIX_BRIGHT & 0xFFFFFF);

    context.fill(x - 1, y - 1, x + width + 1, y, glowColor);
    context.fill(x - 1, y + height, x + width + 1, y + height + 1, glowColor);
    context.fill(x - 1, y, x, y + height, glowColor);
    context.fill(x + width, y, x + width + 1, y + height, glowColor);

    drawMatrixBorder(context, x, y, width, height, COLOR_MATRIX_BRIGHT);
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

    int bgColor = active ? COLOR_BG_MEDIUM_GREEN : COLOR_BG_DARK_GREEN;
    context.fill(x, y, x + width, y + height, 0xEE000000 | (bgColor & 0xFFFFFF));

    drawVerticalGradient(context, x, y, width, height / 2, 0x22FFFFFF & COLOR_MATRIX_DARK, 0x00FFFFFF);

    if (active) {
      float pulse = (float) Math.sin(animationFrame * 0.08) * 0.2f + 0.5f;
      int pulseAlpha = (int) (pulse * 255);
      int pulseColor = (pulseAlpha << 24) | (COLOR_MATRIX_BRIGHT & 0xFFFFFF);
      context.fill(x, y + height - 2, x + width, y + height, pulseColor);
    }

    if (title != null && font != null) {
      int textX = x + 8;
      int textY = y + (height - font.fontHeight) / 2;

      context.drawText(font, title, textX + 1, textY + 1, 0x88000000, false);
      context.drawText(font, title, textX, textY, active ? COLOR_MATRIX_BRIGHT : COLOR_MATRIX_MEDIUM, true);
    }

    drawMatrixBorder(context, x, y, width, height, active ? COLOR_MATRIX_BRIGHT : COLOR_MATRIX_DARK);
  }

  @Override public void renderTaskbar(DrawContext context, int screenWidth, int screenHeight, int height) {
    int y = screenHeight - height;

    context.fill(0, y, screenWidth, screenHeight, 0xEE000000);

    float pulse = (float) Math.sin(animationFrame * 0.06) * 0.3f + 0.5f;
    int pulseAlpha = (int) (pulse * 255);
    int pulseColor = (pulseAlpha << 24) | (COLOR_MATRIX_BRIGHT & 0xFFFFFF);

    context.fill(0, y - 1, screenWidth, y, pulseColor);
    context.fill(0, y, screenWidth, y + 2, COLOR_MATRIX_BRIGHT);

    for (int x = 0; x < screenWidth; x += 20) {
      if ((x + animationFrame) % 40 < 20) {
        context.fill(x, y + 1, x + 2, y + 3, COLOR_MATRIX_MEDIUM);
      }
    }
  }

  @Override public void renderPanel(DrawContext context, int x, int y, int width, int height) {
    context.fill(x, y, x + width, y + height, 0xCC000000);
    context.fill(x + 1, y + 1, x + width - 1, y + 2, 0x22FFFFFF & COLOR_MATRIX_DARK);
    drawMatrixBorder(context, x, y, width, height, COLOR_MATRIX_DARK);
  }

  @Override public void renderTextField(DrawContext context, int x, int y, int width, int height, boolean focused) {
    context.fill(x, y, x + width, y + height, COLOR_BG_DARK_GREEN);

    context.fill(x + 1, y + 1, x + width - 1, y + 2, 0x44000000);

    if (focused) {
      float pulse = (float) Math.sin(animationFrame * 0.1) * 0.2f + 0.4f;
      int glowAlpha = (int) (pulse * 255);
      int glowColor = (glowAlpha << 24) | (COLOR_MATRIX_BRIGHT & 0xFFFFFF);

      context.fill(x - 2, y - 2, x + width + 2, y - 1, glowColor);
      context.fill(x - 2, y + height + 1, x + width + 2, y + height + 2, glowColor);
      context.fill(x - 2, y - 1, x - 1, y + height + 1, glowColor);
      context.fill(x + width + 1, y - 1, x + width + 2, y + height + 1, glowColor);
    }

    int borderColor = focused ? COLOR_MATRIX_BRIGHT : COLOR_MATRIX_MEDIUM;
    drawMatrixBorder(context, x, y, width, height, borderColor);

    if (focused && (animationFrame / 10) % 2 == 0) {
      context.fill(x + 4, y + 3, x + 5, y + height - 3, COLOR_MATRIX_BRIGHT);
    }
  }

  @Override public void renderSelection(DrawContext context, int x, int y, int width, int height) {
    context.fill(x, y, x + width, y + height, 0x66FFFFFF & COLOR_MATRIX_BRIGHT);
    drawMatrixBorder(context, x, y, width, height, COLOR_MATRIX_BRIGHT);
  }

  @Override public void renderProgressBar(DrawContext context, int x, int y, int width, int height, float progress) {
    float clampedProgress = Math.max(0, Math.min(1, progress));
    int fillWidth = (int) (width * clampedProgress);

    context.fill(x, y, x + width, y + height, COLOR_BG_DARK_GREEN);

    if (fillWidth > 0) {
      drawHorizontalGradient(context, x, y, fillWidth, height, COLOR_MATRIX_MEDIUM, COLOR_MATRIX_BRIGHT);

      float shimmer = (float) Math.sin(animationFrame * 0.15 + progress * 10) * 0.3f + 0.3f;
      int shimmerAlpha = (int) (shimmer * 255);
      int shimmerColor = (shimmerAlpha << 24) | 0xFFFFFF;
      context.fill(x, y, x + fillWidth, y + height / 3, shimmerColor);

      int streamPos = (animationFrame * 3) % fillWidth;
      context.fill(x + streamPos, y, x + streamPos + 2, y + height, 0xAAFFFFFF);
    }

    drawMatrixBorder(context, x, y, width, height, COLOR_MATRIX_MEDIUM);
  }

  @Override
  public void renderSlider(DrawContext context, int x, int y, int width, int height, float value, boolean hovered) {
    float clampedValue = Math.max(0, Math.min(1, value));

    context.fill(x, y + height / 2 - 2, x + width, y + height / 2 + 2, COLOR_BG_MEDIUM_GREEN);

    int fillWidth = (int) (width * clampedValue);
    if (fillWidth > 0) {
      context.fill(x, y + height / 2 - 2, x + fillWidth, y + height / 2 + 2, COLOR_MATRIX_MEDIUM);
    }

    int thumbX = x + fillWidth - 6;
    int thumbY = y;

    if (hovered) {
      float pulse = (float) Math.sin(animationFrame * 0.12) * 0.2f + 0.4f;
      int glowAlpha = (int) (pulse * 255);
      int glowColor = (glowAlpha << 24) | (COLOR_MATRIX_BRIGHT & 0xFFFFFF);
      context.fill(thumbX - 2, thumbY - 2, thumbX + 14, thumbY + height + 2, glowColor);
    }

    context.fill(thumbX, thumbY, thumbX + 12, thumbY + height, hovered ? COLOR_MATRIX_BRIGHT : COLOR_MATRIX_MEDIUM);
    context.fill(thumbX + 1, thumbY + 1, thumbX + 11, thumbY + 2, 0x55FFFFFF);
    drawMatrixBorder(context, thumbX, thumbY, 12, height, COLOR_MATRIX_BRIGHT);
  }

  @Override public void renderCheckbox(DrawContext context, int x, int y, int size, boolean checked, boolean hovered) {
    context.fill(x, y, x + size, y + size, COLOR_BG_DARK_GREEN);

    if (hovered) {
      context.fill(x - 1, y - 1, x + size + 1, y + size + 1, 0x44FFFFFF & COLOR_MATRIX_BRIGHT);
    }

    if (checked) {
      int checkSize = size - 6;
      context.fill(x + 3, y + 3, x + 3 + checkSize, y + 3 + checkSize, COLOR_MATRIX_BRIGHT);

      float pulse = (float) Math.sin(animationFrame * 0.1) * 0.2f + 0.3f;
      int pulseAlpha = (int) (pulse * 255);
      int pulseColor = (pulseAlpha << 24) | 0xFFFFFF;
      context.fill(x + 3, y + 3, x + 3 + checkSize, y + 3 + checkSize, pulseColor);
    }

    int borderColor = hovered ? COLOR_MATRIX_BRIGHT : COLOR_MATRIX_MEDIUM;
    drawMatrixBorder(context, x, y, size, size, borderColor);
  }

  @Override public void renderSeparator(DrawContext context, int x, int y, int width) {
    drawHorizontalGradient(context, x, y, width / 3, 1, 0x00FFFFFF, COLOR_MATRIX_MEDIUM);
    context.fill(x + width / 3, y, x + 2 * width / 3, y + 1, COLOR_MATRIX_MEDIUM);
    drawHorizontalGradient(context, x + 2 * width / 3, y, width / 3, 1, COLOR_MATRIX_MEDIUM, 0x00FFFFFF);

    for (int i = 0; i < width; i += 10) {
      if ((i + animationFrame) % 20 < 10) {
        context.fill(x + i, y, x + i + 1, y + 1, COLOR_MATRIX_BRIGHT);
      }
    }
  }

  @Override public void update() {
    animationFrame++;
  }

  private void drawMatrixBorder(DrawContext context, int x, int y, int width, int height, int color) {
    context.fill(x, y, x + width, y + 1, color);
    context.fill(x, y + height - 1, x + width, y + height, color);
    context.fill(x, y, x + 1, y + height, color);
    context.fill(x + width - 1, y, x + width, y + height, color);
  }

  private void drawCornerAccent(DrawContext context, int x, int y, int size, boolean left, boolean top) {
    int color = COLOR_MATRIX_BRIGHT;

    if (left) {
      context.fill(x, y, x + size, y + 1, color);
      context.fill(x, y, x + 1, y + size, color);
    } else {
      context.fill(x, y, x + size, y + 1, color);
      context.fill(x + size - 1, y, x + size, y + size, color);
    }

    if (!top) {
      if (left) {
        context.fill(x, y + size - 1, x + size, y + size, color);
      } else {
        context.fill(x, y + size - 1, x + size, y + size, color);
      }
    }
  }

  private static class MatrixDrop {
    int y;
    int speed;
    int length;
    char[] chars;
    int resetDelay;

    MatrixDrop(int startY) {
      reset(startY);
    }

    void reset(int startY) {
      this.y = startY - random.nextInt(100);
      this.speed = random.nextInt(3) + 1;
      this.length = random.nextInt(15) + 10;
      this.chars = new char[length];
      this.resetDelay = random.nextInt(100);

      for (int i = 0; i < length; i++) {
        chars[i] = MATRIX_CHARS.charAt(random.nextInt(MATRIX_CHARS.length()));
      }
    }

    void update(int screenHeight) {
      y += speed;

      if (y > screenHeight + (length * 10)) {
        if (resetDelay <= 0) {
          reset(-length * 10);
        } else {
          resetDelay--;
        }
      }

      if (random.nextDouble() < 0.05) {
        chars[random.nextInt(length)] = MATRIX_CHARS.charAt(random.nextInt(MATRIX_CHARS.length()));
      }
    }
  }
}