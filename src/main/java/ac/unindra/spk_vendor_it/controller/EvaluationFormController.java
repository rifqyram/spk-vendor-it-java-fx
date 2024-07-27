package ac.unindra.spk_vendor_it.controller;

import ac.unindra.spk_vendor_it.JavaFxApplication;
import ac.unindra.spk_vendor_it.constant.ResponseMessage;
import ac.unindra.spk_vendor_it.model.*;
import ac.unindra.spk_vendor_it.service.*;
import ac.unindra.spk_vendor_it.util.*;
import atlantafx.base.theme.Styles;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.ListChangeListener;
import javafx.concurrent.Task;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Pair;
import lombok.Setter;
import org.springframework.util.StringUtils;

import java.net.URL;
import java.util.*;

public class EvaluationFormController implements Initializable {

    public AnchorPane main;
    public TableView<EvaluationDetailModel> tableEvaluation;
    public ComboBox<VendorModel> vendorComboBox;
    public Label vendorLabelError;
    public VBox criteriaContainer;
    public ComboBox<ProjectModel> projectComboBox;
    public Label projectLabelError;

    @Setter
    private AnchorPane ownerPane;
    @Setter
    private Runnable onFormSubmit;

    private List<CriteriaModel> criteriaModels;
    private final Map<String, TextInputControl> inputControlMap = new HashMap<>();
    private final Map<String, ComboBoxBase<ComboBoxValueModel<Integer>>> comboBoxBaseMap = new HashMap<>();
    private final Map<String, Label> labelMap = new HashMap<>();

    private final ProjectService projectService;
    private final CriteriaService criteriaService;
    private final VendorService vendorService;
    private final ProjectEvaluationService projectEvaluationService;

    private EvaluationDetailModel selectedDetailModel;


    public EvaluationFormController() {
        this.projectService = JavaFxApplication.getBean(ProjectService.class);
        this.criteriaService = JavaFxApplication.getBean(CriteriaService.class);
        this.vendorService = JavaFxApplication.getBean(VendorService.class);
        this.projectEvaluationService = JavaFxApplication.getBean(ProjectEvaluationService.class);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initProjectComboBox();
        initVendorComboBox();
        initCriteria();
    }

    private void initTable() {
        TableColumn<EvaluationDetailModel, Integer> noCol = new TableColumn<>("No");
        TableUtil.setTableSequence(noCol);
        TableUtil.setColumnResizePolicy(tableEvaluation);

        TableColumn<EvaluationDetailModel, VendorModel> vendorCol = new TableColumn<>("Vendor");
        vendorCol.setCellValueFactory(new PropertyValueFactory<>("vendor"));
        vendorCol.setCellFactory(col -> TableUtil.setTableObject(VendorModel::getName));
        vendorCol.setMinWidth(150);

        tableEvaluation.getColumns().add(noCol);
        tableEvaluation.getColumns().add(vendorCol);

        for (CriteriaModel criteriaModel : criteriaModels) {
            TableColumn<EvaluationDetailModel, Integer> criteriaCol = new TableColumn<>(criteriaModel.getName());
            criteriaCol.setId(criteriaModel.getId());
            criteriaCol.setCellValueFactory(param -> new SimpleIntegerProperty(param.getValue().getCriteria().get(criteriaModel.getId())).asObject());
            criteriaCol.setMinWidth(150);
            tableEvaluation.getColumns().add(criteriaCol);
        }

        TableColumn<EvaluationDetailModel, Void> actionCol = new TableColumn<>("Actions");
        actionCol.setMinWidth(150);
        actionCol.setCellFactory(col -> TableUtil.setTableActions(
                (table, index) -> {
                    EvaluationDetailModel evaluationDetailModel = table.getItems().get(index);
                    selectedDetailModel = evaluationDetailModel;

                    Map<String, Integer> criteria = evaluationDetailModel.getCriteria();

                    vendorComboBox.getItems().add(evaluationDetailModel.getVendor());
                    vendorComboBox.setValue(evaluationDetailModel.getVendor());

                    for (Map.Entry<String, TextInputControl> entry : inputControlMap.entrySet()) {
                        String key = entry.getKey();
                        TextInputControl textInputControl = entry.getValue();
                        textInputControl.setText(String.valueOf(criteria.get(key)));
                    }

                    for (Map.Entry<String, ComboBoxBase<ComboBoxValueModel<Integer>>> entry : comboBoxBaseMap.entrySet()) {
                        String key = entry.getKey();
                        ComboBox<ComboBoxValueModel<Integer>> comboBoxBase = (ComboBox<ComboBoxValueModel<Integer>>) entry.getValue();
                        comboBoxBase.setValue(comboBoxBase.getItems().stream().filter(comboBoxValueModel -> comboBoxValueModel.getValue().equals(criteria.get(key))).findFirst().orElse(null));
                    }
                },
                (table, index) -> {
                    EvaluationDetailModel evaluationDetailModel = table.getItems().get(index);
                    table.getItems().remove(evaluationDetailModel);
                }
        ));

        // remove vendor combo box item list if exist on tableEvaluation
        tableEvaluation.getItems().addListener((ListChangeListener<? super EvaluationDetailModel>) change -> {
            if (tableEvaluation.getItems().isEmpty()) {
                vendorComboBox.getItems().clear();
                initVendorComboBox();
            } else {
                vendorComboBox.getItems().removeAll(tableEvaluation.getItems().stream().map(EvaluationDetailModel::getVendor).toList());
            }
        });

        tableEvaluation.getColumns().add(actionCol);
    }

