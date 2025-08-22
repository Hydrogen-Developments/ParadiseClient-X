package net.paradise_client.ui.notification;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.math.MathHelper;

public class Notification {
    private static final int PADDING_X = 10;
    private static final int PADDING_Y = 6;
    private static final int MAX_WIDTH = 240;
    private static final int BAR_HEIGHT = 2;
    private static final int STACK_GAP = 6;

    private static final long SLIDE_MS = 500L;
    private static final long LIFETIME = 5_000L;

    private static final int ACCENT_RGB = 0x55A3FF;
    private static final int BG_RGB = 0x0B0B0B;
    private static final float BG_ALPHA = 0.9f;
    private static final float SHADOW_ALPHA = 0.4f;

    private final String title;
    private final String message;
    private final long startTime;

    public Notification(String title, String message) {
        this.title = title;
        this.message = message;
        this.startTime = System.currentTimeMillis();
    }

    private static float easeOutCubic(float t) {
        return (float) (1f - Math.pow(1f - t, 3));
    }

    private static int argb(float alpha, int rgb) {
        return ((int) (alpha * 255) << 24) | (rgb & 0x00FFFFFF);
    }

    public boolean draw(DrawContext ctx, TextRenderer tr, int slot) {
        long elapsed = System.currentTimeMillis() - startTime;

        float pLife = MathHelper.clamp(elapsed / (float) LIFETIME, 0f, 1f);
        float pSlideIn = easeOutCubic(MathHelper.clamp(elapsed / (float) SLIDE_MS, 0f, 1f));

        float alphaMul;
        if (elapsed < SLIDE_MS) {
            alphaMul = pSlideIn;               // fade‑in
        } else if (elapsed > LIFETIME - SLIDE_MS) {
            float pSlideOut = (elapsed - (LIFETIME - SLIDE_MS)) / (float) SLIDE_MS;
            alphaMul = 1f - easeOutCubic(pSlideOut); // fade‑out
        } else {
            alphaMul = 1f;
        }

        int titleW = tr.getWidth(title);
        int msgW = tr.getWidth(message);
        int width = Math.min(Math.max(titleW, msgW) + PADDING_X * 2, MAX_WIDTH);
        int height = tr.fontHeight * 2 + PADDING_Y * 3 + BAR_HEIGHT;

        int screenW = ctx.getScaledWindowWidth();
        int screenH = ctx.getScaledWindowHeight();

        @SuppressWarnings("")
        int startX = screenW;
        int endX = screenW - width - 8;
        int x;
        if (elapsed < SLIDE_MS) {
            x = (int) (endX + (startX - endX) * (1f - pSlideIn));
        } else if (elapsed > LIFETIME - SLIDE_MS) {
            float pSlideOut = easeOutCubic((elapsed - (LIFETIME - SLIDE_MS)) / (float) SLIDE_MS);
            x = (int) (endX + (startX - endX) * pSlideOut);
        } else {
            x = endX;
        }

        float stackTargetY = screenH - height - 16 - slot * (height + STACK_GAP);
        float baseY;
        if (elapsed < SLIDE_MS) {
            float yOffset = 12 * (1f - easeOutCubic(pSlideIn));
            baseY = stackTargetY - yOffset;
        } else {
            baseY = stackTargetY;
        }
        int y = (int) baseY;

        int bg = argb(BG_ALPHA * alphaMul, BG_RGB);
        int border = argb(alphaMul, ACCENT_RGB);
        int shadow = argb(SHADOW_ALPHA * alphaMul, 0x000000);

        ctx.fill(x + 2, y + 2, x + width + 2, y + height + 2, shadow);

        ctx.fill(x, y, x + width, y + height - BAR_HEIGHT, bg);
        ctx.drawBorder(x, y, width, height - BAR_HEIGHT, border);

        int txtClr = argb(alphaMul, 0xFFFFFF);
        ctx.drawText(tr, title, x + PADDING_X, y + PADDING_Y, txtClr, false);
        ctx.drawText(tr, message, x + PADDING_X, y + PADDING_Y + tr.fontHeight + 2, txtClr, false);

        float barFrac = 1f - pLife;
        int barW = (int) (width * barFrac);
        int barBg = argb(0.25f * alphaMul, ACCENT_RGB);
        int barFg = argb(alphaMul, ACCENT_RGB);
        int barY0 = y + height - BAR_HEIGHT;
        int barY1 = y + height;
        ctx.fill(x, barY0, x + width, barY1, barBg);
        ctx.fill(x, barY0, x + barW, barY1, barFg);

        return pLife >= 1f;
    }
}
