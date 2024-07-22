package ac.unindra.spk_vendor_it.controller;

import ac.unindra.spk_vendor_it.JavaFxApplication;
import ac.unindra.spk_vendor_it.model.PageModel;
import ac.unindra.spk_vendor_it.model.UserInfoModel;
import ac.unindra.spk_vendor_it.service.UserService;
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
import java.util.ResourceBundle;

public class UserController implements Initializable {
    public AnchorPane main;
    public Button buttonModalAdd;
    public TextField searchField;
    public Button searchBtn;
    public TableView<UserInfoModel> tableUser;
    public TableColumn<UserInfoModel, Integer> noCol;
    public TableColumn<UserInfoModel, String> nipCol;
    public TableColumn<UserInfoModel, String> nameCol;
    public TableColumn<UserInfoModel, String> positionCol;
    public TableColumn<UserInfoModel, String> emailCol;
    public TableColumn<UserInfoModel, String> mobilePhoneNoCol;
    public TableColumn<UserInfoModel, Boolean> statusCol;
    public TableColumn<UserInfoModel, Void> actionCol;
    public Pagination pagination;

    private final UserService userService;

    public UserController() {
        this.userService = JavaFxApplication.getBean(UserService.class);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setupButtonIcons();
        setupTable();
        handlePagination();
        handleSearch();
    }

    private void setupTable() {
        TableUtil.setColumnResizePolicy(tableUser);
        TableUtil.setTableSequence(noCol);
        nipCol.setCellValueFactory(new PropertyValueFactory<>("nip"));
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        positionCol.setCellValueFactory(new PropertyValueFactory<>("position"));
        emailCol.setCellValueFactory(new PropertyValueFactory<>("email"));
        mobilePhoneNoCol.setCellValueFactory(new PropertyValueFactory<>("mobilePhoneNo"));
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));
        statusCol.setCellFactory(col -> TableUtil.setTableObject(status -> status ? "Aktif" : "Tidak Aktif"));
        actionCol.setCellFactory(col -> TableUtil.setTableActions(
                        (table, index) -> {
                            UserInfoModel userInfoModel = table.getItems().get(index);
                            FXMLUtil.openModal(main, "user_form", "Ubah Akun", false, (UserFormController controller) -> {
                                controller.setOwnerPane(main);
                                controller.updateForm(userInfoModel);
                                controller.setOnFormSubmit(this::doSearch);
                            });
                        },
                        (table, index) -> {
                            AlertUtil.confirmDelete(() -> {
                                UserInfoModel userInfoModel = table.getItems().get(index);
                                Task<Void> task = new Task<>() {
                                    @Override
                                    protected Void call() {
                                        userService.delete(userInfoModel.getId());
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

    private void setupButtonIcons() {
        buttonModalAdd.setGraphic(new FontIcon(Material2AL.ADD));
    }

    private void handlePagination() {
        pagination.setPageFactory(number -> {
            Task<Page<UserInfoModel>> task = new Task<>() {
                @Override
                protected Page<UserInfoModel> call() {
                    return userService.getAll(PageModel.builder()
                            .page(number)
                            .size(10)
                            .build());
                }
            };
            task.setOnSucceeded(event -> {
                Page<UserInfoModel> userInfoModelPage = task.getValue();
                tableUser.getItems().setAll(userInfoModelPage.getContent());
                pagination.setPageCount(userInfoModelPage.getTotalPages());
            });

            task.setOnFailed(e -> handleErrorResponse(e.getSource().getException().getMessage()));
            new Thread(task).start();
            return new StackPane();
        });
    }

    public void openModalAdd() {
        FXMLUtil.openModal(main, "user_form", "Tambah Akun", false, (UserFormController controller) -> {
            controller.setOwnerPane(main);
            controller.setOnFormSubmit(this::doSearch);
        });
    }

    private void handleSearch() {
        searchField.setOnKeyPressed(keyEvent -> {
            if (keyEvent.getCode() == KeyCode.ENTER) {
                doSearch();
            }
        });
    }

    public void doSearch() {
        Task<Page<UserInfoModel>> task = new Task<>() {
            @Override
            protected Page<UserInfoModel> call() {
                return userService.getAll(PageModel.builder()
                        .page(0)
                        .size(10)
                        .query(searchField.getText())
                        .build());
            }
        };

        task.setOnSucceeded(event -> {
            Page<UserInfoModel> userInfoModelPage = task.getValue();
            tableUser.getItems().setAll(userInfoModelPage.getContent());
            pagination.setPageCount(userInfoModelPage.getTotalPages());
        });

        task.setOnFailed(e -> handleErrorResponse(e.getSource().getException().getMessage()));

        new Thread(task).start();
    }

    private void handleErrorResponse(String message) {
        FXMLUtil.updateUI(() -> NotificationUtil.showNotificationError(main, message));
    }
}
