package ac.unindra.spk_vendor_it.controller;

import ac.unindra.spk_vendor_it.JavaFxApplication;
import ac.unindra.spk_vendor_it.constant.UserRole;
import ac.unindra.spk_vendor_it.entity.UserCredential;
import ac.unindra.spk_vendor_it.service.AuthService;
import ac.unindra.spk_vendor_it.util.FXMLUtil;
import ac.unindra.spk_vendor_it.util.NotificationUtil;
import ac.unindra.spk_vendor_it.util.StringUtil;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

import static ac.unindra.spk_vendor_it.util.FXMLUtil.loadFXML;

public class MainController implements Initializable {

    public AnchorPane main;
    public Button navDashboard;
    public Button navVendor;
    public Button navProject;
    public Button navUser;
    public Button navTransaction;
    public Button navLogout;
    public Button navSettingsProfile;
    public ScrollPane contentBox;
    private final AuthService authService;
    public Label adminName;
    public Label roleAdmin;
    public Button navEvaluationResult;


    public MainController() {
        this.authService = JavaFxApplication.getBean(AuthService.class);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        loadUserInfo();
        loadPage("dashboard");
        initRole();
    }

    private void initRole() {
        UserCredential userInfo = authService.getUserInfo();
        if (userInfo.getRole().equals(UserRole.EMPLOYEE)) {
            navUser.setDisable(true);
            navTransaction.setDisable(true);
        }
    }

    private void loadUserInfo() {
        Task<UserCredential> task = new Task<>() {
            @Override
            protected UserCredential call() {
                return authService.getUserInfo();
            }
        };

        task.setOnSucceeded(event -> {
            UserCredential user = task.getValue();
            adminName.setText(StringUtil.toTitleCase(user.getUsername()));
            roleAdmin.setText(user.getRole().getDescription());
        });

        task.setOnFailed(e -> NotificationUtil.showNotificationError(main, "Gagal memuat data user, silahkan login ulang"));

        new Thread(task).start();
    }

    private void loadPage(String page) {
        Parent root = loadFXML(page);
        contentBox.setContent(root);
    }

    public void toDashboardPage() {
        loadPage("dashboard");
    }

    public void toCriteriaPage() {
        loadPage("criteria");
    }

    public void toVendorPage() {
        loadPage("vendor");
    }

    public void toProjectPage() {
        loadPage("project");
    }

    public void toUserPage() {
        loadPage("user");
    }

    public void toEvaluationPage() {
        loadPage("evaluation");
    }

    public void toEvaluationResultPage() {
        loadPage("evaluation_result");
    }

    public void toSettingsPage() {
        loadPage("profile_setting");
    }

    public void handleLogout() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation Dialog");
        alert.setHeaderText("Konfirmasi Logout");
        alert.setContentText("Apakah anda yakin ingin Logout?");

        ButtonType yesBtn = new ButtonType("Ya", ButtonBar.ButtonData.YES);
        ButtonType noBtn = new ButtonType("Tidak", ButtonBar.ButtonData.NO);
        alert.getButtonTypes().setAll(yesBtn, noBtn);

        Optional<ButtonType> result = alert.showAndWait();

        if (result.isPresent() && result.get().equals(yesBtn)) {
            authService.logout();
            Stage stage = (Stage) main.getScene().getWindow();
            stage.close();
            FXMLUtil.navigationStage(main, "login", "Login", false);
        }
    }
}