    public void handleSubmit() {
        Map<ComboBoxBase<?>, Pair<Label, ValidationUtil.ValidationStrategyComboBox>> map = new HashMap<>();
        map.put(projectComboBox, new Pair<>(projectLabelError, input -> input.getValue() == null ? "Proyek harus dipilih" : null));
        if (!ValidationUtil.isFormValidComboBox(map) && tableEvaluation.getItems().isEmpty()) {
            FXMLUtil.updateUI(() -> NotificationUtil.showNotificationError(main, "Evaluasi tidak boleh kosong"));
            return;
        }

        AlertUtil.confirmDialog("Konfirmasi Simpan Data", "Konfirmasi", "Apakah anda yakin ingin menyimpan data evaluasi?", () -> {
            EvaluationModel evaluationModel = new EvaluationModel();
            evaluationModel.setProject(projectComboBox.getValue());
            evaluationModel.setEvaluationDetails(tableEvaluation.getItems());

            Task<Void> task = new Task<>() {
                @Override
                protected Void call() {
                    projectEvaluationService.create(evaluationModel);
                    return null;
                }
            };

            task.setOnSucceeded(event -> handleResponseSuccess());
            task.setOnCancelled(event -> handleResponseError(event.getSource().getException().getMessage()));
            task.setOnFailed(event -> handleResponseError(event.getSource().getException().getMessage()));

            new Thread(task).start();
        }, () -> {
        });
    }

    public void handleClose() {
        if (onFormSubmit != null) {
            onFormSubmit.run();
        }
        Stage stage = (Stage) main.getScene().getWindow();
        stage.close();
    }

