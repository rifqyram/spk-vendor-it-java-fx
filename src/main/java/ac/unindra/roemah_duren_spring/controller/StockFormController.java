package ac.unindra.roemah_duren_spring.controller;

import ac.unindra.roemah_duren_spring.JavaFxApplication;
import ac.unindra.roemah_duren_spring.constant.ResponseMessage;
import ac.unindra.roemah_duren_spring.model.Branch;
import ac.unindra.roemah_duren_spring.model.Product;
import ac.unindra.roemah_duren_spring.model.Stock;
import ac.unindra.roemah_duren_spring.model.Supplier;
import ac.unindra.roemah_duren_spring.service.BranchService;
import ac.unindra.roemah_duren_spring.service.ProductService;
import ac.unindra.roemah_duren_spring.service.StockService;
import ac.unindra.roemah_duren_spring.util.*;
import javafx.event.ActionEvent;
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

public class StockFormController implements Initializable {
    public AnchorPane main;
    public Button btnSubmit;
    public Button cancelBtn;
    public ComboBox<Product> productComboBox;
    public Label productLabelError;
    public ComboBox<Branch> branchComboBox;
    public Label branchLabelError;
    public TextField stockField;
    public Label stockLabelError;

    @Setter
    private AnchorPane ownerPane;
    @Setter
    private Runnable onFormSubmit;

    private final StockService stockService;
    private final BranchService branchService;
    private final ProductService productService;
    private Stock selectedStock;

    public StockFormController() {
        this.stockService = JavaFxApplication.getBean(StockService.class);
        this.branchService = JavaFxApplication.getBean(BranchService.class);
        this.productService = JavaFxApplication.getBean(ProductService.class);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setupButtonIcons();
        initComboBox();

        branchComboBox.setCellFactory(element -> ComboBoxUtil.getComboBoxListCell(branch -> branch.getCode() + " | " + branch.getName()));
        branchComboBox.setButtonCell(ComboBoxUtil.getComboBoxListCell(branch -> branch.getCode() + " | " + branch.getName()));

        productComboBox.setCellFactory(element -> ComboBoxUtil.getComboBoxListCell(product -> product.getCode() + " | " + product.getName()));
        productComboBox.setButtonCell(ComboBoxUtil.getComboBoxListCell(product -> product.getCode() + " | " + product.getName()));

        TextFieldUtil.changeValueToNumber(stockField);
    }

    private void setupButtonIcons() {
        btnSubmit.setGraphic(new FontIcon(Material2MZ.SAVE));
        cancelBtn.setGraphic(new FontIcon(Material2AL.CANCEL));
    }

    private void initComboBox() {
        branchService.getAllBranch(
                response -> FXMLUtil.updateUI(() -> branchComboBox.getItems().setAll(response.getData())),
                error -> FXMLUtil.updateUI(() -> NotificationUtil.showNotificationError(ownerPane, error.getMessage()))
        );
        productService.getAllProducts(
                response -> FXMLUtil.updateUI(() -> productComboBox.getItems().setAll(response.getData())),
                error -> FXMLUtil.updateUI(() -> NotificationUtil.showNotificationError(ownerPane, error.getMessage()))
        );
    }

    public void updateForm(Stock stock) {
        selectedStock = stock;
        productComboBox.setValue(stock.getProduct());
        branchComboBox.setValue(stock.getBranch());
        stockField.setText(String.valueOf(stock.getStock()));
    }

    public void handleSubmit() {
        if (!ValidationUtil.isFormValid(getValidationMap()) && !ValidationUtil.isFormValidComboBox(getValidationComboBoxMap()))
            return;

        if (selectedStock == null) {
            processCreate();
        } else {
            processUpdate();
        }
    }

    private void processCreate() {
        Stock stock = createStockModel();
        stockService.createStock(
                stock,
                response -> handleResponseSuccess(ResponseMessage.SUCCESS_CREATE),
                error -> handleResponseError(error.getMessage())
        );
    }

    private void processUpdate() {
        Stock stock = createStockModel();
        stockService.updateStock(
                stock,
                response -> handleResponseSuccess(ResponseMessage.SUCCESS_UPDATE),
                error -> handleResponseError(error.getMessage())
        );

    }

    private Stock createStockModel() {
        Stock stock = new Stock();
        stock.setId(selectedStock != null ? selectedStock.getId() : null);
        stock.setProduct(productComboBox.getValue());
        stock.setBranch(branchComboBox.getValue());
        stock.setStock(Integer.parseInt(stockField.getText()));
        stock.setTransactionDetails(selectedStock != null ? selectedStock.getTransactionDetails() : null);
        stock.setUpdatedDate(selectedStock != null ? selectedStock.getUpdatedDate() : null);
        return stock;
    }

    public void handleClose() {
        if (onFormSubmit != null) {
            onFormSubmit.run();
        }
        Stage stage = (Stage) main.getScene().getWindow();
        stage.close();
    }

    public void handleResponseSuccess(String message) {
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
                stockField, new Pair<>(stockLabelError, ValidationUtil.ValidationStrategy.NUMBER)
        );
    }

    private Map<ComboBoxBase<?>, Pair<Label, ValidationUtil.ValidationStrategyComboBox>> getValidationComboBoxMap() {
        return Map.of(
                branchComboBox, new Pair<>(branchLabelError, input -> input.getValue() == null ? "Cabang harus dipilih" : null),
                productComboBox, new Pair<>(productLabelError, input -> input.getValue() == null ? "Cabang harus dipilih" : null)
        );
    }
}
