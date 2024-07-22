package ac.unindra.spk_vendor_it.controller;

import ac.unindra.spk_vendor_it.JavaFxApplication;
import ac.unindra.spk_vendor_it.model.CriteriaModel;
import ac.unindra.spk_vendor_it.model.PageModel;
import ac.unindra.spk_vendor_it.service.CriteriaService;
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

public class CriteriaController implements Initializable {
    public AnchorPane main;
    public Button btnModalAdd;
    public Button printReport;
    public TextField searchField;
    public Button searchBtn;
    public TableView<CriteriaModel> tableCriteria;
    public TableColumn<CriteriaModel, Integer> noCol;
    public TableColumn<CriteriaModel, String> nameCol;
    public TableColumn<CriteriaModel, String> categoryCol;
    public TableColumn<CriteriaModel, Integer> weightCol;
    public TableColumn<CriteriaModel, String> descriptionCol;
    public TableColumn<CriteriaModel, Void> actionsCol;
    public Pagination pagination;

    private final CriteriaService criteriaService;

    public CriteriaController() {
        this.criteriaService = JavaFxApplication.getBean(CriteriaService.class);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setupButtonIcons();
        setupTable();
        handlePagination();
        handleSearch();
    }

    private void setupButtonIcons() {
        btnModalAdd.setGraphic(new FontIcon(Material2AL.ADD));
        printReport.setGraphic(new FontIcon(Material2MZ.PRINT));
    }

    private void setupTable() {
        TableUtil.setColumnResizePolicy(tableCriteria);
        TableUtil.setTableSequence(noCol);
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        categoryCol.setCellValueFactory(new PropertyValueFactory<>("category"));
        weightCol.setCellValueFactory(new PropertyValueFactory<>("weight"));
        descriptionCol.setCellValueFactory(new PropertyValueFactory<>("description"));
        actionsCol.setCellFactory(col -> TableUtil.setTableActions(
                        (table, index) -> {
                            CriteriaModel criteriaModel = table.getItems().get(index);
                            FXMLUtil.openModal(main, "criteria_form", "Ubah Kriteria", false, (CriteriaFormController controller) -> {
                                controller.setOwnerPane(main);
                                controller.updateForm(criteriaModel);
                                controller.setOnFormSubmit(this::doSearch);
                            });
                        },
                        (table, index) -> {
                            AlertUtil.confirmDelete(() -> {
                                CriteriaModel criteriaModel = table.getItems().get(index);
                                Task<Void> task = new Task<>() {
                                    @Override
                                    protected Void call() {
                                        criteriaService.delete(criteriaModel.getId());
                                        return null;
                                    }
                                };

                                task.setOnSucceeded(event -> doSearch());
                                task.setOnFailed(e -> handleErrorResponse(e.getSource().getException().getMessage()));
                                new Thread(task).start();
                            });
                        }
                )
        );
    }

    private void handlePagination() {
        pagination.setPageFactory(number -> {
            Task<Page<CriteriaModel>> task = new Task<>() {
                @Override
                protected Page<CriteriaModel> call() {
                    return criteriaService.getAll(PageModel.builder()
                            .page(number)
                            .size(10)
                            .build());
                }
            };
            task.setOnSucceeded(event -> {
                Page<CriteriaModel> criteriaPage = task.getValue();
                tableCriteria.getItems().setAll(criteriaPage.getContent());
                pagination.setPageCount(criteriaPage.getTotalPages());
            });

            task.setOnFailed(e -> handleErrorResponse(e.getSource().getException().getMessage()));
            new Thread(task).start();
            return new StackPane();
        });
    }

    public void openModalAdd() {
        FXMLUtil.openModal(main, "criteria_form", "Tambah Kriteria", false, (CriteriaFormController controller) -> {
            controller.setOwnerPane(main);
            controller.setOnFormSubmit(this::doSearch);
        });
    }

    public void doPrintReport() {
    }

    public void doSearch() {
        Task<Page<CriteriaModel>> task = new Task<>() {
            @Override
            protected Page<CriteriaModel> call() {
                return criteriaService.getAll(PageModel.builder()
                        .page(0)
                        .size(10)
                        .query(searchField.getText())
                        .build());
            }
        };

        task.setOnSucceeded(event -> {
            Page<CriteriaModel> criteriaPage = task.getValue();
            tableCriteria.getItems().setAll(criteriaPage.getContent());
            pagination.setPageCount(criteriaPage.getTotalPages());
        });

        task.setOnFailed(e -> handleErrorResponse(e.getSource().getException().getMessage()));

        new Thread(task).start();
    }

    private void handleSearch() {
        searchField.setOnKeyPressed(keyEvent -> {
            if (keyEvent.getCode() == KeyCode.ENTER) {
                doSearch();
            }
        });
    }

    private void handleErrorResponse(String message) {
        FXMLUtil.updateUI(() -> NotificationUtil.showNotificationError(main, message));
    }
}
