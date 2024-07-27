package ac.unindra.spk_vendor_it.controller;

import ac.unindra.spk_vendor_it.JavaFxApplication;
import ac.unindra.spk_vendor_it.model.ComboBoxValueModel;
import ac.unindra.spk_vendor_it.model.CriteriaModel;
import ac.unindra.spk_vendor_it.model.WeightCriteriaModel;
import ac.unindra.spk_vendor_it.service.CriteriaService;
import ac.unindra.spk_vendor_it.util.TableUtil;
import ac.unindra.spk_vendor_it.util.*;
import javafx.concurrent.Task;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.util.Pair;
import lombok.Setter;
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.material2.Material2AL;
import org.kordamp.ikonli.material2.Material2MZ;

import java.net.URL;
import java.util.*;

public class CriteriaFormController implements Initializable {

    public AnchorPane main;
    public TextField nameField;
    public Label nameLabelError;
    public ComboBox<String> categoryComboBox;
    public Label categoryLabelError;
    public TextField descriptionField;
    public Label descriptionLabelError;
    public ComboBox<ComboBoxValueModel<Integer>> weightCriteriaComboBox;
    public Label weightCriteriaLabelError;
    public TextField weightSubCriteriaField;
    public Label weightSubCriteriaLabelError;
    public TextField descriptionSubCriteriaField;
    public Label descriptionSubCriteriaLabelError;
    public Button btnAddSubCriteria;
    public TableView<WeightCriteriaModel> tableWeight;
    public TableColumn<WeightCriteriaModel, Integer> noCol;
    public TableColumn<WeightCriteriaModel, Integer> weightCol;
    public TableColumn<WeightCriteriaModel, String> descriptionWeightCol;
    public TableColumn<WeightCriteriaModel, Void> actionsCol;
    public Button btnSubmit;
    public Button cancelBtn;

    private CriteriaModel criteriaModel;

    @Setter
    private AnchorPane ownerPane;
    @Setter
    private Runnable onFormSubmit;

    private final List<ComboBoxValueModel<Integer>> weightCriteriaList = new ArrayList<>();

    private final CriteriaService criteriaService;

    public CriteriaFormController() {
        this.criteriaService = JavaFxApplication.getBean(CriteriaService.class);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setupButtonIcons();
        setupComboBox();
        setupTable();
        TextFieldUtil.changeValueToNumber(weightSubCriteriaField);
    }

    private void setupTable() {
        TableUtil.setColumnResizePolicy(tableWeight);
        TableUtil.setTableSequence(noCol);
        weightCol.setCellValueFactory(new PropertyValueFactory<>("weight"));
        descriptionWeightCol.setCellValueFactory(new PropertyValueFactory<>("description"));
        actionsCol.setCellFactory(col -> TableUtil.setTableDeleteAction((table, index) -> {
            WeightCriteriaModel weightCriteriaModel = tableWeight.getItems().get(index);
            table.getItems().remove(weightCriteriaModel);
        }));
    }

    public void updateForm(CriteriaModel criteriaModel) {
        this.criteriaModel = criteriaModel;
        nameField.setText(criteriaModel.getName());
        categoryComboBox.setValue(criteriaModel.getCategory());
        descriptionField.setText(criteriaModel.getDescription());
        tableWeight.getItems().setAll(criteriaModel.getSubCriteria());

        ComboBoxValueModel<Integer> weightCriteria = weightCriteriaList.stream()
                .filter(item -> item.getValue().equals(criteriaModel.getWeight()))
                .findFirst()
                .orElse(null);
        weightCriteriaComboBox.setValue(weightCriteria);
    }

    private void setupComboBox() {
        categoryComboBox.getItems().clear();
        categoryComboBox.getItems().addAll("Benefit", "Cost");

        weightCriteriaList.clear();
        weightCriteriaList.add(new ComboBoxValueModel<>("1 | Tidak Penting", 1));
        weightCriteriaList.add(new ComboBoxValueModel<>("2 | Kurang Penting", 2));
        weightCriteriaList.add(new ComboBoxValueModel<>("3 | Cukup Penting", 3));
        weightCriteriaList.add(new ComboBoxValueModel<>("4 | Penting", 4));
        weightCriteriaList.add(new ComboBoxValueModel<>("5 | Sangat Penting", 5));
        weightCriteriaComboBox.getItems().setAll(weightCriteriaList);

        weightCriteriaComboBox.setButtonCell(ComboBoxUtil.getComboBoxListCell(ComboBoxValueModel::getLabel));
        weightCriteriaComboBox.setCellFactory(col -> ComboBoxUtil.getComboBoxListCell(ComboBoxValueModel::getLabel));
    }

