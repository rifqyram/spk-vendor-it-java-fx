package ac.unindra.spk_vendor_it.controller;

import ac.unindra.spk_vendor_it.JavaFxApplication;
import ac.unindra.spk_vendor_it.constant.ResponseMessage;
import ac.unindra.spk_vendor_it.model.VendorModel;
import ac.unindra.spk_vendor_it.service.VendorService;
import ac.unindra.spk_vendor_it.util.FXMLUtil;
import ac.unindra.spk_vendor_it.util.NotificationUtil;
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

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

public class VendorFormController implements Initializable {
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

    private VendorModel selectedVendor;

    @Setter
    private AnchorPane ownerPane;

    @Setter
    private Runnable onFormSubmit;

    private final VendorService vendorService;

    public VendorFormController() {
        this.vendorService = JavaFxApplication.getBean(VendorService.class);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setupButtonIcons();
    }

    public void updateForm(VendorModel vendorModel) {
        textFieldName.setText(vendorModel.getName());
        emailField.setText(vendorModel.getEmail());
        mobilePhoneNumberField.setText(vendorModel.getMobilePhoneNo());
        addressField.setText(vendorModel.getAddress());
        selectedVendor = vendorModel;
    }

    public void handleSubmit() {
        if (!ValidationUtil.isFormValid(getMapValidation())) return;
        if (selectedVendor == null) {
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
        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() {
                VendorModel vendorModel = createSupplierModel();
                vendorService.create(vendorModel);
                return null;
            }
        };
        task.setOnSucceeded(event -> handleResponseSuccess(ResponseMessage.SUCCESS_SAVE));
        task.setOnFailed(event -> handleResponseError(task.getException().getMessage()));
        task.setOnCancelled(event -> handleResponseError(event.getSource().getException().getMessage()));
        new Thread(task).start();
    }

    private void processUpdate() {
        Task<Void> task = new Task<>() {
            @Override
            protected Void call() {
                VendorModel vendorModel = createSupplierModel();
                vendorService.update(vendorModel);
                return null;
            }
        };
        task.setOnSucceeded(event -> handleResponseSuccess(ResponseMessage.SUCCESS_UPDATE));
        task.setOnFailed(event -> handleResponseError(task.getException().getMessage()));
        task.setOnCancelled(event -> handleResponseError(event.getSource().getException().getMessage()));
        new Thread(task).start();
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

    private VendorModel createSupplierModel() {
        VendorModel supplier = new VendorModel();
        supplier.setId(selectedVendor != null ? selectedVendor.getId() : null);
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
