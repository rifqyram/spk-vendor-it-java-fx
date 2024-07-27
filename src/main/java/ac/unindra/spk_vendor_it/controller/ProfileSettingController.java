package ac.unindra.spk_vendor_it.controller;

import ac.unindra.spk_vendor_it.JavaFxApplication;
import ac.unindra.spk_vendor_it.constant.ResponseMessage;
import ac.unindra.spk_vendor_it.constant.UserRole;
import ac.unindra.spk_vendor_it.entity.UserCredential;
import ac.unindra.spk_vendor_it.entity.UserInfo;
import ac.unindra.spk_vendor_it.model.UserInfoModel;
import ac.unindra.spk_vendor_it.service.UserService;
import ac.unindra.spk_vendor_it.util.AlertUtil;
import ac.unindra.spk_vendor_it.util.FXMLUtil;
import ac.unindra.spk_vendor_it.util.NotificationUtil;
import ac.unindra.spk_vendor_it.util.ValidationUtil;
import javafx.concurrent.Task;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.util.Pair;
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.material2.Material2MZ;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

public class ProfileSettingController implements Initializable {
    public AnchorPane main;
    public TextField nameField;
    public Label nameLabelError;
    public TextField positionField;
    public Label positionErrorLabel;
    public TextField mobilePhoneField;
    public Label mobilePhoneLabelError;
    public PasswordField passwordField;
    public Label passwordLabelError;
    public CheckBox showPasswordCheckBox;
    public PasswordField confirmationPasswordField;
    public Label confirmationPasswordLabelError;
    public Button btnSubmit;
    public Label nipValue;
    public Label nameValue;
    public Label positionValue;
    public Label emailValue;
    public Label mobilePhoneNoValue;
    public Label statusAccountValue;

    private final UserService userService;
    private UserCredential userCredential;

    public ProfileSettingController() {
        this.userService = JavaFxApplication.getBean(UserService.class);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setupButtonIcons();
        updateForm();
        showPassword();
    }

    private void updateForm() {
        Task<UserCredential> task = new Task<>() {
            @Override
            protected UserCredential call() {
                return userService.getInfoByContext();
            }
        };

        task.setOnSucceeded(event -> {
            UserCredential userCredential = task.getValue();
            this.userCredential = userCredential;
            if (userCredential.getRole().equals(UserRole.ADMIN)) {
                nipValue.setText(": -");
                nameValue.setText(": -");
                positionValue.setText(": -");
                emailValue.setText(": -");
                mobilePhoneNoValue.setText(": -");
                statusAccountValue.setText(userCredential.isStatus() ? ": Aktif" : ": Tidak Aktif");
                nameField.setDisable(true);
                positionField.setDisable(true);
                mobilePhoneField.setDisable(true);
            } else {
                UserInfo userInfo = userCredential.getUserInfo();
                UserInfoModel userInfoModel = UserInfoModel.fromEntity(userInfo);
                nipValue.setText(": " + userInfoModel.getNip());
                nameValue.setText(": " + userInfoModel.getName());
                positionValue.setText(": " + userInfoModel.getPosition());
                emailValue.setText(": " + userInfoModel.getEmail());
                mobilePhoneNoValue.setText(": +62" + userInfoModel.getMobilePhoneNo());
                statusAccountValue.setText(userCredential.isStatus() ? ": Aktif" : ": Tidak Aktif");
                nameField.setText(userInfoModel.getName());
                positionField.setText(userInfoModel.getPosition());
                mobilePhoneField.setText(userInfoModel.getMobilePhoneNo());
            }
        });

        task.setOnCancelled(e -> handleResponseError(e.getSource().getException().getMessage()));
        task.setOnFailed(e -> handleResponseError(e.getSource().getException().getMessage()));

        new Thread(task).start();
    }

    private void setupButtonIcons() {
        btnSubmit.setGraphic(new FontIcon(Material2MZ.SAVE));
    }

