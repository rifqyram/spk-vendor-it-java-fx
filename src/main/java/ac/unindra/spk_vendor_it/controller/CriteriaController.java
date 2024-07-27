package ac.unindra.spk_vendor_it.controller;

import ac.unindra.spk_vendor_it.JavaFxApplication;
import ac.unindra.spk_vendor_it.constant.UserRole;
import ac.unindra.spk_vendor_it.entity.UserCredential;
import ac.unindra.spk_vendor_it.entity.UserInfo;
import ac.unindra.spk_vendor_it.model.CriteriaModel;
import ac.unindra.spk_vendor_it.model.PageModel;
import ac.unindra.spk_vendor_it.model.WeightCriteriaModel;
import ac.unindra.spk_vendor_it.service.AuthService;
import ac.unindra.spk_vendor_it.service.CriteriaService;
import ac.unindra.spk_vendor_it.service.JasperService;
import ac.unindra.spk_vendor_it.util.*;
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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
    private final AuthService authService;
    private final JasperService jasperService;

    public CriteriaController() {
        this.jasperService = JavaFxApplication.getBean(JasperService.class);
        this.authService = JavaFxApplication.getBean(AuthService.class);
        this.criteriaService = JavaFxApplication.getBean(CriteriaService.class);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initRole();
        setupButtonIcons();
        setupTable();
        handlePagination();
        handleSearch();
    }

    private void initRole() {
        UserCredential userInfo = authService.getUserInfo();
        if (userInfo.getRole().equals(UserRole.EMPLOYEE)) {
            btnModalAdd.setVisible(false);
            actionsCol.setVisible(false);
        }
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

    public void doPrintReport() {
        Task<Void> task = new Task<>() {
            @Override
            protected Void call() {
                List<CriteriaModel> criteriaModels = criteriaService.getAll();
                UserCredential user = authService.getUserInfo();

                List<Map<String, Object>> data = new ArrayList<>();

                int no = 1;
                for (CriteriaModel criteriaModel : criteriaModels) {
                    Map<String, Object> criteriaMap = new HashMap<>();
                    criteriaMap.put("NO", String.valueOf(no++));
                    criteriaMap.put("CRITERIA_NAME", criteriaModel.getName());
                    criteriaMap.put("CATEGORY", criteriaModel.getCategory());
                    criteriaMap.put("WEIGHT", String.valueOf(criteriaModel.getWeight()));
                    criteriaMap.put("DESCRIPTION", criteriaModel.getDescription());

                    if (criteriaModel.getSubCriteria() != null && !criteriaModel.getSubCriteria().isEmpty()) {
                        criteriaMap.put("SUB_CRITERIA", "<b>Sub Criteria</b> : \n" + criteriaModel.getSubCriteria()
                                .stream()
                                .sorted(Comparator.comparing(WeightCriteriaModel::getWeight))
                                .flatMap(weightCriteriaModel -> Stream.of(weightCriteriaModel.getWeight() + " | " + weightCriteriaModel.getDescription() + "\n"))
                                .collect(Collectors.joining("\n")));
                    } else {
                        criteriaMap.put("SUB_CRITERIA", "");
                    }
                    data.add(criteriaMap);
                }

                Map<String, Object> params = new HashMap<>();
                params.put("DAY_DATE_YEAR", DateUtil.strDayDateFromLocalDate(LocalDate.now()));
                params.put("DATE_TIME", DateUtil.strDateTimeFromLocalDateTime(LocalDateTime.now()));

                if (user.getRole().equals(UserRole.ADMIN)) {
                    params.put("POSITION", "Chief Executive Officer");
                    params.put("USERNAME", "Jution Candra Kirana");
                } else {
                    UserInfo userInfo = authService.getUserInfo().getUserInfo();
                    params.put("POSITION", userInfo.getPosition());
                    params.put("USERNAME", userInfo.getName());
                }

                jasperService.createReport(main, "criteria", data, params);
                return null;
            }
        };

        task.setOnRunning(e -> setLoadingPrintReport());
        task.setOnSucceeded(event -> resetPrintReport());
        task.setOnFailed(e -> handleErrorResponse(e.getSource().getException().getMessage()));
        task.setOnCancelled(e -> handleErrorResponse(e.getSource().getException().getMessage()));

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
        FXMLUtil.updateUI(() -> {
            NotificationUtil.showNotificationError(main, message);
            resetPrintReport();
        });
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
