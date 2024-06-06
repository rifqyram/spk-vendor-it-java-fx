package ac.unindra.roemah_duren_spring.controller;

import ac.unindra.roemah_duren_spring.JavaFxApplication;
import ac.unindra.roemah_duren_spring.constant.ConstantPage;
import ac.unindra.roemah_duren_spring.model.request.AuthRequest;
import ac.unindra.roemah_duren_spring.model.response.CommonResponse;
import ac.unindra.roemah_duren_spring.model.response.LoginResponse;
import ac.unindra.roemah_duren_spring.repository.TokenManager;
import ac.unindra.roemah_duren_spring.util.FXMLUtil;
import ac.unindra.roemah_duren_spring.util.NotificationUtil;
import ac.unindra.roemah_duren_spring.util.ValidationUtil;
import ac.unindra.roemah_duren_spring.util.WebClientUtil;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.util.Pair;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

public class LoginController implements Initializable {
    private final WebClientUtil webClient;
    private final TokenManager tokenManager;

    @FXML
    public TextField emailText;
    @FXML
    public PasswordField passwordText;
    @FXML
    public Hyperlink navForgotPassword;
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
        this.webClient = JavaFxApplication.getBean(WebClientUtil.class);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
    }

    @FXML
    public void toForgotPasswordPage(ActionEvent actionEvent) {
        FXMLUtil.navigationStage(main, ConstantPage.FORGOT_PASSWORD, "Forgot Password | Roemah Duren", false);
    }

    @FXML
    public void handleLogin(ActionEvent actionEvent) {
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
        if (ValidationUtil.isFormValid(validationMap)) return;

        loginBtn.setText("Loading...");
        loginBtn.setDisable(true);
        webClient.callApi(
                "/api/auth/login",
                HttpMethod.POST,
                AuthRequest.builder()
                        .email(emailText.getText())
                        .password(passwordText.getText())
                        .build(),
                new ParameterizedTypeReference<CommonResponse<LoginResponse>>() {
                },
                response -> FXMLUtil.updateUI(() -> {
                    loginBtn.setText("Login");
                    loginBtn.setDisable(false);

                    NotificationUtil.showNotificationSuccess(main, response.getMessage());
                    tokenManager.saveToken(response.getData().getToken());

                    FXMLUtil.navigationStage(main, ConstantPage.MAIN_APP, "Roemah Duren", true);
                }),
                error -> FXMLUtil.updateUI(() -> {
                    loginBtn.setText("Login");
                    loginBtn.setDisable(false);
                    NotificationUtil.showNotificationError(main, error.getMessage());
                })
        );
    }
}
