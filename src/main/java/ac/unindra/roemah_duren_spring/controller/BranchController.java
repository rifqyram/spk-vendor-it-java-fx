package ac.unindra.roemah_duren_spring.controller;

import ac.unindra.roemah_duren_spring.JavaFxApplication;
import ac.unindra.roemah_duren_spring.constant.ConstantPage;
import ac.unindra.roemah_duren_spring.model.request.BranchRequest;
import ac.unindra.roemah_duren_spring.model.response.BranchResponse;
import ac.unindra.roemah_duren_spring.model.response.CommonResponse;
import ac.unindra.roemah_duren_spring.util.*;
import atlantafx.base.theme.Styles;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.util.Pair;
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.material2.Material2AL;
import org.kordamp.ikonli.material2.Material2MZ;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;

import java.net.URL;
import java.util.*;

import static ac.unindra.roemah_duren_spring.util.FXMLUtil.loadFXML;

public class BranchController implements Initializable {
    @FXML
    public AnchorPane main;
    @FXML
    public TextField nameField;
    @FXML
    public Label nameLabelError;
    @FXML
    public TextArea addressField;
    @FXML
    public Label addressLabelError;
    @FXML
    public TextField mobilePhoneField;
    @FXML
    public Label mobilePhoneLabelError;
    @FXML
    public Button submitBtn;
    @FXML
    public Button resetBtn;
    @FXML
    public Button removeBtn;
    @FXML
    public TableView<BranchResponse> tableBranch;
    @FXML
    public TableColumn<BranchResponse, Integer> noCol;
    @FXML
    public TableColumn<BranchResponse, String> nameCol;
    @FXML
    public TableColumn<BranchResponse, String> addressCol;
    @FXML
    public TableColumn<BranchResponse, String> mobilePhoneNoCol;
    @FXML
    public Pagination pagination;
    private ObservableList<BranchResponse> branches;

    private final WebClientUtil webClient;
    private BranchResponse selectedBranch;

