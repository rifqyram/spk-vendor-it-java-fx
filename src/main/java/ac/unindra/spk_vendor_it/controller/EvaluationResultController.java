package ac.unindra.spk_vendor_it.controller;

import ac.unindra.spk_vendor_it.JavaFxApplication;
import ac.unindra.spk_vendor_it.constant.UserRole;
import ac.unindra.spk_vendor_it.entity.ProjectEvaluation;
import ac.unindra.spk_vendor_it.entity.UserCredential;
import ac.unindra.spk_vendor_it.entity.UserInfo;
import ac.unindra.spk_vendor_it.model.EvaluationModel;
import ac.unindra.spk_vendor_it.model.PageModel;
import ac.unindra.spk_vendor_it.model.ProjectModel;
import ac.unindra.spk_vendor_it.service.AuthService;
import ac.unindra.spk_vendor_it.service.JasperService;
import ac.unindra.spk_vendor_it.service.ProjectEvaluationService;
import ac.unindra.spk_vendor_it.util.DateUtil;
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
import org.kordamp.ikonli.material2.Material2MZ;
import org.springframework.data.domain.Page;

import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class EvaluationResultController implements Initializable {
    public AnchorPane main;
    public Button printReport;
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
    private final JasperService jasperService;

    public EvaluationResultController() {
        this.authService = JavaFxApplication.getBean(AuthService.class);
        this.jasperService = JavaFxApplication.getBean(JasperService.class);
        this.projectEvaluationService = JavaFxApplication.getBean(ProjectEvaluationService.class);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setupButtonIcons();
        handlePagination();
        handleSearch();
        initTable();
    }

    private void setupButtonIcons() {
        printReport.setGraphic(new FontIcon(Material2MZ.PRINT));
    }

    private void initTable() {
        TableUtil.setTableSequence(noCol);
        nameCol.setCellValueFactory(new PropertyValueFactory<>("project"));
        nameCol.setCellFactory(col -> TableUtil.setTableObject(ProjectModel::getName));
        countVendorCol.setCellValueFactory(new PropertyValueFactory<>("countVendor"));
        startDateCol.setCellValueFactory(new PropertyValueFactory<>("startDate"));
        endDateCol.setCellValueFactory(new PropertyValueFactory<>("endDate"));
        evaluationDateCol.setCellValueFactory(new PropertyValueFactory<>("evaluationDate"));
        actionsCol.setCellFactory(col -> TableUtil.createDetailButtonCell((table, index) -> {
            EvaluationModel evaluationModel = table.getItems().get(index);
            FXMLUtil.openModal(main, "evaluation_result_detail", "Detail Evaluasi", true, (EvaluationDetailController evaluationDetailController) -> {
                evaluationDetailController.setOwnerPane(main);
                evaluationDetailController.updateDetail(evaluationModel);
            });
        }));
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

    private void handleErrorResponse(String error) {
        FXMLUtil.updateUI(() -> {
            NotificationUtil.showNotificationError(main, error);
            resetPrintReport();
        });
    }

    private void handleSearch() {
        searchField.setOnKeyPressed(keyEvent -> {
            if (keyEvent.getCode() == KeyCode.ENTER) {
                doSearch();
            }
        });
    }

    public void doPrintReport() {
        Task<Void> task = new Task<>() {
            @Override
            protected Void call() {
                UserCredential user = authService.getUserInfo();
                List<ProjectEvaluation> projectEvaluations = projectEvaluationService.getAll();

                List<Map<String, Object>> data = new ArrayList<>();

                for (ProjectEvaluation projectEvaluation : projectEvaluations) {
                    Map<String, Object> map = new HashMap<>();
                    map.put("PROJECT_NAME", projectEvaluation.getProject().getName());
                    map.put("START_DATE", DateUtil.strDateFromLocalDateTime(projectEvaluation.getProject().getStartDate()));
                    map.put("END_DATE", DateUtil.strDateFromLocalDateTime(projectEvaluation.getProject().getEndDate()));
                    map.put("EVALUATION_DATE", DateUtil.strDateTimeFromLocalDateTime(projectEvaluation.getEvaluationDate()));

                    projectEvaluation.getEvaluations().sort(Comparator.comparing(evaluation -> evaluation.getEvaluationResult().getTotalScore(), Comparator.reverseOrder()));

                    AtomicInteger rank = new AtomicInteger(1);
                    AtomicInteger no = new AtomicInteger(1);
                    List<Map<String, Object>> details = projectEvaluation.getEvaluations().stream()
                            .map(evaluation -> {
                                Map<String, Object> mapDetail = new HashMap<>();
                                mapDetail.put("NO", String.valueOf(no.getAndIncrement()));
                                mapDetail.put("VENDOR_NAME", evaluation.getVendor().getName());
                                mapDetail.put("PREFERENCE", String.valueOf(evaluation.getEvaluationResult().getTotalScore()));
                                mapDetail.put("RANKING", String.valueOf(rank.getAndIncrement()));
                                return mapDetail;
                            }).toList();

                    map.put("DETAILS", details);
                    data.add(map);
                }

                Map<String, Object> params = new HashMap<>();
                params.put("DAY_DATE_YEAR", DateUtil.strDayDateFromLocalDate(LocalDate.now()));
                params.put("DATE_TIME", DateUtil.strDateTimeFromLocalDateTime(LocalDateTime.now()));
                params.put("SUB_REPORT", jasperService.loadReport("evaluation_detail"));

                if (user.getRole().equals(UserRole.ADMIN)) {
                    params.put("POSITION", "Chief Executive Officer");
                    params.put("USERNAME", "Jution Candra Kirana");
                } else {
                    UserInfo userInfo = authService.getUserInfo().getUserInfo();
                    params.put("POSITION", userInfo.getPosition());
                    params.put("USERNAME", userInfo.getName());
                }

                jasperService.createReport(main, "evaluation", data, params);
                return null;
            }
        };

        task.setOnRunning(event -> setLoadingPrintReport());
        task.setOnSucceeded(event -> resetPrintReport());
        task.setOnCancelled(event -> handleErrorResponse(event.getSource().getException().getMessage()));
        task.setOnFailed(event -> handleErrorResponse(event.getSource().getException().getMessage()));

        new Thread(task).start();
    }

    private void resetPrintReport() {
        printReport.setText("Cetak Laporan");
        printReport.setDisable(false);
    }

    private void setLoadingPrintReport() {
        printReport.setText("Loading...");
        printReport.setDisable(true);
    }
}
