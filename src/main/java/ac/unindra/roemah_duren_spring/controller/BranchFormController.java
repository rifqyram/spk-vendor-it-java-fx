package ac.unindra.roemah_duren_spring.controller;

import ac.unindra.roemah_duren_spring.JavaFxApplication;
import ac.unindra.roemah_duren_spring.constant.ResponseMessage;
import ac.unindra.roemah_duren_spring.model.Branch;
import ac.unindra.roemah_duren_spring.service.BranchService;
import ac.unindra.roemah_duren_spring.util.FXMLUtil;
import ac.unindra.roemah_duren_spring.util.NotificationUtil;
import ac.unindra.roemah_duren_spring.util.ValidationUtil;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.util.Pair;
import lombok.Setter;
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.material2.Material2AL;
import org.kordamp.ikonli.material2.Material2MZ;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

public class BranchFormController implements Initializable {
    public TextField textFieldName;
    public Label nameLabelError;
    public TextField mobilePhoneNumberField;
    public Label mobilePhoneLabelError;
    public TextArea addressField;
    public Label addressLabelError;
    public Button btnSubmit;
    public Button cancelBtn;
    public AnchorPane main;
    public TextField textFieldCode;
    public Label branchLabelError;

    private Branch selectedBranch;

    @Setter
    private AnchorPane ownerPane;

    @Setter
    private Runnable onFormSubmit;

    private final BranchService branchService;

    public BranchFormController() {
        this.branchService = JavaFxApplication.getBean(BranchService.class);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setupButtonIcons();
    }

    public void updateForm(Branch branch) {
        textFieldCode.setText(branch.getCode());
        textFieldName.setText(branch.getName());
        mobilePhoneNumberField.setText(branch.getMobilePhoneNo());
        addressField.setText(branch.getAddress());
        selectedBranch = branch;
    }

    public void handleSubmit() {
        if (!ValidationUtil.isFormValid(getMapValidation())) return;
        if (selectedBranch == null) {
            processCreate();
        } else {
            processUpdate();
        }
    }

    public void handleClose() {
        if (onFormSubmit != null) onFormSubmit.run();
        Stage stage = (Stage) main.getScene().getWindow();
        stage.close();
    }

    private void processCreate() {
        Branch branch = createBranchModel();
        branchService.createBranch(
                branch,
                response -> handleResponseSuccess(ResponseMessage.SUCCESS_CREATE),
                response -> handleResponseError(response.getMessage())
        );
    }

    private void processUpdate() {
        Branch branch = createBranchModel();
        branchService.updateBranch(
                branch,
                response -> handleResponseSuccess(ResponseMessage.SUCCESS_UPDATE),
                response -> handleResponseError(response.getMessage())
        );
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

    private Branch createBranchModel () {
        Branch branch = new Branch();
        branch.setId(selectedBranch != null ? selectedBranch.getId() : null);
        branch.setCode(textFieldCode.getText());
        branch.setName(textFieldName.getText());
        branch.setMobilePhoneNo(mobilePhoneNumberField.getText());
        branch.setAddress(addressField.getText());
        return branch;
    }

    private void setupButtonIcons() {
        btnSubmit.setGraphic(new FontIcon(Material2MZ.SAVE));
        cancelBtn.setGraphic(new FontIcon(Material2AL.CANCEL));
    }

    private Map<TextInputControl, Pair<Label, ValidationUtil.ValidationStrategy>> getMapValidation() {
        Map<TextInputControl, Pair<Label, ValidationUtil.ValidationStrategy>> validationMap = new HashMap<>();
        validationMap.put(textFieldName, new Pair<>(nameLabelError, ValidationUtil.ValidationStrategy.REQUIRED));
        validationMap.put(mobilePhoneNumberField, new Pair<>(mobilePhoneLabelError, ValidationUtil.ValidationStrategy.PHONE_NUMBER));
        validationMap.put(addressField, new Pair<>(addressLabelError, ValidationUtil.ValidationStrategy.REQUIRED));
        return validationMap;
    }
}