    public BranchController() {
        this.webClient = JavaFxApplication.getBean(WebClientUtil.class);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        submitBtn.setGraphic(new FontIcon(Material2MZ.SAVE));
        resetBtn.setGraphic(new FontIcon(Material2MZ.REFRESH));
        removeBtn.setGraphic(new FontIcon(Material2AL.DELETE));
        removeBtn.setDisable(true);

        branches = FXCollections.observableArrayList();

        tableBranch.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);
        noCol.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(Integer item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setText(null);
                } else {
                    setText(String.valueOf(getIndex() + 1));
                }
            }
        });
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        addressCol.setCellValueFactory(new PropertyValueFactory<>("address"));
        mobilePhoneNoCol.setCellValueFactory(new PropertyValueFactory<>("mobilePhoneNo"));

        pagination.setPageFactory(number -> {
            webClient.callApiWithQueryParam(
                    "/api/branches",
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<CommonResponse<List<BranchResponse>>>() {
                    },
                    Map.of("page", number.toString(), "size", "10"),
                    response -> FXMLUtil.updateUI(() -> {
                        branches.setAll(response.getData());
                        pagination.setPageCount(response.getPaging().getTotalPages());
                        pagination.setCurrentPageIndex(response.getPaging().getPage());
                        tableBranch.getItems().setAll(branches);
                    }),
                    error -> FXMLUtil.updateUI(() -> NotificationUtil.showNotificationError(main, error.getMessage()))
            );
            return new StackPane();
        });

        tableBranch.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                nameField.setText(newSelection.getName());
                addressField.setText(newSelection.getAddress());
                mobilePhoneField.setText(newSelection.getMobilePhoneNo());
                selectedBranch = newSelection;
                removeBtn.setDisable(false);
            }
        });
    }

    @FXML
    public void handleSubmit(ActionEvent actionEvent) {
        Map<TextInputControl, Pair<Label, ValidationUtil.ValidationStrategy>> validationMap = new HashMap<>();
        validationMap.put(nameField, new Pair<>(nameLabelError, input -> input.isEmpty() ? "Nama Cabang wajib di isi!" : ""));
        validationMap.put(addressField, new Pair<>(addressLabelError, input -> input.isEmpty() ? "Alamat wajib di isi!" : ""));
        validationMap.put(mobilePhoneField, new Pair<>(mobilePhoneLabelError, input -> input.isEmpty() ? "Nomor Telepon wajib di isi!" : ""));
        if (!ValidationUtil.validateForm(validationMap)) return;

        if (selectedBranch == null) {
            submitBtn.setText("Loading...");
            submitBtn.setDisable(true);
            webClient.callApi(
                    "/api/branches",
                    HttpMethod.POST,
                    BranchRequest.builder()
                            .name(nameField.getText())
                            .address(addressField.getText())
                            .mobilePhoneNo(mobilePhoneField.getText())
                            .build(),
                    new ParameterizedTypeReference<CommonResponse<BranchResponse>>() {
                    },
                    response -> FXMLUtil.updateUI(() -> {
                        submitBtn.setText("Submit");
                        submitBtn.setDisable(false);
                        tableBranch.getItems().add(response.getData());
                        handleReset(null);
                        NotificationUtil.showNotificationSuccess(main, response.getMessage());
                    }),
                    error -> {
                        NotificationUtil.showNotificationSuccess(main, error.getMessage());
                        submitBtn.setText("Submit");
                        submitBtn.setDisable(false);
                    }
            );
        } else {
            AlertUtil.confirmDialog(
                    "Konfirmasi ubah",
                    "Konfirmasi ubah",
                    "Apakah yakin data ini ingin dirubah?",
                    () -> {
                        submitBtn.setText("Loading...");
                        submitBtn.setDisable(true);
                        webClient.callApi(
                                "/api/branches",
                                HttpMethod.PUT,
                                BranchRequest.builder()
                                        .id(selectedBranch.getId())
                                        .name(nameField.getText())
                                        .address(addressField.getText())
                                        .mobilePhoneNo(mobilePhoneField.getText())
                                        .build(),
                                new ParameterizedTypeReference<CommonResponse<BranchResponse>>() {
                                },
                                response -> FXMLUtil.updateUI(() -> {
                                    submitBtn.setText("Submit");
                                    submitBtn.setDisable(false);
                                    tableBranch.getItems().setAll(
                                            tableBranch.getItems().stream().map(branchResponse -> {
                                                if (branchResponse.getId().equals(selectedBranch.getId())) {
                                                    return response.getData();
                                                }
                                                return branchResponse;
                                            }).toList()
                                    );
                                    handleReset(null);
                                    NotificationUtil.showNotificationSuccess(main, response.getMessage());
                                }),
                                error -> {
                                    NotificationUtil.showNotificationSuccess(main, error.getMessage());
                                    submitBtn.setText("Submit");
                                    submitBtn.setDisable(false);
                                }
                        );
                    }
            );
        }
    }

    @FXML
    public void handleReset(ActionEvent actionEvent) {
        nameField.setText("");
        addressField.setText("");
        mobilePhoneField.setText("");
        nameLabelError.setText("");
        addressLabelError.setText("");
        mobilePhoneLabelError.setText("");
        selectedBranch = null;
        removeBtn.setDisable(true);
        submitBtn.setDisable(false);
    }

    @FXML
    public void handleDelete(ActionEvent actionEvent) {
        AlertUtil.confirmDialog(
                "Konfismasi Hapus",
                "Konfirmasi Hapus",
                "Apakah anda yakin ingin dihapus",
                () -> {
                    if (selectedBranch != null) {
                        removeBtn.setText("Loading...");
                        removeBtn.setDisable(true);
                        webClient.callApi(
                                "/api/branches/" + selectedBranch.getId(),
                                HttpMethod.DELETE,
                                null,
                                new ParameterizedTypeReference<>() {
                                },
                                response -> FXMLUtil.updateUI(() -> {
                                    branches.remove(selectedBranch);
                                    tableBranch.getItems().setAll(branches);
                                    handleReset(null);
                                    NotificationUtil.showNotificationSuccess(main, response.getMessage());
                                    removeBtn.setText("Hapus");
                                    removeBtn.setDisable(false);
                                }),
                                error -> {
                                    NotificationUtil.showNotificationSuccess(main, error.getMessage());
                                    removeBtn.setText("Hapus");
                                    removeBtn.setDisable(false);
                                }
                        );
                    }
                }
        );
    }
}
