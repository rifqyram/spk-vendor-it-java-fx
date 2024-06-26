package ac.unindra.roemah_duren_spring.controller;

import ac.unindra.roemah_duren_spring.JavaFxApplication;
import ac.unindra.roemah_duren_spring.model.Admin;
import ac.unindra.roemah_duren_spring.service.AdminService;
import ac.unindra.roemah_duren_spring.util.FXMLUtil;
import ac.unindra.roemah_duren_spring.util.NotificationUtil;
import ac.unindra.roemah_duren_spring.util.ValidationUtil;
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

public class AdminFormController implements Initializable {
    public AnchorPane main;
    public TextField nipField;
    public Label nipLabelError;
    public TextField nameField;
    public Label nameLabelError;
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
    private Runnable onSubmitForm;
    private Admin selectedAdmin;

    private final AdminService adminService;

    public AdminFormController() {
        this.adminService = JavaFxApplication.getBean(AdminService.class);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setupButtonIcons();

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

    public void updateForm(Admin admin) {
        selectedAdmin = admin;
        nipField.setText(admin.getNip());
        nameField.setText(admin.getName());
        emailField.setText(admin.getEmail());
        mobilePhoneField.setText(admin.getMobilePhoneNo());
        checkBoxStatus.setSelected(admin.isStatus());
    }

    public void handleSubmit() {
        if (!ValidationUtil.isFormValid(getValidationMap())) return;
        if (selectedAdmin != null) {
            processUpdate();
        } else {
            processCreate();
        }
    }

    private void processCreate() {
        Admin adminModel = createAdminModel();
        adminService.createAdmin(
                adminModel,
                response -> handleResponseSuccess("Admin berhasil ditambahkan"),
                error -> handleResponseError(error.getMessage())
        );
    }

    private void processUpdate() {
        Admin adminModel = createAdminModel();
        adminService.updateAdmin(
                adminModel,
                response -> handleResponseSuccess("Admin berhasil diubah"),
                error -> handleResponseError(error.getMessage())
        );
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

    private Admin createAdminModel() {
        Admin admin = new Admin();
        admin.setId(selectedAdmin != null ? selectedAdmin.getId() : null);
        admin.setNip(nipField.getText());
        admin.setName(nameField.getText());
        admin.setEmail(emailField.getText());
        admin.setMobilePhoneNo(mobilePhoneField.getText());
        admin.setPassword(passwordField.getText());
        admin.setStatus(checkBoxStatus.isSelected());
        return admin;
    }

    public void handleClose() {
        if (onSubmitForm != null) {
            onSubmitForm.run();
        }
        Stage stage = (Stage) main.getScene().getWindow();
        stage.close();
    }

    private Map<TextInputControl, Pair<Label, ValidationUtil.ValidationStrategy>> getValidationMap() {
        return Map.of(
                nipField, new Pair<>(nipLabelError, ValidationUtil.ValidationStrategy.REQUIRED),
                nameField, new Pair<>(nameLabelError, ValidationUtil.ValidationStrategy.REQUIRED),
                emailField, new Pair<>(emailLabelError, ValidationUtil.ValidationStrategy.EMAIL),
                mobilePhoneField, new Pair<>(mobilePhoneLabelError, ValidationUtil.ValidationStrategy.PHONE_NUMBER),
                passwordField, new Pair<>(passwordLabelError, input -> {
                    if (selectedAdmin == null) {
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
