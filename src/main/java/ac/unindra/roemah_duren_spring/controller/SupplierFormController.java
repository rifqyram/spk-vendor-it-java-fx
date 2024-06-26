package ac.unindra.roemah_duren_spring.controller;

import ac.unindra.roemah_duren_spring.JavaFxApplication;
import ac.unindra.roemah_duren_spring.constant.ResponseMessage;
import ac.unindra.roemah_duren_spring.model.Supplier;
import ac.unindra.roemah_duren_spring.service.SupplierService;
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

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

public class SupplierFormController implements Initializable {
    public TextField textFieldName;
    public Label nameLabelError;
    public TextField emailField;
    public Label emailLabelError;
    public TextField mobilePhoneNumberField;
    public Label mobilePhoneLabelError;
    public TextArea addressField;
    public Label addressLabelError;
    public Button btnSubmit;
    public Button cancelBtn;
    public AnchorPane main;

    private Supplier selectedSupplier;

    @Setter
    private AnchorPane ownerPane;

    @Setter
    private Runnable onFormSubmit;

    private final SupplierService supplierService;

    public SupplierFormController() {
        this.supplierService = JavaFxApplication.getBean(SupplierService.class);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setupButtonIcons();
    }

    public void updateForm(Supplier supplier) {
        textFieldName.setText(supplier.getName());
        emailField.setText(supplier.getEmail());
        mobilePhoneNumberField.setText(supplier.getMobilePhoneNo());
        addressField.setText(supplier.getAddress());
        selectedSupplier = supplier;
    }

    public void handleSubmit() {
        if (!ValidationUtil.isFormValid(getMapValidation())) return;
        if (selectedSupplier == null) {
            processCreate();
        } else {
            processUpdate();
        }
    }

    public void handleClose() {
        if (onFormSubmit != null) onFormSubmit.run();
        Stage stage = (Stage) main.getScene().getWindow();
        stage.close();
    }

    private void processCreate() {
        Supplier supplier = createSupplierModel();
        supplierService.createSupplier(
                supplier,
                response -> handleResponseSuccess(ResponseMessage.SUCCESS_CREATE),
                response -> handleResponseError(response.getMessage())
        );
    }

    private void processUpdate() {
        Supplier supplier = createSupplierModel();
        supplierService.updateSupplier(
                supplier,
                response -> handleResponseSuccess(ResponseMessage.SUCCESS_UPDATE),
                response -> handleResponseError(response.getMessage())
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

    private Supplier createSupplierModel() {
        Supplier supplier = new Supplier();
        supplier.setId(selectedSupplier != null ? selectedSupplier.getId() : null);
        supplier.setName(textFieldName.getText());
        supplier.setEmail(emailField.getText());
        supplier.setMobilePhoneNo(mobilePhoneNumberField.getText());
        supplier.setAddress(addressField.getText());
        return supplier;
    }

    private void setupButtonIcons() {
        btnSubmit.setGraphic(new FontIcon(Material2MZ.SAVE));
        cancelBtn.setGraphic(new FontIcon(Material2AL.CANCEL));
    }

    private Map<TextInputControl, Pair<Label, ValidationUtil.ValidationStrategy>> getMapValidation() {
        Map<TextInputControl, Pair<Label, ValidationUtil.ValidationStrategy>> validationMap = new HashMap<>();
        validationMap.put(textFieldName, new Pair<>(nameLabelError, ValidationUtil.ValidationStrategy.REQUIRED));
        validationMap.put(emailField, new Pair<>(emailLabelError, ValidationUtil.ValidationStrategy.EMAIL));
        validationMap.put(mobilePhoneNumberField, new Pair<>(mobilePhoneLabelError, ValidationUtil.ValidationStrategy.PHONE_NUMBER));
        validationMap.put(addressField, new Pair<>(addressLabelError, ValidationUtil.ValidationStrategy.REQUIRED));
        return validationMap;
    }
}
