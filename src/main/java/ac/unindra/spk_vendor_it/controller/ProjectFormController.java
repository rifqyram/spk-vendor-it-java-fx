package ac.unindra.spk_vendor_it.controller;

import ac.unindra.spk_vendor_it.JavaFxApplication;
import ac.unindra.spk_vendor_it.constant.ResponseMessage;
import ac.unindra.spk_vendor_it.model.ProjectModel;
import ac.unindra.spk_vendor_it.model.VendorModel;
import ac.unindra.spk_vendor_it.service.ProjectService;
import ac.unindra.spk_vendor_it.util.FXMLUtil;
import ac.unindra.spk_vendor_it.util.NotificationUtil;
import ac.unindra.spk_vendor_it.util.ValidationUtil;
import javafx.concurrent.Task;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.util.Pair;
import lombok.Setter;

import java.net.URL;
import java.util.Map;
import java.util.ResourceBundle;

public class ProjectFormController implements Initializable {
    public TextField textFieldName;
    public Label nameLabelError;
    public DatePicker startDateField;
    public Label startDateLabelError;
    public DatePicker endDateField;
    public Label endDateLabelError;
    public TextArea descriptionField;
    public Label descriptionLabelError;
    public Button btnSubmit;
    public Button cancelBtn;
    public AnchorPane main;

    private ProjectModel selectedProject;

    @Setter
    private AnchorPane ownerPane;

    @Setter
    private Runnable onFormSubmit;

    private final ProjectService projectService;

    public ProjectFormController() {
        this.projectService = JavaFxApplication.getBean(ProjectService.class);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }

    public void updateForm(ProjectModel projectModel) {
        selectedProject = projectModel;
        textFieldName.setText(projectModel.getName());
        descriptionField.setText(projectModel.getDescription());
        startDateField.setValue(projectModel.getStartDate());
        endDateField.setValue(projectModel.getEndDate());
    }

    public ProjectModel createProjectModel() {
        return ProjectModel.builder()
                .id(selectedProject != null ? selectedProject.getId() : null)
                .name(textFieldName.getText())
                .description(descriptionField.getText())
                .startDate(startDateField.getValue())
                .endDate(endDateField.getValue())
                .build();
    }

    public void handleSubmit() {
        if (!ValidationUtil.isFormValid(getValidationMap()) && !ValidationUtil.isFormValidComboBox(getValidationComboBox())) {
            return;
        }
        if (selectedProject == null) {
            processCreate();
        } else {
            processUpdate();
        }
    }

    private void processUpdate() {
        Task<Void> task = new Task<>() {
            @Override
            protected Void call() {
                ProjectModel projectModel = createProjectModel();
                projectService.update(projectModel);
                return null;
            }
        };
        task.setOnSucceeded(event -> handleResponseSuccess(ResponseMessage.SUCCESS_UPDATE));
        task.setOnFailed(event -> handleResponseError(event.getSource().getException().getMessage()));
        task.setOnCancelled(event -> handleResponseError(event.getSource().getException().getMessage()));
        new Thread(task).start();
    }

    private void processCreate() {
        Task<Void> task = new Task<>() {
            @Override
            protected Void call() {
                ProjectModel projectModel = createProjectModel();
                projectService.create(projectModel);
                return null;
            }
        };
        task.setOnSucceeded(event -> handleResponseSuccess(ResponseMessage.SUCCESS_SAVE));
        task.setOnFailed(event -> handleResponseError(event.getSource().getException().getMessage()));
        task.setOnCancelled(event -> handleResponseError(event.getSource().getException().getMessage()));
        new Thread(task).start();
    }

    private Map<TextInputControl, Pair<Label, ValidationUtil.ValidationStrategy>> getValidationMap() {
        return Map.of(
                textFieldName, new Pair<>(nameLabelError, ValidationUtil.ValidationStrategy.REQUIRED),
                descriptionField, new Pair<>(descriptionLabelError, ValidationUtil.ValidationStrategy.REQUIRED)
        );
    }

    public Map<ComboBoxBase<?>, Pair<Label, ValidationUtil.ValidationStrategyComboBox>> getValidationComboBox() {
        return Map.of(
                startDateField, new Pair<>(startDateLabelError, input -> input.getValue() == null ? "Pilih tanggal awal" : ""),
                endDateField, new Pair<>(endDateLabelError, input -> input.getValue() == null ? "Pilih tanggal akhir" : "")
        );
    }

    public void handleClose() {
        if (onFormSubmit != null) {
            onFormSubmit.run();
        }
        Stage stage = (Stage) main.getScene().getWindow();
        stage.close();
    }

    private void handleResponseSuccess(String message) {
        FXMLUtil.updateUI(() -> {
            NotificationUtil.showNotificationSuccess(ownerPane, message);
            handleClose();
        });
    }

    private void handleResponseError(String message) {
        FXMLUtil.updateUI(() -> NotificationUtil.showNotificationError(ownerPane, message));
    }
}
