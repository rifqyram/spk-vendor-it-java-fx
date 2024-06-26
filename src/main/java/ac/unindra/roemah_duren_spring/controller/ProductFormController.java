package ac.unindra.roemah_duren_spring.controller;

import ac.unindra.roemah_duren_spring.JavaFxApplication;
import ac.unindra.roemah_duren_spring.constant.ResponseMessage;
import ac.unindra.roemah_duren_spring.model.Product;
import ac.unindra.roemah_duren_spring.model.Supplier;
import ac.unindra.roemah_duren_spring.service.ProductService;
import ac.unindra.roemah_duren_spring.service.SupplierService;
import ac.unindra.roemah_duren_spring.util.*;
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
import java.util.Map;
import java.util.ResourceBundle;

public class ProductFormController implements Initializable {

    public AnchorPane main;
    public TextField codeField;
    public Label codeLabelError;
    public TextField textFieldName;
    public Label nameLabelError;
    public TextField priceField;
    public Label priceLabelError;
    public ComboBox<Supplier> supplierComboBox;
    public Label supplierLabelError;
    public TextArea descriptionField;
    public Label descriptionErrorLabel;
    public Button btnSubmit;
    public Button cancelBtn;

    @Setter
    private AnchorPane ownerPane;

    @Setter
    private Runnable onSubmit;

    private Product selectedProduct;
    private final SupplierService supplierService;
    private final ProductService productService;

    public ProductFormController() {
        this.supplierService = JavaFxApplication.getBean(SupplierService.class);
        this.productService = JavaFxApplication.getBean(ProductService.class);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setupButtonIcons();
        initComboBox();
        TextFieldUtil.changeValueToUppercase(codeField);
        TextFieldUtil.changeValueToCurrency(priceField);
        supplierComboBox.setCellFactory(col -> ComboBoxUtil.getComboBoxListCell(Supplier::getName));
        supplierComboBox.setButtonCell(ComboBoxUtil.getComboBoxListCell(Supplier::getName));
    }

    private void initComboBox() {
        supplierService.getSuppliers(
                response -> supplierComboBox.getItems().setAll(response.getData()),
                error -> handleResponseError(error.getMessage())
        );
    }

    private void processCreate() {
        Product productModel = createProductModel();
        productService.createProduct(
                productModel,
                response -> handleResponseSuccess(ResponseMessage.SUCCESS_CREATE),
                error -> handleResponseError(error.getMessage())
        );
    }

    private void processUpdate() {
        Product productModel = createProductModel();
        productService.updateProduct(
                productModel,
                response -> handleResponseSuccess(ResponseMessage.SUCCESS_UPDATE),
                error -> handleResponseError(error.getMessage())
        );
    }

    public void updateForm(Product product) {
        codeField.setText(product.getCode());
        textFieldName.setText(product.getName());
        priceField.setText(String.valueOf(product.getPrice()));
        supplierComboBox.setValue(product.getSupplier());
        descriptionField.setText(product.getDescription());
        selectedProduct = product;
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

    public void handleSubmit() {
        if (!ValidationUtil.isFormValid(getValidationMap()) && !ValidationUtil.isFormValidComboBox(getValidationComboBoxMap())) return;

        if (selectedProduct != null) {
            processUpdate();
        } else {
            processCreate();
        }
    }

    public void handleClose() {
        if (onSubmit != null) {
            onSubmit.run();
        }
        Stage stage = (Stage) main.getScene().getWindow();
        stage.close();
    }

    private Product createProductModel() {
        String numericValue = CurrencyUtil.getNumericValue(priceField.getText());
        Product product = new Product();
        product.setId(selectedProduct != null ? selectedProduct.getId() : null);
        product.setCode(codeField.getText().toUpperCase());
        product.setName(textFieldName.getText());
        product.setPrice(Long.parseLong(numericValue));
        product.setSupplier(supplierComboBox.getValue());
        product.setDescription(descriptionField.getText());
        return product;
    }

    private void setupButtonIcons() {
        btnSubmit.setGraphic(new FontIcon(Material2MZ.SAVE));
        cancelBtn.setGraphic(new FontIcon(Material2AL.CANCEL));
    }

    private Map<TextInputControl, Pair<Label, ValidationUtil.ValidationStrategy>> getValidationMap() {
        return Map.of(
                codeField, new Pair<>(codeLabelError, ValidationUtil.ValidationStrategy.REQUIRED),
                textFieldName, new Pair<>(nameLabelError, ValidationUtil.ValidationStrategy.REQUIRED),
                priceField, new Pair<>(priceLabelError, ValidationUtil.ValidationStrategy.REQUIRED),
                descriptionField, new Pair<>(descriptionErrorLabel, ValidationUtil.ValidationStrategy.REQUIRED)
        );
    }

    private Map<ComboBoxBase<?>, Pair<Label, ValidationUtil.ValidationStrategyComboBox>> getValidationComboBoxMap() {
        return Map.of(
                supplierComboBox, new Pair<>(supplierLabelError, input -> input.getValue() == null ? "Supplier harus dipilih" : null)
        );
    }
}
