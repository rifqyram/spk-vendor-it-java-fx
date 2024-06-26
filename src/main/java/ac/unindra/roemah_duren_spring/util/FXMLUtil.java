package ac.unindra.roemah_duren_spring.util;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ListCell;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FXMLUtil {
    public static Parent loadFXML(String page) {
        Parent root = null;
        try {
            String path = String.format("/fxml/%s.fxml", page);
            root = FXMLLoader.load(Objects.requireNonNull(FXMLUtil.class.getResource(path)));
        } catch (IOException e) {
            Logger.getLogger(FXMLUtil.class.getName()).log(Level.SEVERE, null, e);
        }
        return root;
    }

    public static void updateUI(Runnable runnable) {
        Platform.runLater(runnable);
    }

    public static void navigationStage(Pane pane, String page, String title, boolean resizable) {
        Stage stage = (Stage) pane.getScene().getWindow();
        stage.close();
        Parent root = loadFXML(page);
        Stage mainStage = new Stage();
        mainStage.setScene(new Scene(root));
        mainStage.setResizable(resizable);
        mainStage.sizeToScene();
        mainStage.setTitle(title);
        mainStage.show();
    }

    public static <T> void openModal(Pane pane, String page, String title, boolean resizeable, Consumer<T> consumer) {
        try {
            FXMLLoader loader = new FXMLLoader();
            String path = String.format("/fxml/%s.fxml", page);
            loader.setLocation(FXMLUtil.class.getResource(path));
            Parent parent = loader.load();
            Stage dialogStage = createDialogStage(pane, title, resizeable, parent);
            dialogStage.initModality(Modality.WINDOW_MODAL);
            T controller = loader.getController();
            if (controller != null) {
                consumer.accept(controller);
            }
            dialogStage.showAndWait();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static Stage createDialogStage(Pane pane, String title, boolean resizeable, Parent parent) {
        Stage dialogStage = new Stage();
        dialogStage.setTitle(title);
        dialogStage.initModality(Modality.WINDOW_MODAL);
        dialogStage.initOwner(pane.getScene().getWindow());
        dialogStage.setResizable(resizeable);
        Scene scene = new Scene(parent);
        dialogStage.setScene(scene);
        return dialogStage;
    }
}
