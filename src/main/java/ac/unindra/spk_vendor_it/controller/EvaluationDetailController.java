package ac.unindra.spk_vendor_it.controller;

import ac.unindra.spk_vendor_it.JavaFxApplication;
import ac.unindra.spk_vendor_it.constant.UserRole;
import ac.unindra.spk_vendor_it.entity.ProjectEvaluation;
import ac.unindra.spk_vendor_it.entity.UserCredential;
import ac.unindra.spk_vendor_it.entity.UserInfo;
import ac.unindra.spk_vendor_it.model.EvaluationModel;
import ac.unindra.spk_vendor_it.model.EvaluationResultModel;
import ac.unindra.spk_vendor_it.model.VendorModel;
import ac.unindra.spk_vendor_it.service.AuthService;
import ac.unindra.spk_vendor_it.service.JasperService;
import ac.unindra.spk_vendor_it.service.ProjectEvaluationService;
import ac.unindra.spk_vendor_it.util.DateUtil;
import ac.unindra.spk_vendor_it.util.FXMLUtil;
import ac.unindra.spk_vendor_it.util.NotificationUtil;
import ac.unindra.spk_vendor_it.util.TableUtil;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import lombok.Setter;
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.material2.Material2MZ;

import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class EvaluationDetailController implements Initializable {
    public AnchorPane main;
    public Button printBtn;
    public Label projectStartValue;
    public Label projectEndValue;
    public Label evaluationDateValue;
    public Label projectValue;
    public TableView<EvaluationResultModel> tableDetail;
    public TableColumn<EvaluationResultModel, Integer> noCol;
    public TableColumn<EvaluationResultModel, Double> preferenceCol;
    public TableColumn<EvaluationResultModel, VendorModel> vendorCol;
    public TableColumn<EvaluationResultModel, Integer> rankCol;
    public Button closeBtn;

    @Setter
    private AnchorPane ownerPane;

    private EvaluationModel evaluationModel;

    private final ProjectEvaluationService projectEvaluationService;
    private final JasperService jasperService;
    private final AuthService authService;

    public EvaluationDetailController() {
        this.jasperService = JavaFxApplication.getBean(JasperService.class);
        this.authService = JavaFxApplication.getBean(AuthService.class);
        this.projectEvaluationService = JavaFxApplication.getBean(ProjectEvaluationService.class);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setupButtonIcons();
    }

    private void setupButtonIcons() {
        printBtn.setGraphic(new FontIcon(Material2MZ.PRINT));
    }

    public void updateDetail(EvaluationModel evaluationModel) {
        this.evaluationModel = evaluationModel;
        projectValue.setText(": " + evaluationModel.getProject().getName());
        projectStartValue.setText(": " + evaluationModel.getProject().getStartDate().toString());
        projectEndValue.setText(": " + evaluationModel.getProject().getEndDate().toString());
        evaluationDateValue.setText(": " + evaluationModel.getEvaluationDate().toString());

        initResult();
        initTable();
    }

    private void initResult() {
        if (evaluationModel != null) {
            Task<List<EvaluationResultModel>> task = new Task<>() {
                @Override
                protected List<EvaluationResultModel> call() {
                    return projectEvaluationService.getEvaluationResult(evaluationModel.getId());
                }
            };

            task.setOnSucceeded(event -> tableDetail.getItems().setAll(task.getValue()));
            task.setOnFailed(event -> handleErrorResponse(task.getException().getMessage()));
            task.setOnCancelled(event -> handleErrorResponse(task.getException().getMessage()));
            new Thread(task).start();
        }
    }

    private void initTable() {
        TableUtil.setColumnResizePolicy(tableDetail);
        TableUtil.setTableSequence(noCol);

        vendorCol.setCellFactory(col -> TableUtil.setTableObject(VendorModel::getName));
        vendorCol.setCellValueFactory(new PropertyValueFactory<>("vendor"));

        preferenceCol.setCellValueFactory(new PropertyValueFactory<>("preference"));
        rankCol.setCellValueFactory(new PropertyValueFactory<>("rank"));
    }

    public void handleClose() {
        Stage stage = (Stage) main.getScene().getWindow();
        stage.close();
    }

    public void doPrint() {
        Task<Void> task = new Task<>() {
            @Override
            protected Void call() {
                UserCredential user = authService.getUserInfo();
                ProjectEvaluation projectEvaluation = projectEvaluationService.getById(evaluationModel.getId());

                List<Map<String, Object>> data = new ArrayList<>();

                Map<String, Object> map = new HashMap<>();
                map.put("PROJECT_NAME", projectEvaluation.getProject().getName());
                map.put("START_DATE", DateUtil.strDateFromLocalDateTime(projectEvaluation.getProject().getStartDate()));
                map.put("END_DATE", DateUtil.strDateFromLocalDateTime(projectEvaluation.getProject().getEndDate()));
                map.put("EVALUATION_DATE", DateUtil.strDateTimeFromLocalDateTime(projectEvaluation.getEvaluationDate()));

                projectEvaluation.getEvaluations().sort(Comparator.comparing(evaluation -> evaluation.getEvaluationResult().getTotalScore(), Comparator.reverseOrder()));

                AtomicInteger no = new AtomicInteger(1);
                AtomicInteger rank = new AtomicInteger(1);
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
        task.setOnFailed(event -> handleErrorResponse(task.getException().getMessage()));
        task.setOnCancelled(event -> handleErrorResponse(task.getException().getMessage()));

        new Thread(task).start();
    }

    private void handleErrorResponse(String error) {
        FXMLUtil.updateUI(() -> {
            NotificationUtil.showNotificationError(main, error);
            resetPrintReport();
        });
    }

    private void resetPrintReport() {
        printBtn.setText("Cetak Laporan");
        printBtn.setDisable(false);
    }

    private void setLoadingPrintReport() {
        printBtn.setText("Loading...");
        printBtn.setDisable(true);
    }
}