    public void addSubCriteria() {
        if (!ValidationUtil.isFormValid(getMapValidationSubCriteria())) return;
        WeightCriteriaModel weightCriteriaModel = createWeightCriteriaModel();
        tableWeight.getItems().add(weightCriteriaModel);
        weightSubCriteriaField.clear();
        descriptionSubCriteriaField.clear();
    }

    public void handleSubmit() {
        if (!ValidationUtil.isFormValid(getMapValidationCriteria()) && !ValidationUtil.isFormValidComboBox(getValidationComboBoxMap())) return;
        if (criteriaModel == null) {
            processCreate();
        } else {
            processUpdate();
        }
    }

    public void handleClose() {
        if (onFormSubmit != null) {
            onFormSubmit.run();
        }
        Stage stage = (Stage) main.getScene().getWindow();
        stage.close();
    }

    private void processCreate() {
        CriteriaModel criteria = createCriteriaModel();
        Task<Void> task = new Task<>() {
            @Override
            protected Void call() {
                criteriaService.create(criteria);
                return null;
            }
        };

        task.setOnFailed(event -> handleResponseError(task.getException().getMessage()));
        task.setOnSucceeded(event -> handleResponseSuccess("Kriteria berhasil ditambahkan"));
        task.setOnCancelled(event -> handleResponseError(event.getSource().getException().getMessage()));

        new Thread(task).start();
    }

    private void processUpdate() {
        CriteriaModel criteria = createCriteriaModel();
        Task<Void> task = new Task<>() {
            @Override
            protected Void call() {
                criteriaService.update(criteria);
                return null;
            }
        };

        task.setOnFailed(event -> handleResponseError(task.getException().getMessage()));
        task.setOnSucceeded(event -> handleResponseSuccess("Kriteria berhasil dirubah"));
        task.setOnCancelled(event -> handleResponseError(event.getSource().getException().getMessage()));

        new Thread(task).start();
    }

    public void handleResponseSuccess(String message) {
        FXMLUtil.updateUI(() -> {
            NotificationUtil.showNotificationSuccess(ownerPane, message);
            handleClose();
        });
    }

    private void handleResponseError(String message) {
        FXMLUtil.updateUI(() -> NotificationUtil.showNotificationError(ownerPane, message));
    }

    private CriteriaModel createCriteriaModel() {
        return CriteriaModel.builder()
                .id(criteriaModel == null ? null : criteriaModel.getId())
                .name(nameField.getText())
                .category(categoryComboBox.getValue())
                .description(descriptionField.getText())
                .weight(weightCriteriaComboBox.getValue().getValue())
                .subCriteria(tableWeight.getItems())
                .build();
    }

    private WeightCriteriaModel createWeightCriteriaModel() {
        return WeightCriteriaModel.builder()
                .weight(Integer.parseInt(weightSubCriteriaField.getText()))
                .description(descriptionSubCriteriaField.getText())
                .build();
    }

    private void setupButtonIcons() {
        btnSubmit.setGraphic(new FontIcon(Material2MZ.SAVE));
        cancelBtn.setGraphic(new FontIcon(Material2AL.CANCEL));
    }

    private Map<TextInputControl, Pair<Label, ValidationUtil.ValidationStrategy>> getMapValidationCriteria() {
        Map<TextInputControl, Pair<Label, ValidationUtil.ValidationStrategy>> validationMap = new HashMap<>();
        validationMap.put(nameField, new Pair<>(nameLabelError, ValidationUtil.ValidationStrategy.REQUIRED));
        validationMap.put(descriptionField, new Pair<>(descriptionLabelError, ValidationUtil.ValidationStrategy.REQUIRED));
        return validationMap;
    }



    private Map<ComboBoxBase<?>, Pair<Label, ValidationUtil.ValidationStrategyComboBox>> getValidationComboBoxMap() {
        return Map.of(
                categoryComboBox, new Pair<>(categoryLabelError, input -> input.getValue() == null ? "Kategori harus dipilih" : null),
                weightCriteriaComboBox, new Pair<>(weightCriteriaLabelError, input -> input.getValue() == null ? "Bobot harus dipilih" : null)
        );
    }

    private Map<TextInputControl, Pair<Label, ValidationUtil.ValidationStrategy>> getMapValidationSubCriteria() {
        Map<TextInputControl, Pair<Label, ValidationUtil.ValidationStrategy>> validationMap = new HashMap<>();
        validationMap.put(weightSubCriteriaField, new Pair<>(weightSubCriteriaLabelError, ValidationUtil.ValidationStrategy.NUMBER));
        validationMap.put(descriptionSubCriteriaField, new Pair<>(descriptionSubCriteriaLabelError, ValidationUtil.ValidationStrategy.REQUIRED));
        return validationMap;
    }
}
