package ac.unindra.roemah_duren_spring.util;

import atlantafx.base.controls.Notification;
import atlantafx.base.theme.Styles;
import atlantafx.base.util.Animations;
import javafx.animation.PauseTransition;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.util.Duration;
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.material2.Material2AL;

public class NotificationUtil {
    private static void showNotification(Pane main, String messageText, Material2AL iconType, String styleClass) {
        FontIcon icon = new FontIcon(iconType);
        Notification notification = new Notification(messageText, icon);
        notification.getStyleClass().addAll(styleClass, Styles.ELEVATED_1);
        notification.setPrefHeight(Region.USE_PREF_SIZE);
        notification.setMaxHeight(Region.USE_PREF_SIZE);

        AnchorPane.setTopAnchor(notification, 20.0);
        AnchorPane.setRightAnchor(notification, 20.0);

        notification.setOnClose(event -> closeNotification(main, notification));

        FXMLUtil.updateUI(() -> {
            if (!main.getChildren().contains(notification)) {
                main.getChildren().add(notification);
                var in = Animations.slideInRight(notification, Duration.millis(500));
                in.playFromStart();

                PauseTransition delay = new PauseTransition(Duration.seconds(3));
                delay.setOnFinished(e -> closeNotification(main, notification));
                delay.play();
            }
        });
    }

    private static void closeNotification(Pane main, Notification notification) {
        FXMLUtil.updateUI(() -> {
            var out = Animations.slideOutUp(notification, Duration.millis(500));
            out.setOnFinished(e -> main.getChildren().remove(notification));
            out.playFromStart();
        });
    }

    public static void showNotificationSuccess(Pane pane, String message) {
        showNotification(pane, message, Material2AL.CHECK_CIRCLE, Styles.SUCCESS);
    }

    public static void showNotificationError(Pane pane, String message) {
        showNotification(pane, message, Material2AL.ERROR_OUTLINE, Styles.DANGER);
    }
}
