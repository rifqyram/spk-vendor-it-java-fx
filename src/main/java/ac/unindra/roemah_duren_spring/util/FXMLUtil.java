package ac.unindra.roemah_duren_spring.util;

import ac.unindra.roemah_duren_spring.constant.ConstantPage;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;
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
}
