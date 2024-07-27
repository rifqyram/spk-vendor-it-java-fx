package ac.unindra.spk_vendor_it.controller;

import ac.unindra.spk_vendor_it.JavaFxApplication;
import ac.unindra.spk_vendor_it.model.EvaluationModel;
import ac.unindra.spk_vendor_it.model.PageModel;
import ac.unindra.spk_vendor_it.model.ProjectModel;
import ac.unindra.spk_vendor_it.service.ProjectEvaluationService;
import ac.unindra.spk_vendor_it.service.StatisticsService;
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
import org.springframework.data.domain.Page;

import java.net.URL;
import java.time.LocalDateTime;
import java.util.List;
import java.util.ResourceBundle;

public class DashboardController implements Initializable {
    public AnchorPane main;
    public Label labelTotalVendor;
    public Label labelTotalProject;
    public Label labelTotalCriteria;
    public Label labelTotalEvaluation;

    public TextField searchField;
    public Button searchBtn;
    public TableView<EvaluationModel> tableEvaluation;
    public TableColumn<EvaluationModel, Integer> noCol;
    public TableColumn<EvaluationModel, ProjectModel> nameCol;
    public TableColumn<EvaluationModel, Integer> countVendorCol;
    public TableColumn<EvaluationModel, ProjectModel> startDateCol;
    public TableColumn<EvaluationModel, ProjectModel> endDateCol;
    public TableColumn<EvaluationModel, LocalDateTime> evaluationDateCol;
    public Pagination pagination;

    private final StatisticsService statisticsService;
    private final ProjectEvaluationService projectEvaluationService;

    public DashboardController() {
        this.projectEvaluationService = JavaFxApplication.getBean(ProjectEvaluationService.class);
        this.statisticsService = JavaFxApplication.getBean(StatisticsService.class);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initData();
        handlePagination();
        handleSearch();
        initTable();
    }

    private void initData() {
        Task<Void> task = new Task<>() {
            @Override
            protected Void call() {
                long totalVendor = statisticsService.totalVendor();
                long totalProject = statisticsService.totalProject();
                long totalCriteria = statisticsService.totalCriteria();
                long totalEvaluation = statisticsService.totalEvaluation();

                FXMLUtil.updateUI(() -> {
                    labelTotalVendor.setText(String.valueOf(totalVendor));
                    labelTotalProject.setText(String.valueOf(totalProject));
                    labelTotalCriteria.setText(String.valueOf(totalCriteria));
                    labelTotalEvaluation.setText(String.valueOf(totalEvaluation));
                });

                return null;
            }
        };

        task.setOnFailed(event -> handleErrorResponse(task.getException().getMessage()));
        task.setOnCancelled(event -> handleErrorResponse(task.getException().getMessage()));

        new Thread(task).start();
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

    private void handleSearch() {
        searchField.setOnKeyPressed(keyEvent -> {
            if (keyEvent.getCode() == KeyCode.ENTER) {
                doSearch();
            }
        });
    }

    private void initTable() {
        TableUtil.setTableSequence(noCol);
        nameCol.setCellValueFactory(new PropertyValueFactory<>("project"));
        nameCol.setCellFactory(col -> TableUtil.setTableObject(ProjectModel::getName));
        countVendorCol.setCellValueFactory(new PropertyValueFactory<>("countVendor"));
        startDateCol.setCellValueFactory(new PropertyValueFactory<>("startDate"));
        endDateCol.setCellValueFactory(new PropertyValueFactory<>("endDate"));
        evaluationDateCol.setCellValueFactory(new PropertyValueFactory<>("evaluationDate"));
    }

    private void handleErrorResponse(String message) {
        FXMLUtil.updateUI(() -> NotificationUtil.showNotificationError(main, message));
    }

}
