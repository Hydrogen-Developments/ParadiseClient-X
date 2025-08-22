package net.paradise_client.ui.notification;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;

import java.util.ArrayList;
import java.util.List;

public class NotificationManager {
    private final List<Notification> notifications = new ArrayList<>();

    public void addNotification(Notification notification) {
        notifications.add(notification);
    }

    public void drawNotifications(DrawContext ctx, TextRenderer tr) {
        for (int i = 0; i < notifications.size(); i++) {
            Notification n = notifications.get(i);
            if (n.draw(ctx, tr, i)) {
                notifications.remove(i--);
            }
        }
    }
}