    public void handleSubmit() {
        ValidationUtil.resetValidation(getMapValidationMap());
        if (!ValidationUtil.isFormValid(getMapValidationMap())) return;

        AlertUtil.confirmUpdate(() -> {
            if (userCredential.getRole().equals(UserRole.ADMIN)) {
                processUpdateAdmin();
                return;
            }

            processUpdateEmployee();
        });
    }

    private void processUpdateAdmin() {
        Task<Void> task = new Task<>() {
            @Override
            protected Void call() {
                userService.updatePassword(passwordField.getText());
                return null;
            }
        };

        task.setOnSucceeded(event -> handleResponseSuccess());
        task.setOnFailed(e -> handleResponseError(e.getSource().getException().getMessage()));
        task.setOnCancelled(e -> handleResponseError(e.getSource().getException().getMessage()));
        new Thread(task).start();
    }

    private void processUpdateEmployee() {
        Task<Void> task = new Task<>() {
            @Override
            protected Void call() {
                UserInfoModel userInfoModel = createUserInfoModel();
                userService.update(userInfoModel);
                return null;
            }
        };

        task.setOnSucceeded(event -> handleResponseSuccess());
        task.setOnFailed(e -> handleResponseError(e.getSource().getException().getMessage()));
        task.setOnCancelled(e -> handleResponseError(e.getSource().getException().getMessage()));

        new Thread(task).start();
    }

    private void handleResponseSuccess() {
        FXMLUtil.updateUI(() -> {
            NotificationUtil.showNotificationSuccess(main, ResponseMessage.SUCCESS_UPDATE);
            updateForm();
            clearPassword();
        });
    }

    private void clearPassword() {
        passwordField.clear();
        confirmationPasswordField.clear();
    }

    private void handleResponseError(String message) {
        FXMLUtil.updateUI(() -> NotificationUtil.showNotificationError(main, message));
    }

    private UserInfoModel createUserInfoModel() {
        UserInfo userInfo = userCredential.getUserInfo();
        UserInfoModel userInfoModel = UserInfoModel.fromEntity(userInfo);
        userInfoModel.setName(nameField.getText());
        userInfoModel.setPosition(positionField.getText());
        userInfoModel.setMobilePhoneNo(mobilePhoneField.getText());
        if (!passwordField.getText().isEmpty()) {
            if (showPasswordCheckBox.isSelected()) {
                userInfoModel.setPassword(passwordField.getPromptText());
            } else {
                userInfoModel.setPassword(passwordField.getText());
            }
        }
        return userInfoModel;
    }

    private Map<TextInputControl, Pair<Label, ValidationUtil.ValidationStrategy>> getMapValidationMap() {
        Map<TextInputControl, Pair<Label, ValidationUtil.ValidationStrategy>> validationMap = new HashMap<>();
        if (userCredential.getRole().equals(UserRole.EMPLOYEE)) {
            validationMap.put(nameField, new Pair<>(nameLabelError, ValidationUtil.ValidationStrategy.REQUIRED));
            validationMap.put(positionField, new Pair<>(positionErrorLabel, ValidationUtil.ValidationStrategy.REQUIRED));
            validationMap.put(mobilePhoneField, new Pair<>(mobilePhoneLabelError, ValidationUtil.ValidationStrategy.PHONE_NUMBER));
        }
        if (!passwordField.getText().isEmpty()) {
            validationMap.put(passwordField, new Pair<>(passwordLabelError, ValidationUtil.ValidationStrategy.PASSWORD));
            validationMap.put(confirmationPasswordField, new Pair<>(confirmationPasswordLabelError, input -> {
                if (input.isEmpty()) return "Konfirmasi password tidak boleh kosong";
                if (!input.equals(passwordField.getText())) return "Konfirmasi password tidak sama";
                return null;
            }));
        }
        return validationMap;
    }

    private void showPassword() {
        showPasswordCheckBox.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                passwordField.setPromptText(passwordField.getText());
                passwordField.setText("");
            } else {
                passwordField.setText(passwordField.getPromptText());
                passwordField.setPromptText("");
            }
        });
    }
}
