package ac.unindra.spk_vendor_it.controller;

import ac.unindra.spk_vendor_it.JavaFxApplication;
import ac.unindra.spk_vendor_it.constant.ResponseMessage;
import ac.unindra.spk_vendor_it.constant.UserRole;
import ac.unindra.spk_vendor_it.entity.UserCredential;
import ac.unindra.spk_vendor_it.model.EvaluationModel;
import ac.unindra.spk_vendor_it.model.PageModel;
import ac.unindra.spk_vendor_it.model.ProjectModel;
import ac.unindra.spk_vendor_it.service.AuthService;
import ac.unindra.spk_vendor_it.service.ProjectEvaluationService;
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
import org.springframework.data.domain.Page;

import java.net.URL;
import java.time.LocalDateTime;
import java.util.List;
import java.util.ResourceBundle;

public class EvaluationController implements Initializable {
    public AnchorPane main;
    public Button buttonModalAdd;
    public TextField searchField;
    public Button searchBtn;
    public TableView<EvaluationModel> tableEvaluation;
    public TableColumn<EvaluationModel, Integer> noCol;
    public TableColumn<EvaluationModel, ProjectModel> nameCol;
    public TableColumn<EvaluationModel, Integer> countVendorCol;
    public TableColumn<EvaluationModel, ProjectModel> startDateCol;
    public TableColumn<EvaluationModel, ProjectModel> endDateCol;
    public TableColumn<EvaluationModel, LocalDateTime> evaluationDateCol;
    public TableColumn<EvaluationModel, Void> actionsCol;
    public Pagination pagination;

    private final ProjectEvaluationService projectEvaluationService;
    private final AuthService authService;

    public EvaluationController() {
        this.authService = JavaFxApplication.getBean(AuthService.class);
        this.projectEvaluationService = JavaFxApplication.getBean(ProjectEvaluationService.class);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setupButtonIcons();
        handlePagination();
        handleSearch();
        initTable();
        initRole();
    }

    private void initRole() {
        UserCredential userInfo = authService.getUserInfo();
        if (userInfo.getRole().equals(UserRole.EMPLOYEE)) {
            buttonModalAdd.setVisible(false);
            actionsCol.setVisible(false);
        }
    }

    private void setupButtonIcons() {
        buttonModalAdd.setGraphic(new FontIcon(Material2AL.ADD));
    }

    private void initTable() {
        TableUtil.setTableSequence(noCol);
        nameCol.setCellValueFactory(new PropertyValueFactory<>("project"));
        nameCol.setCellFactory(col -> TableUtil.setTableObject(ProjectModel::getName));
        countVendorCol.setCellValueFactory(new PropertyValueFactory<>("countVendor"));
        startDateCol.setCellValueFactory(new PropertyValueFactory<>("startDate"));
        endDateCol.setCellValueFactory(new PropertyValueFactory<>("endDate"));
        evaluationDateCol.setCellValueFactory(new PropertyValueFactory<>("evaluationDate"));
        actionsCol.setCellFactory(col -> TableUtil.setTableDeleteAction((table, index) -> AlertUtil.confirmDelete(() -> {
            Task<Void> task = new Task<>() {
                @Override
                protected Void call() {
                    EvaluationModel evaluationModel = table.getItems().get(index);
                    projectEvaluationService.delete(evaluationModel.getId());
                    return null;
                }
            };

            task.setOnSucceeded(event -> handleSuccessResponse());
            task.setOnCancelled(event -> handleErrorResponse(event.getSource().getException().getMessage()));
            task.setOnFailed(event -> handleErrorResponse(event.getSource().getException().getMessage()));

            new Thread(task).start();
        })));
    }

    public void openModalAdd() {
        FXMLUtil.openModal(main, "evaluation_form", "Tambah Evaluasi", true, (EvaluationFormController evaluationFormController) -> {
            evaluationFormController.setOwnerPane(main);
            evaluationFormController.setOnFormSubmit(this::doSearch);
        });
    }

    private void handlePagination() {
        pagination.setPageFactory(number -> {
            Task<Page<EvaluationModel>> task = new Task<>() {
                @Override
                protected Page<EvaluationModel> call() {
                    return projectEvaluationService.getAll(PageModel.builder()
                            .page(number)
                            .size(10)
                            .query(searchField.getText())
                            .build());
                }
            };

            task.setOnSucceeded(event -> {
                Page<EvaluationModel> evaluationModels = task.getValue();
                tableEvaluation.getItems().setAll(evaluationModels.getContent());
                pagination.setPageCount(evaluationModels.getTotalPages());
            });

            task.setOnCancelled(event -> handleErrorResponse(event.getSource().getException().getMessage()));
            task.setOnFailed(event -> handleErrorResponse(event.getSource().getException().getMessage()));

            new Thread(task).start();
            return new StackPane();
        });
    }

    public void doSearch() {
        Task<List<EvaluationModel>> task = new Task<>() {
            @Override
            protected List<EvaluationModel> call() {
                return projectEvaluationService.getAll(PageModel.builder()
                        .page(0)
                        .size(10)
                        .query(searchField.getText())
                        .build()).getContent();
            }
        };

        task.setOnSucceeded(event -> {
            List<EvaluationModel> evaluationModels = task.getValue();
            tableEvaluation.getItems().setAll(evaluationModels);
        });

        task.setOnCancelled(event -> handleErrorResponse(event.getSource().getException().getMessage()));
        task.setOnFailed(event -> handleErrorResponse(event.getSource().getException().getMessage()));

        new Thread(task).start();
    }

    private void handleSuccessResponse() {
        NotificationUtil.showNotificationSuccess(main, ResponseMessage.SUCCESS_DELETE);
        doSearch();
    }

    private void handleErrorResponse(String error) {
        NotificationUtil.showNotificationError(main, error);
    }

    private void handleSearch() {
        searchField.setOnKeyPressed(keyEvent -> {
            if (keyEvent.getCode() == KeyCode.ENTER) {
                doSearch();
            }
        });
    }
}
