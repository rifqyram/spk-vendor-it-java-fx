package ac.unindra.roemah_duren_spring.util;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;

import java.util.Optional;

public class AlertUtil {
    public static void confirmDialog(String title, String headerText, String contentText, Runnable onYesAction) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(headerText);
        alert.setContentText(contentText);

        ButtonType yesBtn = new ButtonType("Ya", ButtonBar.ButtonData.YES);
        ButtonType noBtn = new ButtonType("Tidak", ButtonBar.ButtonData.NO);
        alert.getButtonTypes().setAll(yesBtn, noBtn);

        Optional<ButtonType> result = alert.showAndWait();

        if (result.isPresent() && result.get().equals(yesBtn)) {
            onYesAction.run();
        }
    }
}