    public void addEvaluation() {
        if (!ValidationUtil.isFormValid(getMapValidationTextInput()) && !ValidationUtil.isFormValidComboBox(getValidationComboBoxEvaluationMap())) {
            return;
        }

        Map<String, Control> map = new HashMap<>();
        map.putAll(inputControlMap);
        map.putAll(comboBoxBaseMap);

        // if selectedDetailModel is not null, then update the selectedDetailModel

        if (selectedDetailModel != null) {
            selectedDetailModel.setVendor(vendorComboBox.getValue());
            Map<String, Integer> criteriaMap = selectedDetailModel.getCriteria();

            for (Map.Entry<String, Control> entry : map.entrySet()) {
                String criteriaId = entry.getKey();
                Control control = entry.getValue();

                if (control instanceof TextInputControl textInputControl) {
                    criteriaMap.put(criteriaId, Integer.parseInt(textInputControl.getText()));
                    textInputControl.clear();
                    continue;
                }

                if (control instanceof ComboBoxBase<?> comboBoxBase) {
                    criteriaMap.put(criteriaId, ((ComboBoxValueModel<Integer>) comboBoxBase.getValue()).getValue());
                    comboBoxBase.setValue(null);
                }
            }

            tableEvaluation.refresh();
            selectedDetailModel = null;
            vendorComboBox.setValue(null);

            ValidationUtil.resetValidation(getMapValidationTextInput());
            ValidationUtil.resetValidationComboBox(getValidationComboBoxEvaluationMap());
            return;
        }

        EvaluationDetailModel evaluationDetailModel = new EvaluationDetailModel();
        evaluationDetailModel.setVendor(vendorComboBox.getValue());
        Map<String, Integer> criteriaMap = new HashMap<>();

        for (Map.Entry<String, Control> entry : map.entrySet()) {
            String criteriaId = entry.getKey();
            Control control = entry.getValue();

            if (control instanceof TextInputControl textInputControl) {
                criteriaMap.put(criteriaId, Integer.parseInt(textInputControl.getText()));
                textInputControl.clear();
                continue;
            }

            if (control instanceof ComboBoxBase<?> comboBoxBase) {
                criteriaMap.put(criteriaId, ((ComboBoxValueModel<Integer>) comboBoxBase.getValue()).getValue());
                comboBoxBase.setValue(null);
            }
        }

        evaluationDetailModel.setCriteria(criteriaMap);
        tableEvaluation.getItems().add(evaluationDetailModel);

        vendorComboBox.setValue(null);

        ValidationUtil.resetValidation(getMapValidationTextInput());
        ValidationUtil.resetValidationComboBox(getValidationComboBoxEvaluationMap());
    }

    private void initFormCriteria() {
        if (criteriaModels == null) {
            handleResponseError("Gagal mengambil data kriteria");
            return;
        }

        criteriaModels.forEach(criteriaModel -> {
            TableColumn<EvaluationModel, Integer> newColumn = new TableColumn<>(criteriaModel.getName());
            newColumn.setId(criteriaModel.getId());

            VBox vBox = new VBox();
            Label label = new Label(criteriaModel.getName());
            vBox.getChildren().add(label);

            if (criteriaModel.getSubCriteria().isEmpty()) {
                TextField textField = new TextField();
                TextFieldUtil.changeValueToNumber(textField);
                VBox.setMargin(textField, new Insets(8, 0, 8, 0));
                Label helperText = new Label("Masukkan nilai " + criteriaModel.getName());
                helperText.setStyle("-fx-font-size: 10px;");
                vBox.getChildren().add(textField);
                vBox.getChildren().add(helperText);
                inputControlMap.put(criteriaModel.getId(), textField);
                textField.minWidth(Double.MAX_VALUE);
            } else {
                ComboBox<ComboBoxValueModel<Integer>> comboBox = new ComboBox<>();
                criteriaModel.getSubCriteria().stream().sorted(Comparator.comparingInt(WeightCriteriaModel::getWeight)).forEach(subCriteriaModel -> {
                    ComboBoxValueModel<Integer> comboBoxValueModel = new ComboBoxValueModel<>(subCriteriaModel.getWeight() + " | " + subCriteriaModel.getDescription(), subCriteriaModel.getWeight());
                    comboBox.getItems().add(comboBoxValueModel);
                });
                VBox.setMargin(comboBox, new Insets(8, 0, 8, 0));
                Label helperText = new Label("Pilih nilai " + criteriaModel.getName());
                helperText.setStyle("-fx-font-size: 10px;");
                vBox.getChildren().add(comboBox);
                vBox.getChildren().add(helperText);
                comboBoxBaseMap.put(criteriaModel.getId(), comboBox);
                comboBox.minWidth(Double.MAX_VALUE);
            }

            Label labelError = new Label();
            labelError.setStyle("-fx-font-size: 12px;");
            labelError.getStyleClass().add(Styles.DANGER);
            labelMap.put(criteriaModel.getId(), labelError);
            vBox.getChildren().add(labelError);

            criteriaContainer.getChildren().add(vBox);
        });
    }

    private void initCriteria() {
        Task<List<CriteriaModel>> task = new Task<>() {
            @Override
            protected List<CriteriaModel> call() {
                return criteriaService.getAll();
            }
        };
        task.setOnSucceeded(event -> {
            criteriaModels = task.getValue();
            initFormCriteria();
            initTable();
        });
        task.setOnFailed(event -> handleResponseError(task.getException().getMessage()));
        task.setOnCancelled(event -> handleResponseError(task.getException().getMessage()));
        new Thread(task).start();
    }

