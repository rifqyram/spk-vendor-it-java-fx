package ac.unindra.roemah_duren_spring.controller;

import ac.unindra.roemah_duren_spring.JavaFxApplication;
import ac.unindra.roemah_duren_spring.model.*;
import ac.unindra.roemah_duren_spring.service.BranchService;
import ac.unindra.roemah_duren_spring.service.CustomerService;
import ac.unindra.roemah_duren_spring.service.TransactionService;
import ac.unindra.roemah_duren_spring.util.TableUtil;
import ac.unindra.roemah_duren_spring.util.*;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Pair;
import lombok.Setter;
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.material2.Material2AL;
import org.kordamp.ikonli.material2.Material2MZ;
import org.springframework.util.StringUtils;

import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

public class TransactionFormController implements Initializable {
    public AnchorPane main;
    public ComboBox<Customer> customerComboBox;
    public Label customerLabelError;
    public ComboBox<String> transactionTypeComboBox;
    public Label transactionTypeLabelError;
    public VBox targetBranchPane;
    public ComboBox<Branch> branchComboBox;
    public Label branchLabelError;
    public ComboBox<Branch> targetBranchComboBox;
    public Label targetBranchLabelError;
    public ComboBox<Stock> productComboBox;
    public Label productLabelError;
    public TextField priceField;
    public Label priceLabelError;
    public CheckBox checkBoxPrice;
    public Label basePriceLabel;
    public TextField qtyField;
    public Label qtyLabelError;
    public Button cancelBtn;
    public Button btnSubmit;
    public Label labelStock;
    public Button btnAddCart;
    public TableView<TransactionDetail> tableDetail;
    public TableColumn<TransactionDetail, Integer> noCol;
    public TableColumn<TransactionDetail, Stock> productCol;
    public TableColumn<TransactionDetail, Long> priceCol;
    public TableColumn<TransactionDetail, Integer> qtyCol;
    public TableColumn<TransactionDetail, Void> actionsCol;
    public Label totalPriceLabel;


    @Setter
    private AnchorPane ownerPane;
    @Setter
    private Runnable onSubmitForm;
    private String selectedType;
    private String basePrice;
    private Stock selectedStock;
    private TransactionDetail selectedTransactionDetail;


    private final CustomerService customerService;
    private final BranchService branchService;
    private final TransactionService transactionService;

