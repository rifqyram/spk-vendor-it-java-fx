package ac.unindra.roemah_duren_spring.controller;

import ac.unindra.roemah_duren_spring.JavaFxApplication;
import ac.unindra.roemah_duren_spring.constant.ResponseMessage;
import ac.unindra.roemah_duren_spring.model.Customer;
import ac.unindra.roemah_duren_spring.service.CustomerService;
import ac.unindra.roemah_duren_spring.util.FXMLUtil;
import ac.unindra.roemah_duren_spring.util.NotificationUtil;
import ac.unindra.roemah_duren_spring.util.ValidationUtil;
import javafx.fxml.FXML;
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

public class CustomerFormController implements Initializable {
    @FXML
    public Button btnSubmit;
    @FXML
    public Button cancelBtn;
    @FXML
    private TextField nameField;
    @FXML
    private Label nameLabelError;
    @FXML
    private TextArea addressField;
    @FXML
    private TextField emailField;
    @FXML
    private TextField mobilePhoneField;
    @FXML
    private AnchorPane main;

    private Customer selectedCustomer;
    private final CustomerService customerService;

    @Setter
    private Runnable onFormSubmit;
    @Setter
    private AnchorPane ownerPane;

    public CustomerFormController() {
        this.customerService = JavaFxApplication.getBean(CustomerService.class);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setupButtonIcons();
    }

    public void updateForm(Customer customer) {
        selectedCustomer = customer;
        nameField.setText(customer.getName());
        addressField.setText(customer.getAddress() != null ? customer.getAddress() : "");
        emailField.setText(customer.getEmail() != null ? customer.getEmail() : "");
        mobilePhoneField.setText(customer.getMobilePhoneNo() != null ? customer.getMobilePhoneNo() : "");
    }

    @FXML
    public void handleSubmit() {
        if (!ValidationUtil.isFormValid(getMapValidation())) {
            return;
        }

        if (selectedCustomer == null) {
            processCreate();
        } else {
            processUpdate();
        }
    }

    private void processCreate() {
        Customer customer = createCustomerModel();
        customerService.createCustomer(
                customer,
                response -> handleResponseSuccess(ResponseMessage.SUCCESS_CREATE),
                error -> handleResponseError(error.getMessage())
        );
    }

    private void processUpdate() {
        Customer customer = createCustomerModel();
        customerService.updateCustomer(
                customer,
                response -> handleResponseSuccess(ResponseMessage.SUCCESS_UPDATE),
                error -> handleResponseError(error.getMessage())
        );
    }

    private Customer createCustomerModel() {
        Customer customer = new Customer();
        customer.setId(selectedCustomer == null ? null : selectedCustomer.getId());
        customer.setName(nameField.getText());
        customer.setEmail(emailField.getText());
        customer.setAddress(addressField.getText());
        customer.setMobilePhoneNo(mobilePhoneField.getText());
        return customer;
    }

    @FXML
    private void handleClose() {
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

    private void handleResponseError(String error) {
        FXMLUtil.updateUI(() -> NotificationUtil.showNotificationError(ownerPane, error));
    }

    private Map<TextInputControl, Pair<Label, ValidationUtil.ValidationStrategy>> getMapValidation() {
        Map<TextInputControl, Pair<Label, ValidationUtil.ValidationStrategy>> validationMap = new HashMap<>();
        validationMap.put(nameField, new Pair<>(nameLabelError, ValidationUtil.ValidationStrategy.REQUIRED));
        return validationMap;
    }

    private void setupButtonIcons() {
        btnSubmit.setGraphic(new FontIcon(Material2MZ.SAVE));
        cancelBtn.setGraphic(new FontIcon(Material2AL.CANCEL));
    }
}
