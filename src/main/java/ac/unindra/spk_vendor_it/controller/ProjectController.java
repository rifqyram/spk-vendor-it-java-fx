package ac.unindra.spk_vendor_it.controller;

import ac.unindra.spk_vendor_it.JavaFxApplication;
import ac.unindra.spk_vendor_it.constant.ResponseMessage;
import ac.unindra.spk_vendor_it.constant.UserRole;
import ac.unindra.spk_vendor_it.entity.UserCredential;
import ac.unindra.spk_vendor_it.entity.UserInfo;
import ac.unindra.spk_vendor_it.model.PageModel;
import ac.unindra.spk_vendor_it.model.ProjectModel;
import ac.unindra.spk_vendor_it.service.AuthService;
import ac.unindra.spk_vendor_it.service.JasperService;
import ac.unindra.spk_vendor_it.service.ProjectService;
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

public class ProjectController implements Initializable {
    public AnchorPane main;
    public TextField searchField;
    public TableView<ProjectModel> tableProject;
    public TableColumn<ProjectModel, Integer> noCol;
    public TableColumn<ProjectModel, String> nameCol;
    public TableColumn<ProjectModel, String> descriptionCol;
    public TableColumn<ProjectModel, String> startDateCol;
    public TableColumn<ProjectModel, String> endDateCol;
    public TableColumn<ProjectModel, Void> actionsCol;
    public Button searchBtn;
    public Pagination pagination;
    public Button buttonModalAdd;
    public Button printReport;

    private final ProjectService projectService;
    private final AuthService authService;
    private final JasperService jasperService;

    public ProjectController() {
        this.jasperService = JavaFxApplication.getBean(JasperService.class);
        this.authService = JavaFxApplication.getBean(AuthService.class);
        this.projectService = JavaFxApplication.getBean(ProjectService.class);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setupButtonIcons();
        initTableData();
        handlePagination();
        handleSearch();
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
        printReport.setGraphic(new FontIcon(Material2MZ.PRINT));
    }

    private void initTableData() {
        TableUtil.setColumnResizePolicy(tableProject);
        TableUtil.setTableSequence(noCol);
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        descriptionCol.setCellValueFactory(new PropertyValueFactory<>("description"));
        startDateCol.setCellValueFactory(new PropertyValueFactory<>("startDate"));
        endDateCol.setCellValueFactory(new PropertyValueFactory<>("endDate"));
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
            Task<Page<ProjectModel>> task = new Task<>() {
                @Override
                protected Page<ProjectModel> call() {
                    return projectService.getAll(PageModel.builder()
                            .page(number)
                            .size(10)
                            .build());
                }
            };
            task.setOnSucceeded(event -> {
                Page<ProjectModel> projectPage = task.getValue();
                tableProject.getItems().setAll(projectPage.getContent());
                pagination.setPageCount(projectPage.getTotalPages());
            });

            task.setOnFailed(e -> handleErrorResponse(e.getSource().getException().getMessage()));
            task.setOnCancelled(e -> handleErrorResponse(e.getSource().getException().getMessage()));

            new Thread(task).start();
            return new StackPane();
        });
    }

    public void doSearch() {
        Task<Page<ProjectModel>> task = new Task<>() {
            @Override
            protected Page<ProjectModel> call() {
                return projectService.getAll(PageModel.builder()
                        .page(0)
                        .size(10)
                        .query(searchField.getText())
                        .build());
            }
        };

        task.setOnSucceeded(event -> {
            Page<ProjectModel> vendorPage = task.getValue();
            tableProject.getItems().setAll(vendorPage.getContent());
            pagination.setPageCount(vendorPage.getTotalPages());
        });
        task.setOnFailed(event -> NotificationUtil.showNotificationError(main, task.getException().getMessage()));
        task.setOnCancelled(event -> NotificationUtil.showNotificationError(main, event.getSource().getException().getMessage()));

        new Thread(task).start();
    }

    private void handleSuccessResponse() {
        FXMLUtil.updateUI(() -> {
            NotificationUtil.showNotificationSuccess(main, ResponseMessage.SUCCESS_DELETE);
            doSearch();
        });
    }

    private void handleErrorResponse(String error) {
        FXMLUtil.updateUI(() -> {
            NotificationUtil.showNotificationError(main, error);
            resetPrintReport();
        });
    }

    public void openModalAdd() {
        FXMLUtil.openModal(main, "project_form", "Tambah Proyek", false, (ProjectFormController controller) -> {
            controller.setOnFormSubmit(this::doSearch);
            controller.setOwnerPane(main);
        });
    }

    private TableUtil.TableAction<TableView<ProjectModel>, Integer> processDelete() {
        return (table, index) -> {
            ProjectModel project = table.getItems().get(index);
            AlertUtil.confirmDelete(
                    () -> {
                        Task<Void> task = new Task<>() {
                            @Override
                            protected Void call() {
                                projectService.delete(project.getId());
                                return null;
                            }
                        };
                        task.setOnSucceeded(event -> handleSuccessResponse());
                        task.setOnFailed(event -> handleErrorResponse(task.getException().getMessage()));
                        task.setOnCancelled(event -> handleErrorResponse(event.getSource().getException().getMessage()));

                        new Thread(task).start();
                    }
            );
        };
    }

    private TableUtil.TableAction<TableView<ProjectModel>, Integer> processUpdate() {
        return (table, index) -> {
            ProjectModel projectModel = table.getItems().get(index);
            FXMLUtil.openModal(main, "project_form", "Ubah Proyek", false, (ProjectFormController controller) -> {
                controller.updateForm(projectModel);
                controller.setOnFormSubmit(this::doSearch);
                controller.setOwnerPane(main);
            });
        };
    }

    public void doPrintReport() {
        Task<Void> task = new Task<>() {
            @Override
            protected Void call() {
                List<ProjectModel> projectModels = projectService.getAll();
                List<Map<String, Object>> data = new ArrayList<>();
                UserCredential user = authService.getUserInfo();

                int no = 1;
                for (ProjectModel projectModel : projectModels) {
                    Map<String, Object> map = new HashMap<>();
                    map.put("NO", String.valueOf(no++));
                    map.put("PROJECT_NAME", projectModel.getName());
                    map.put("START_DATE", projectModel.getStartDate().toString());
                    map.put("END_DATE", projectModel.getEndDate().toString());
                    map.put("DESCRIPTION", projectModel.getDescription());
                    data.add(map);
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

                jasperService.createReport(main, "project", data, params);
                return null;
            }
        };

        task.setOnRunning(e -> setLoadingPrintReport());
        task.setOnSucceeded(event -> resetPrintReport());
        task.setOnFailed(e -> handleErrorResponse(e.getSource().getException().getMessage()));
        task.setOnCancelled(e -> handleErrorResponse(e.getSource().getException().getMessage()));
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
