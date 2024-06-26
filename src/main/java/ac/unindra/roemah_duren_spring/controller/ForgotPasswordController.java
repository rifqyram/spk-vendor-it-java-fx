package ac.unindra.roemah_duren_spring.controller;

import ac.unindra.roemah_duren_spring.constant.ConstantPage;
import ac.unindra.roemah_duren_spring.util.FXMLUtil;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;

import java.net.URL;
import java.util.ResourceBundle;

public class ForgotPasswordController implements Initializable {

    @FXML
    public AnchorPane main;
    @FXML
    public Label errorEmailLabel;
    @FXML
    public Button submitBtn;
    @FXML
    public Hyperlink navigateLogin;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }

    @FXML
    public void toLoginPage() {
        FXMLUtil.navigationStage(main, ConstantPage.LOGIN, "Login | Roemah Duren", false);
    }

    @FXML
    public void handleOnSubmit(ActionEvent actionEvent) {

    }
}
