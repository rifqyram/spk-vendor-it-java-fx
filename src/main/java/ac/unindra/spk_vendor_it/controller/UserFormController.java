package ac.unindra.spk_vendor_it.controller;

import ac.unindra.spk_vendor_it.JavaFxApplication;
import ac.unindra.spk_vendor_it.constant.ResponseMessage;
import ac.unindra.spk_vendor_it.model.UserInfoModel;
import ac.unindra.spk_vendor_it.service.UserService;
import ac.unindra.spk_vendor_it.util.FXMLUtil;
import ac.unindra.spk_vendor_it.util.NotificationUtil;
import ac.unindra.spk_vendor_it.util.TextFieldUtil;
import ac.unindra.spk_vendor_it.util.ValidationUtil;
import javafx.concurrent.Task;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.util.Pair;
import lombok.Setter;
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.material2.Material2AL;
import org.kordamp.ikonli.material2.Material2MZ;
import org.springframework.util.StringUtils;

import java.net.URL;
import java.util.Map;
import java.util.ResourceBundle;

public class UserFormController implements Initializable {
    public AnchorPane main;
    public TextField nipField;
    public Label nipLabelError;
    public TextField nameField;
    public Label nameLabelError;
    public TextField positionField;
    public Label positionErrorLabel;
    public TextField emailField;
    public Label emailLabelError;
    public TextField mobilePhoneField;
    public Label mobilePhoneLabelError;
    public PasswordField passwordField;
    public Label passwordLabelError;
    public CheckBox showPasswordCheckBox;
    public CheckBox checkBoxStatus;
    public Button btnSubmit;
    public Button cancelBtn;

    @Setter
    private AnchorPane ownerPane;
    @Setter
    private Runnable onFormSubmit;

    private UserInfoModel selectedUser;
    private final UserService userService;

    public UserFormController() {
        this.userService = JavaFxApplication.getBean(UserService.class);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setupButtonIcons();
        TextFieldUtil.changeValueToNumber(nipField);
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

    private void setupButtonIcons() {
        btnSubmit.setGraphic(new FontIcon(Material2MZ.SAVE));
        cancelBtn.setGraphic(new FontIcon(Material2AL.CANCEL));
    }

    public void updateForm(UserInfoModel userInfoModel) {
        selectedUser = userInfoModel;
        nipField.setText(userInfoModel.getNip());
        nameField.setText(userInfoModel.getName());
        positionField.setText(userInfoModel.getPosition());
        emailField.setText(userInfoModel.getEmail());
        mobilePhoneField.setText(userInfoModel.getMobilePhoneNo());
        checkBoxStatus.setSelected(userInfoModel.getStatus());
    }

    public void handleSubmit() {
        if (!ValidationUtil.isFormValid(getValidationMap())) {
            return;
        }

        if (selectedUser == null) {
            processCreate();
        } else {
            processUpdate();
        }
    }

    private void processCreate() {
        Task<Void> task = new Task<>() {
            @Override
            protected Void call() {
                UserInfoModel userInfoModel = createUserInfoModel();
                userService.create(userInfoModel);
                return null;
            }
        };
        task.setOnSucceeded(event -> handleResponseSuccess(ResponseMessage.SUCCESS_SAVE));
        task.setOnFailed(e -> handleResponseError(e.getSource().getException().getMessage()));
        task.setOnCancelled(e -> handleResponseError(e.getSource().getException().getMessage()));
        new Thread(task).start();
    }

    private void processUpdate() {
        Task<Void> task = new Task<>() {
            @Override
            protected Void call() {
                UserInfoModel userInfoModel = createUserInfoModel();
                userService.update(userInfoModel);
                return null;
            }
        };
        task.setOnSucceeded(event -> handleResponseSuccess(ResponseMessage.SUCCESS_UPDATE));
        task.setOnFailed(e -> handleResponseError(e.getSource().getException().getMessage()));
        task.setOnCancelled(e -> handleResponseError(e.getSource().getException().getMessage()));
        new Thread(task).start();
    }

    private UserInfoModel createUserInfoModel() {
        return UserInfoModel.builder()
                .id(selectedUser != null ? selectedUser.getId() : null)
                .nip(nipField.getText())
                .name(nameField.getText())
                .position(positionField.getText())
                .email(emailField.getText())
                .mobilePhoneNo(mobilePhoneField.getText())
                .status(checkBoxStatus.isSelected())
                .password(showPasswordCheckBox.isSelected() ? passwordField.getPromptText() : passwordField.getText())
                .build();
    }

    public void handleClose() {
        if (onFormSubmit != null) {
            onFormSubmit.run();
        }
        Stage stage = (Stage) main.getScene().getWindow();
        stage.close();
    }

    private void handleResponseSuccess(String message) {
        FXMLUtil.updateUI(() -> {
            NotificationUtil.showNotificationSuccess(ownerPane, message);
            handleClose();
        });
    }

    private void handleResponseError(String message) {
        FXMLUtil.updateUI(() -> NotificationUtil.showNotificationError(ownerPane, message));
    }

    private Map<TextInputControl, Pair<Label, ValidationUtil.ValidationStrategy>> getValidationMap() {
        return Map.of(
                nipField, new Pair<>(nipLabelError, ValidationUtil.ValidationStrategy.REQUIRED),
                nameField, new Pair<>(nameLabelError, ValidationUtil.ValidationStrategy.REQUIRED),
                positionField, new Pair<>(positionErrorLabel, ValidationUtil.ValidationStrategy.REQUIRED),
                emailField, new Pair<>(emailLabelError, ValidationUtil.ValidationStrategy.EMAIL),
                mobilePhoneField, new Pair<>(mobilePhoneLabelError, ValidationUtil.ValidationStrategy.PHONE_NUMBER),
                passwordField, new Pair<>(passwordLabelError, input -> {
                    if (selectedUser == null) {
                        if (!StringUtils.hasText(input)) {
                            return "Field ini wajib diisi";
                        } else if (input.length() < 8) {
                            return "Password minimal 8 karakter";
                        }
                    }
                    return "";
                })
        );
    }
}
