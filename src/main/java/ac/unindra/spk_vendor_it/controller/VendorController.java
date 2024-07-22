package ac.unindra.spk_vendor_it.controller;

import ac.unindra.spk_vendor_it.JavaFxApplication;
import ac.unindra.spk_vendor_it.constant.ResponseMessage;
import ac.unindra.spk_vendor_it.model.CriteriaModel;
import ac.unindra.spk_vendor_it.model.PageModel;
import ac.unindra.spk_vendor_it.model.VendorModel;
import ac.unindra.spk_vendor_it.service.VendorService;
import ac.unindra.spk_vendor_it.util.AlertUtil;
import ac.unindra.spk_vendor_it.util.FXMLUtil;
import ac.unindra.spk_vendor_it.util.NotificationUtil;
import ac.unindra.spk_vendor_it.util.TableUtil;
import javafx.concurrent.Task;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.material2.Material2AL;
import org.kordamp.ikonli.material2.Material2MZ;
import org.springframework.data.domain.Page;

import java.net.URL;
import java.util.ResourceBundle;

public class VendorController implements Initializable {
    public AnchorPane main;
    public TextField searchField;
    public TableView<VendorModel> tableVendor;
    public TableColumn<VendorModel, Integer> noCol;
    public TableColumn<VendorModel, String> nameCol;
    public TableColumn<VendorModel, String> addressCol;
    public TableColumn<VendorModel, String> emailCol;
    public TableColumn<VendorModel, String> mobilePhoneNoCol;
    public TableColumn<VendorModel, Void> actionsCol;
    public Button searchBtn;
    public Pagination pagination;
    public Button buttonModalAdd;
    public Button printReport;

    private final VendorService vendorService;

    public VendorController() {
        this.vendorService = JavaFxApplication.getBean(VendorService.class);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setupButtonIcons();
        initTableData();
        handlePagination();
        handleSearch();
    }

    private void setupButtonIcons() {
        buttonModalAdd.setGraphic(new FontIcon(Material2AL.ADD));
        printReport.setGraphic(new FontIcon(Material2MZ.PRINT));
    }

    private void initTableData() {
        TableUtil.setColumnResizePolicy(tableVendor);
        TableUtil.setTableSequence(noCol);
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        addressCol.setCellValueFactory(new PropertyValueFactory<>("address"));
        emailCol.setCellValueFactory(new PropertyValueFactory<>("email"));
        mobilePhoneNoCol.setCellValueFactory(new PropertyValueFactory<>("mobilePhoneNo"));
        actionsCol.setCellFactory(col -> TableUtil.setTableActions(processUpdate(), processDelete()));

    }

    private void handleSearch() {
        searchField.setOnKeyPressed(keyEvent -> {
            if (keyEvent.getCode() == KeyCode.ENTER) {
                doSearch();
            }
        });
    }

    private void handlePagination() {
        pagination.setPageFactory(number -> {
            Task<Page<VendorModel>> task = new Task<>() {
                @Override
                protected Page<VendorModel> call() {
                    return vendorService.getAll(PageModel.builder()
                            .page(number)
                            .size(10)
                            .build());
                }
            };
            task.setOnSucceeded(event -> {
                Page<VendorModel> criteriaPage = task.getValue();
                tableVendor.getItems().setAll(criteriaPage.getContent());
                pagination.setPageCount(criteriaPage.getTotalPages());
            });

            task.setOnFailed(e -> handleErrorResponse(e.getSource().getException().getMessage()));
            new Thread(task).start();
            return new StackPane();
        });
    }

    public void doSearch() {
        Task<Page<VendorModel>> task = new Task<>() {
            @Override
            protected Page<VendorModel> call() {
                return vendorService.getAll(PageModel.builder()
                        .page(0)
                        .size(10)
                        .query(searchField.getText())
                        .build());
            }
        };

        task.setOnSucceeded(event -> {
            Page<VendorModel> vendorPage = task.getValue();
            tableVendor.getItems().setAll(vendorPage.getContent());
            pagination.setPageCount(vendorPage.getTotalPages());
        });
        task.setOnFailed(event -> NotificationUtil.showNotificationError(main, task.getException().getMessage()));
        task.setOnCancelled(event -> NotificationUtil.showNotificationError(main, event.getSource().getException().getMessage()));

        new Thread(task).start();
    }

    private void handleSuccessResponse() {
        NotificationUtil.showNotificationSuccess(main, ResponseMessage.SUCCESS_DELETE);
        doSearch();
    }

    private void handleErrorResponse(String error) {
        NotificationUtil.showNotificationError(main, error);
    }

    public void openModalAdd() {
        FXMLUtil.openModal(main, "vendor_form", "Tambah Vendor", false, (VendorFormController controller) -> {
            controller.setOnFormSubmit(this::doSearch);
            controller.setOwnerPane(main);
        });
    }

    private TableUtil.TableAction<TableView<VendorModel>, Integer> processDelete() {
        return (table, index) -> {
            VendorModel vendor = table.getItems().get(index);
            AlertUtil.confirmDelete(
                    () -> {
                        Task<Void> task = new Task<Void>() {
                            @Override
                            protected Void call() {
                                vendorService.delete(vendor.getId());
                                return null;
                            }
                        };
                        task.setOnSucceeded(event -> handleSuccessResponse());
                        task.setOnFailed(event -> handleErrorResponse(task.getException().getMessage()));
                        task.setOnCancelled(event -> handleErrorResponse(event.getSource().getException().getMessage()));
                        doSearch();
                    }
            );
        };
    }

    private TableUtil.TableAction<TableView<VendorModel>, Integer> processUpdate() {
        return (table, index) -> {
            VendorModel vendor = table.getItems().get(index);
            FXMLUtil.openModal(main, "vendor_form", "Tambah Supplier", false, (VendorFormController controller) -> {
                controller.updateForm(vendor);
                controller.setOnFormSubmit(this::doSearch);
                controller.setOwnerPane(main);
            });
        };
    }

    public void doPrintReport() {

    }
}