    public TransactionFormController() {
        this.customerService = JavaFxApplication.getBean(CustomerService.class);
        this.branchService = JavaFxApplication.getBean(BranchService.class);
        this.transactionService = JavaFxApplication.getBean(TransactionService.class);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setupButtonIcons();
        initCustomerComboBox();
        initBranchComboBox();
        initTransTypeComboBox();
        initTableDetail();

        customerComboBox.setButtonCell(ComboBoxUtil.getComboBoxListCell(Customer::getName));
        customerComboBox.setCellFactory(element -> ComboBoxUtil.getComboBoxListCell(Customer::getName));

        branchComboBox.setCellFactory(element -> ComboBoxUtil.getComboBoxListCell(branch -> branch.getCode() + " | " + branch.getName()));
        branchComboBox.setButtonCell(ComboBoxUtil.getComboBoxListCell(branch -> branch.getCode() + " | " + branch.getName()));

        targetBranchComboBox.setCellFactory(element -> ComboBoxUtil.getComboBoxListCell(branch -> branch.getCode() + " | " + branch.getName()));
        targetBranchComboBox.setButtonCell(ComboBoxUtil.getComboBoxListCell(branch -> branch.getCode() + " | " + branch.getName()));

        productComboBox.setCellFactory(element -> ComboBoxUtil.getComboBoxListCell(stock -> stock.getProduct().getCode() + " | " + stock.getProduct().getName()));
        productComboBox.setButtonCell(ComboBoxUtil.getComboBoxListCell(stock -> stock.getProduct().getCode() + " | " + stock.getProduct().getName()));

        TextFieldUtil.changeValueToCurrency(priceField);
        TextFieldUtil.changeValueToNumber(qtyField);

        targetBranchPane.setVisible(false);
        checkBoxPrice.setVisible(false);
        basePriceLabel.setVisible(false);
        labelStock.setVisible(false);
        priceField.setDisable(true);
        qtyField.setDisable(true);
        priceField.setText(String.valueOf(0));

        transactionTypeComboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            selectedType = newValue;
            targetBranchPane.setVisible("Transfer".equals(newValue));
        });

        branchComboBox.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<>() {
            private boolean ignoreListener = false;

            @Override
            public void changed(ObservableValue<? extends Branch> observableValue, Branch oldValue, Branch newValue) {
                if (ignoreListener) {
                    return;
                }

                if (newValue != null) {
                    if (!tableDetail.getItems().isEmpty()) {
                        AlertUtil.confirmDialog(
                                "Konfirmasi",
                                "Konfirmasi",
                                "Apakah anda yakin ingin mengganti cabang ? Semua data keranjang belanja akan dihapus.",
                                () -> {
                                    tableDetail.getItems().clear();
                                    initTargetBranchComboBox(newValue);
                                    initProductComboBox(newValue.getStocks());
                                },
                                () -> {
                                    ignoreListener = true;
                                    branchComboBox.getSelectionModel().select(oldValue);
                                    ignoreListener = false;
                                }
                        );
                        return;
                    }

                    initTargetBranchComboBox(newValue);
                    initProductComboBox(newValue.getStocks());
                }
            }
        });
        ;

        productComboBox.getSelectionModel().selectedItemProperty().addListener((observableValue, oldValue, newValue) -> {
            if (newValue != null) {
                selectedStock = newValue;
                if (selectedType != null && (selectedType.equals("Transfer") || selectedType.equals("Pengembalian"))) {
                    checkBoxPrice.setVisible(false);
                    basePriceLabel.setVisible(false);
                    priceField.setDisable(true);
                    qtyField.setDisable(false);
                    labelStock.setVisible(true);
                    labelStock.setText("Stok : " + newValue.getStock());
                    basePriceLabel.setText(CurrencyUtil.formatCurrencyIDR(newValue.getProduct().getPrice()));
                    basePrice = String.valueOf(newValue.getProduct().getPrice());
                    return;
                } else {
                    checkBoxPrice.setVisible(true);
                    basePriceLabel.setVisible(true);
                }

                priceField.setDisable(false);
                qtyField.setDisable(false);
                labelStock.setVisible(true);
                labelStock.setText("Stok : " + newValue.getStock());
                basePriceLabel.setText(CurrencyUtil.formatCurrencyIDR(newValue.getProduct().getPrice()));
                basePrice = String.valueOf(newValue.getProduct().getPrice());
            } else {
                checkBoxPrice.setVisible(false);
                basePriceLabel.setVisible(false);
                labelStock.setVisible(false);
                basePrice = null;
                priceField.setDisable(true);
                qtyField.setDisable(true);
                qtyField.setText("0");
            }
        });

        checkBoxPrice.selectedProperty().addListener((observableValue, oldValue, newValue) -> {
            if (newValue && basePrice != null) {
                priceField.setDisable(true);
                priceField.setText(basePrice);
            } else {
                priceField.setDisable(false);
                priceField.setText(String.valueOf(0));
            }
        });

        tableDetail.getSelectionModel().selectedItemProperty().addListener((observableValue, oldValue, newValue) -> {
            if (newValue != null) {
                selectedTransactionDetail = newValue;
                selectedStock = newValue.getStock();
                productComboBox.getSelectionModel().select(newValue.getStock());
                priceField.setText(String.valueOf(newValue.getPrice()));
                qtyField.setText(String.valueOf(newValue.getQty()));
            }
        });
    }

    private void initCustomerComboBox() {
        customerService.getCustomers(
                response -> FXMLUtil.updateUI(() -> {
                    customerComboBox.getItems().clear();
                    customerComboBox.getItems().addAll(response.getData());
                }),
                error -> handleResponseError(error.getMessage())
        );
    }

    private void initTransTypeComboBox() {
        transactionTypeComboBox.getItems().setAll(List.of("Pembelian", "Penjualan", "Transfer", "Pengembalian"));
    }

    private void initBranchComboBox() {
        branchService.getAllBranch(
                response -> branchComboBox.getItems().addAll(response.getData()),
                error -> handleResponseError(error.getMessage())
        );
    }

    private void initTableDetail() {
        tableDetail.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);
        TableUtil.setTableSequence(noCol);

        productCol.setCellValueFactory(new PropertyValueFactory<>("stock"));
        productCol.setCellFactory(col -> TableUtil.setTableObject(stock -> stock.getProduct().getName()));

        priceCol.setCellValueFactory(new PropertyValueFactory<>("price"));
        priceCol.setCellFactory(col -> TableUtil.setTableObject(CurrencyUtil::formatCurrencyIDR));

        qtyCol.setCellValueFactory(new PropertyValueFactory<>("qty"));

        actionsCol.setCellFactory(col -> TableUtil.setTableDeleteAction((table, index) -> {
            table.getItems().remove(table.getItems().get(index));
            table.refresh();
        }));
    }

    private void initTargetBranchComboBox(Branch newValue) {
        branchService.getAllBranch(
                response -> FXMLUtil.updateUI(() -> {
                    targetBranchComboBox.getItems().clear();
                    targetBranchComboBox.getItems().addAll(response.getData());
                    targetBranchComboBox.getItems().remove(newValue);
                }),
                error -> handleResponseError(error.getMessage())
        );
    }

    private void initProductComboBox(List<Stock> stocks) {
        productComboBox.getItems().setAll(stocks);
    }

    public void handleSubmit() {
        if (!ValidationUtil.isFormValidComboBox(getValidationComboBoxMapSubmit()))
            return;

        if (tableDetail.getItems().isEmpty()) {
            NotificationUtil.showNotificationError(main, "Keranjang belanja masih kosong");
            return;
        }

        Transaction transactionModel = createTransactionModel();
        AlertUtil.confirmDialog(
                "Konfirmasi",
                "Konfirmasi",
                "Apakah anda yakin ingin menyimpan transaksi ?",
                () -> {
                    transactionService.createTransaction(
                            transactionModel,
                            response -> handleResponseSuccess("Transaksi berhasil disimpan"),
                            error -> handleResponseError(error.getMessage())
                    );
                },
                null
        );
    }

    private void handleResponseSuccess(String message) {
        FXMLUtil.updateUI(() -> {
            NotificationUtil.showNotificationSuccess(ownerPane, message);
            handleClose();
        });
    }

    private Transaction createTransactionModel() {
        Transaction transaction = new Transaction();
        transaction.setCustomer(customerComboBox.getValue());
        transaction.setBranch(branchComboBox.getValue());
        transaction.setTargetBranch(targetBranchComboBox.getValue());
        transaction.setTransactionType(transactionTypeComboBox.getValue());
        transaction.setTransactionDetails(tableDetail.getItems());
        return transaction;
    }

    private TransactionDetail createTransactionDetailModel() {
        TransactionDetail transactionDetail = new TransactionDetail();
        transactionDetail.setPrice(Long.parseLong(CurrencyUtil.getNumericValue(priceField.getText())));
        transactionDetail.setQty(Integer.parseInt(qtyField.getText()));
        transactionDetail.setStock(selectedStock);
        return transactionDetail;
    }

    private void clearDetailForm() {
        productComboBox.getSelectionModel().clearSelection();
        priceField.clear();
        priceField.setDisable(true);
        qtyField.clear();
        checkBoxPrice.setSelected(false);
    }

    public void addToCart() {
        if (!ValidationUtil.isFormValidComboBox(getValidationComboBoxMapAddCart())) {
            return;
        }
        if (!ValidationUtil.isFormValid(getValidationMapAddCart())) {
            return;
        }

        TransactionDetail transactionDetailModel = createTransactionDetailModel();
        boolean found = false;

        if (selectedTransactionDetail != null) {
            tableDetail.getItems().remove(selectedTransactionDetail);
            tableDetail.refresh();
        }

        int totalQuantity = transactionDetailModel.getQty();
        totalQuantity += tableDetail.getItems().stream().filter(transactionDetail -> transactionDetail.getStock().getId().equals(transactionDetailModel.getStock().getId())).mapToInt(TransactionDetail::getQty).sum();

        if (totalQuantity > selectedStock.getStock()) {
            int finalTotalQuantity = totalQuantity;
            Map<TextInputControl, Pair<Label, ValidationUtil.ValidationStrategy>> fieldPairMap = Map.of(
                    qtyField, new Pair<>(qtyLabelError, input -> {
                        if (finalTotalQuantity > selectedStock.getStock()) {
                            return "Kuantitas melebihi stok";
                        }
                        return null;
                    })
            );

            ValidationUtil.isFormValid(fieldPairMap);
            return;
        }

        for (TransactionDetail transactionDetail : tableDetail.getItems()) {
            if (transactionDetail.getStock().getId().equals(transactionDetailModel.getStock().getId()) && transactionDetail.getPrice() == transactionDetailModel.getPrice()) {
                int newStock = transactionDetail.getQty() + transactionDetailModel.getQty();

                Map<TextInputControl, Pair<Label, ValidationUtil.ValidationStrategy>> fieldPairMap = Map.of(
                        qtyField, new Pair<>(qtyLabelError, input -> {
                            if (newStock > selectedStock.getStock()) {
                                return "Kuantitas melebihi stok";
                            }
                            return null;
                        })
                );

                if (newStock > selectedStock.getStock()) {
                    ValidationUtil.isFormValid(fieldPairMap);
                    return;
                }

                transactionDetail.setQty(newStock);
                found = true;
                break;
            }
        }

        if (!found) {
            tableDetail.getItems().add(transactionDetailModel);
        }

        clearDetailForm();
        tableDetail.refresh();

        long totalPrice = tableDetail.getItems().stream().mapToLong(transactionDetail -> Math.toIntExact(transactionDetail.getPrice() * transactionDetail.getQty())).sum();
        totalPriceLabel.setText("Total Harga: " + CurrencyUtil.formatCurrencyIDR(totalPrice));
    }

    public void handleClose() {
        if (onSubmitForm != null) {
            onSubmitForm.run();
        }
        Stage stage = (Stage) main.getScene().getWindow();
        stage.close();
    }

    private void setupButtonIcons() {
        btnSubmit.setGraphic(new FontIcon(Material2MZ.SAVE));
        cancelBtn.setGraphic(new FontIcon(Material2AL.CANCEL));
        btnAddCart.setGraphic(new FontIcon(Material2MZ.SHOPPING_CART));
    }

    private void handleResponseError(String message) {
        FXMLUtil.updateUI(() -> NotificationUtil.showNotificationError(ownerPane, message));
    }

    private Map<TextInputControl, Pair<Label, ValidationUtil.ValidationStrategy>> getValidationMapAddCart() {
        return Map.of(
                priceField, new Pair<>(priceLabelError, input -> {
                    if (!StringUtils.hasText(input)) {
                        return "Harga harus diisi";
                    }
                    if (!CurrencyUtil.getNumericValue(input).matches("[0-9]+")) {
                        return "Harga harus berupa angka";
                    }
                    return null;
                }),
                qtyField, new Pair<>(qtyLabelError, input -> {
                    if (!StringUtils.hasText(input)) {
                        return "Kuantitas harus diisi";
                    }

                    if (!input.matches("[0-9]+")) {
                        return "Kuantitas harus berupa angka";
                    }

                    if (Integer.parseInt(input) <= 0) {
                        return "Kuantitas minimal 1";
                    }

                    if (selectedStock != null && Integer.parseInt(input) > selectedStock.getStock()) {
                        return "Kuantitas melebihi stok";
                    }

                    return null;

                })
        );
    }

    private Map<ComboBoxBase<?>, Pair<Label, ValidationUtil.ValidationStrategyComboBox>> getValidationComboBoxMapAddCart() {
        return Map.of(
                productComboBox, new Pair<>(productLabelError, input -> input.getValue() == null ? "Produk harus dipilih" : null)
        );
    }

    private Map<ComboBoxBase<?>, Pair<Label, ValidationUtil.ValidationStrategyComboBox>> getValidationComboBoxMapSubmit() {
        return Map.of(
                transactionTypeComboBox, new Pair<>(transactionTypeLabelError, input -> input.getValue() == null ? "Tipe transaksi harus dipilih" : null),
                branchComboBox, new Pair<>(branchLabelError, input -> input.getValue() == null ? "Cabang harus dipilih" : null),
                targetBranchComboBox, new Pair<>(targetBranchLabelError, input -> {
                    if (selectedType != null && selectedType.equals("Transfer")) {
                        return input.getValue() == null ? "Cabang harus dipilih" : null;
                    }
                    return null;
                })
        );
    }
}
