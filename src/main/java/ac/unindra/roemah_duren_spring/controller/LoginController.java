package ac.unindra.roemah_duren_spring.controller;

import ac.unindra.roemah_duren_spring.JavaFxApplication;
import ac.unindra.roemah_duren_spring.constant.ConstantPage;
import ac.unindra.roemah_duren_spring.dto.request.AuthRequest;
import ac.unindra.roemah_duren_spring.dto.response.CommonResponse;
import ac.unindra.roemah_duren_spring.dto.response.LoginResponse;
import ac.unindra.roemah_duren_spring.repository.TokenManager;
import ac.unindra.roemah_duren_spring.service.AuthService;
import ac.unindra.roemah_duren_spring.util.FXMLUtil;
import ac.unindra.roemah_duren_spring.util.NotificationUtil;
import ac.unindra.roemah_duren_spring.util.ValidationUtil;
import ac.unindra.roemah_duren_spring.util.WebClientUtil;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.util.Pair;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

public class LoginController implements Initializable {
    private final AuthService authService;
    private final TokenManager tokenManager;

    @FXML
    public TextField emailText;
    @FXML
    public PasswordField passwordText;
    @FXML
    public Button loginBtn;
    @FXML
    public AnchorPane main;
    @FXML
    public Label errorLabelEmail;
    @FXML
    public Label errorLabelPassword;

    public LoginController() {
        this.tokenManager = JavaFxApplication.getBean(TokenManager.class);
        this.authService = JavaFxApplication.getBean(AuthService.class);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        emailText.setOnKeyTyped(keyEvent -> {
            if (keyEvent.getCode().equals(KeyCode.ENTER)) {
                handleLogin();
            }
        });
        passwordText.setOnKeyTyped(keyEvent -> {
            if (keyEvent.getCode().equals(KeyCode.ENTER)) {
                handleLogin();
            }
        });
    }

    @FXML
    public void handleLogin() {
        ValidationUtil.ValidationStrategy emailValidationStrategy = input -> {
            if (input == null || input.isEmpty()) {
                return "email ini wajib di isi!";
            } else if (!input.matches("[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}")) {
                return "email tidak valid!";
            }
            return "";
        };
        Map<TextInputControl, Pair<Label, ValidationUtil.ValidationStrategy>> validationMap = new HashMap<>();
        validationMap.put(emailText, new Pair<>(errorLabelEmail, emailValidationStrategy));
        validationMap.put(passwordText, new Pair<>(errorLabelPassword, input -> input.isEmpty() ? "Password field ini wajib di isi!" : ""));

        if (!ValidationUtil.isFormValid(validationMap)) return;

        loginBtn.setText("Loading...");
        loginBtn.setDisable(true);

        authService.login(
                AuthRequest.builder()
                        .email(emailText.getText())
                        .password(passwordText.getText())
                        .build(),
                response -> FXMLUtil.updateUI(() -> {
                    loginBtn.setText("Login");
                    loginBtn.setDisable(false);

                    NotificationUtil.showNotificationSuccess(main, "Login Berhasil");
                    tokenManager.saveToken(response.getToken());

                    FXMLUtil.navigationStage(main, ConstantPage.MAIN_APP, "Dashboard | Roemah Duren", true);
                }),
                error -> FXMLUtil.updateUI(() -> {
                    loginBtn.setText("Login");
                    loginBtn.setDisable(false);
                    NotificationUtil.showNotificationError(main, error.getMessage());
                })
        );
    }
}
