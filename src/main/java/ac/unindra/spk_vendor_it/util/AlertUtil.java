package ac.unindra.spk_vendor_it.util;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;

import java.util.Optional;

public class AlertUtil {
    public static void confirmDialog(String title, String headerText, String contentText, Runnable onYesAction, Runnable onNoAction) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(headerText);
        alert.setContentText(contentText);

        confirmButton(onYesAction, onNoAction, alert);
    }

    public static void confirmDelete(Runnable onYesAction) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Konfirmasi");
        alert.setHeaderText("Apakah anda yakin ingin menghapus data ini?");
        alert.setContentText("Data yang sudah dihapus tidak dapat dikembalikan");
        confirmButton(onYesAction, null, alert);
    }

    public static void confirmUpdate(Runnable onYesAction) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Konfirmasi");
        alert.setHeaderText("Apakah anda yakin ingin mengubah data ini?");
        alert.setContentText("Data yang sudah diubah tidak dapat dikembalikan");

        confirmButton(onYesAction, null, alert);
    }

    private static void confirmButton(Runnable onYesAction, Runnable onNoAction, Alert alert) {
        ButtonType yesBtn = new ButtonType("Ya", ButtonBar.ButtonData.YES);
        ButtonType noBtn = new ButtonType("Tidak", ButtonBar.ButtonData.NO);

        alert.getButtonTypes().setAll(yesBtn, noBtn);

        Optional<ButtonType> result = alert.showAndWait();

        if (result.isPresent() && result.get().equals(yesBtn)) {
            onYesAction.run();
        } else {
            onNoAction.run();
        }
    }
}
