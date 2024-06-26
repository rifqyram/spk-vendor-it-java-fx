package ac.unindra.roemah_duren_spring.controller;

import ac.unindra.roemah_duren_spring.JavaFxApplication;
import ac.unindra.roemah_duren_spring.constant.ConstantPage;
import ac.unindra.roemah_duren_spring.constant.ResponseMessage;
import ac.unindra.roemah_duren_spring.dto.request.QueryRequest;
import ac.unindra.roemah_duren_spring.model.Product;
import ac.unindra.roemah_duren_spring.model.Supplier;
import ac.unindra.roemah_duren_spring.service.ProductService;
import ac.unindra.roemah_duren_spring.service.SupplierService;
import ac.unindra.roemah_duren_spring.util.TableUtil;
import ac.unindra.roemah_duren_spring.util.*;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.util.Pair;
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.material2.Material2AL;
import org.kordamp.ikonli.material2.Material2MZ;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.ResourceBundle;

public class ProductController implements Initializable {
    public AnchorPane main;
    public TextField searchField;
    public Button searchBtn;
    public TableView<Product> tableProduct;
    public TableColumn<Product, Integer> noCol;
    public TableColumn<Product, String> codeCol;
    public TableColumn<Product, String> nameCol;
    public TableColumn<Product, Long> priceCol;
    public TableColumn<Product, String> descriptionCol;
    public TableColumn<Product, Supplier> supplierCol;
    public TableColumn<Product, Void> actionsCol;
    public Pagination pagination;
    public Button btnOpenModal;

    private final ProductService productService;

    public ProductController() {
        this.productService = JavaFxApplication.getBean(ProductService.class);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setupButtonIcons();
        initTableData();
        handleSearch();
        handlePagination();
    }

    private void handlePagination() {
        pagination.setPageFactory(pageIndex -> {
            productService.getProducts(QueryRequest.builder()
                            .page(pageIndex)
                            .size(10)
                            .query("")
                            .build(),
                    response -> FXMLUtil.updateUI(() -> {
                        tableProduct.getItems().setAll(response.getData());
                        pagination.setPageCount(response.getPaging().getTotalPages());
                    }),
                    error -> handleErrorResponse(error.getMessage())
            );
            return new AnchorPane();
        });
    }

    private void initTableData() {
        TableUtil.setColumnResizePolicy(tableProduct);
        TableUtil.setTableSequence(noCol);
        codeCol.setCellValueFactory(new PropertyValueFactory<>("code"));
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        priceCol.setCellValueFactory(new PropertyValueFactory<>("price"));
        priceCol.setCellFactory(col -> TableUtil.formatCurrencyIDR());
        descriptionCol.setCellValueFactory(new PropertyValueFactory<>("description"));
        supplierCol.setCellValueFactory(new PropertyValueFactory<>("supplier"));
        supplierCol.setCellFactory(col -> TableUtil.setTableObject(Supplier::getName));
        actionsCol.setCellFactory(col -> TableUtil.setTableActions(
                (table, index) -> FXMLUtil.openModal(main, ConstantPage.PRODUCT_FORM, "Ubah Barang", false, (ProductFormController controller) -> {
                    controller.updateForm(table.getItems().get(index));
                    controller.setOwnerPane(main);
                    controller.setOnSubmit(this::doSearch);
                }),
                (table, index) -> AlertUtil.confirmDelete(() -> {
                    Product product = table.getItems().get(index);
                    productService.deleteProduct(
                            product.getId(),
                            response -> handleSuccessResponse(ResponseMessage.SUCCESS_DELETE),
                            error -> handleErrorResponse(error.getMessage())
                    );
                })
        ));
    }

    public void doSearch() {
        productService.getProducts(QueryRequest.builder()
                        .page(0)
                        .size(10)
                        .query(searchField.getText())
                        .build(),
                response -> {
                    FXMLUtil.updateUI(() -> {
                        tableProduct.getItems().clear();
                        tableProduct.getItems().addAll(response.getData());
                        pagination.setPageCount(response.getPaging().getTotalPages());
                        pagination.setCurrentPageIndex(0);
                    });
                },
                error -> handleErrorResponse(error.getMessage())
        );
    }

    private void setupButtonIcons() {
        btnOpenModal.setGraphic(new FontIcon(Material2AL.ADD));
    }

    public void openModal() {
        FXMLUtil.openModal(main, ConstantPage.PRODUCT_FORM, "Tambah Barang", false, (ProductFormController controller) -> {
            controller.setOnSubmit(this::doSearch);
            controller.setOwnerPane(main);
        });
    }

    public void handleSearch() {
        searchField.setOnKeyPressed(keyEvent -> {
            if (keyEvent.getCode() == KeyCode.ENTER) {
                doSearch();
            }
        });
    }

    private void handleSuccessResponse(String message) {
        NotificationUtil.showNotificationSuccess(main, message);
        doSearch();
    }

    private void handleErrorResponse(String error) {
        NotificationUtil.showNotificationError(main, error);
    }
}