    private void initVendorComboBox() {
        Task<List<VendorModel>> task = new Task<>() {
            @Override
            protected List<VendorModel> call() {
                return vendorService.getAll();
            }
        };

        task.setOnSucceeded(event -> {
            List<VendorModel> vendorModels = task.getValue();
            vendorComboBox.getItems().setAll(vendorModels);
            vendorComboBox.setButtonCell(ComboBoxUtil.getComboBoxListCell(VendorModel::getName));
            vendorComboBox.setCellFactory(col -> ComboBoxUtil.getComboBoxListCell(VendorModel::getName));
        });
        task.setOnFailed(event -> handleResponseError(task.getException().getMessage()));
        task.setOnCancelled(event -> handleResponseError(task.getException().getMessage()));

        new Thread(task).start();
    }

    private void initProjectComboBox() {
        Task<List<ProjectModel>> task = new Task<>() {
            @Override
            protected List<ProjectModel> call() {
                return projectService.getAll();
            }
        };

        task.setOnSucceeded(event -> {
            List<ProjectModel> projectModels = task.getValue();
            projectComboBox.getItems().setAll(projectModels);
            projectComboBox.setButtonCell(ComboBoxUtil.getComboBoxListCell(ProjectModel::getName));
            projectComboBox.setCellFactory(col -> ComboBoxUtil.getComboBoxListCell(ProjectModel::getName));
        });
        task.setOnFailed(event -> handleResponseError(task.getException().getMessage()));
        task.setOnCancelled(event -> handleResponseError(task.getException().getMessage()));

        new Thread(task).start();
    }

    private void handleResponseSuccess() {
        FXMLUtil.updateUI(() -> {
            NotificationUtil.showNotificationSuccess(ownerPane, ResponseMessage.SUCCESS_SAVE);
            handleClose();
        });
    }

    private void handleResponseError(String message) {
        FXMLUtil.updateUI(() -> NotificationUtil.showNotificationError(ownerPane, message));
    }

    private Map<ComboBoxBase<?>, Pair<Label, ValidationUtil.ValidationStrategyComboBox>> getValidationComboBoxEvaluationMap() {
        Map<ComboBoxBase<?>, Pair<Label, ValidationUtil.ValidationStrategyComboBox>> map = new HashMap<>();
        map.put(vendorComboBox, new Pair<>(vendorLabelError, input -> input.getValue() == null ? "Vendor harus dipilih" : null));

        for (Map.Entry<String, ComboBoxBase<ComboBoxValueModel<Integer>>> entry : comboBoxBaseMap.entrySet()) {
            String s = entry.getKey();
            ComboBoxBase<?> comboBoxBase = entry.getValue();
            Label errorLabel = null;
            if (labelMap.containsKey(s)) {
                errorLabel = labelMap.get(s);
            }
            map.put(comboBoxBase, new Pair<>(errorLabel, input -> input.getValue() == null ? "Kriteria harus dipilih" : null));
        }

        return map;
    }

    private Map<TextInputControl, Pair<Label, ValidationUtil.ValidationStrategy>> getMapValidationTextInput() {
        Map<TextInputControl, Pair<Label, ValidationUtil.ValidationStrategy>> validationMap = new HashMap<>();

        for (Map.Entry<String, TextInputControl> entry : inputControlMap.entrySet()) {
            String s = entry.getKey();
            TextInputControl textInputControl = entry.getValue();
            Label errorLabel = null;
            if (labelMap.containsKey(s)) {
                errorLabel = labelMap.get(s);
            }
            validationMap.put(textInputControl, new Pair<>(errorLabel, input -> {
                if (!StringUtils.hasText(input)) {
                    return "Nilai kriteria harus diisi";
                } else if (!input.matches("[1-9]+")) {
                    return "Nilai kriteria ini hanya boleh diisi dengan angka, dan tidak boleh 0";
                }
                return "";
            }));
        }

        return validationMap;
    }
}
