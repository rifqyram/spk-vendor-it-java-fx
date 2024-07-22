package ac.unindra.spk_vendor_it.controller;

import ac.unindra.spk_vendor_it.JavaFxApplication;
import ac.unindra.spk_vendor_it.model.LoginModel;
import ac.unindra.spk_vendor_it.service.AuthService;
import ac.unindra.spk_vendor_it.util.FXMLUtil;
import ac.unindra.spk_vendor_it.util.NotificationUtil;
import ac.unindra.spk_vendor_it.util.ValidationUtil;
import javafx.concurrent.Task;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.util.Pair;

import java.net.URL;
import java.util.Map;
import java.util.ResourceBundle;

public class LoginController implements Initializable {
    public TextField usernameText;
    public Label errorLabelUsername;
    public PasswordField passwordText;
    public Label errorLabelPassword;
    public Button loginBtn;
    public AnchorPane main;

    private final AuthService authService;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Task<Void> task = new Task<>() {
            @Override
            protected Void call() {
                authService.checkToken();
                return null;
            }
        };

        task.setOnSucceeded(workerStateEvent -> FXMLUtil.navigationStage(main, "main_app", "Dashboard", true));

        new Thread(task).start();
    }

    public LoginController() {
        this.authService = JavaFxApplication.getBean(AuthService.class);
    }

    public void handleLogin() {
        Map<TextInputControl, Pair<Label, ValidationUtil.ValidationStrategy>> map = Map.of(
                usernameText, new Pair<>(errorLabelUsername, ValidationUtil.ValidationStrategy.REQUIRED),
                passwordText, new Pair<>(errorLabelPassword, ValidationUtil.ValidationStrategy.REQUIRED)
        );

        if (!ValidationUtil.isFormValid(map)) {
            return;
        }

        Task<Void> task = new Task<>() {
            @Override
            protected Void call() {
                LoginModel loginModel = createLoginModel();
                authService.login(loginModel);
                return null;
            }
        };

        task.setOnRunning(workerStateEvent -> {
            loginBtn.setDisable(true);
            loginBtn.setText("Loading...");
        });

        task.setOnSucceeded(workerStateEvent -> FXMLUtil.navigationStage(main, "main_app", "Dashboard", true));

        task.setOnFailed(workerStateEvent -> {
            loginBtn.setDisable(false);
            loginBtn.setText("Login");
            FXMLUtil.updateUI(() -> NotificationUtil.showNotificationError(main, task.getException().getMessage()));
        });

        task.setOnCancelled(workerStateEvent -> {
            loginBtn.setDisable(false);
            loginBtn.setText("Login");
            ValidationUtil.resetValidation(map);
        });

        new Thread(task).start();
    }

    public LoginModel createLoginModel() {
        return new LoginModel(usernameText.getText(), passwordText.getText());
    }
}
