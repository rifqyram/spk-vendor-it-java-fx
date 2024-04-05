package ac.unindra.roemah_duren_spring.controller;

import ac.unindra.roemah_duren_spring.JavaFxApplication;
import ac.unindra.roemah_duren_spring.constant.ConstantPage;
import ac.unindra.roemah_duren_spring.repository.TokenManager;
import ac.unindra.roemah_duren_spring.util.FXMLUtil;
import atlantafx.base.theme.Styles;
import atlantafx.base.theme.Tweaks;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.material2.Material2AL;
import org.kordamp.ikonli.material2.Material2MZ;

import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

import static ac.unindra.roemah_duren_spring.util.FXMLUtil.loadFXML;

public class MainController implements Initializable {
    @FXML
    public Button navDashboard;
    @FXML
    public Button navSupplier;
    @FXML
    public Button navCustomer;
    @FXML
    public Button navProduct;
    @FXML
    public Button navBranch;
    @FXML
    public Button navStock;
    @FXML
    public Button navTransaction;
    @FXML
    public Button navOutCome;
    @FXML
    public Button navEditProfile;
    @FXML
    public Button navLogout;
    @FXML
    public Label masterLabel;
    @FXML
    public Label transactionLabel;
    @FXML
    public Label settingLabel;
    @FXML
    public ScrollPane contentBox;
    @FXML
    public AnchorPane main;

    private final TokenManager tokenManager;


    public MainController() {
        this.tokenManager = JavaFxApplication.getBean(TokenManager.class);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        navDashboard.setGraphic(new FontIcon(Material2AL.DASHBOARD));
        navSupplier.setGraphic(new FontIcon(Material2AL.BUSINESS));
        navCustomer.setGraphic(new FontIcon(Material2MZ.PERSON));
        navProduct.setGraphic(new FontIcon(Material2AL.FASTFOOD));
        navBranch.setGraphic(new FontIcon(Material2AL.LOCAL_CONVENIENCE_STORE));
        navStock.setGraphic(new FontIcon(Material2AL.BAR_CHART));
        navTransaction.setGraphic(new FontIcon(Material2AL.ADD_SHOPPING_CART));
        navOutCome.setGraphic(new FontIcon(Material2AL.ATTACH_MONEY));
        navEditProfile.setGraphic(new FontIcon(Material2AL.APP_SETTINGS_ALT));
        navLogout.setGraphic(new FontIcon(Material2AL.EXIT_TO_APP));

        FXMLUtil.updateUI(() -> loadPage(ConstantPage.DASHBOARD));
    }

    @FXML
    public void toDashboardPage(ActionEvent e) {
        loadPage(ConstantPage.DASHBOARD);
    }

    @FXML
    public void toSupplierPage(ActionEvent actionEvent) {
        loadPage(ConstantPage.SUPPLIER);
    }

    @FXML
    public void toCustomerPage(ActionEvent actionEvent) {
        loadPage(ConstantPage.CUSTOMER);
    }

    @FXML
    public void toProductPage(ActionEvent e) {
        loadPage(ConstantPage.PRODUCT);
    }

    @FXML
    public void toBranchPage(ActionEvent e) {
        loadPage(ConstantPage.BRANCH);
    }

    @FXML
    public void toStockPage(ActionEvent e) {
        loadPage(ConstantPage.STOCK);
    }

    @FXML
    public void toTransactionPage(ActionEvent e) {
        loadPage(ConstantPage.TRANSACTION);
    }

    @FXML
    public void toOutcomePage(ActionEvent e) {
        loadPage(ConstantPage.OUTCOME);
    }

    @FXML
    public void toProfilePage(ActionEvent e) {
        loadPage(ConstantPage.PROFILE);
    }
    private void loadPage(String page) {
        Parent root = loadFXML(page);
        contentBox.setContent(root);
    }

    @FXML
    public void handleLogout(ActionEvent e) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation Dialog");
        alert.setHeaderText("Konfirmasi Logout");
        alert.setContentText("Apakah anda yakin ingin Logout?");

        ButtonType yesBtn = new ButtonType("Ya", ButtonBar.ButtonData.YES);
        ButtonType noBtn = new ButtonType("Tidak", ButtonBar.ButtonData.NO);
        alert.getButtonTypes().setAll(yesBtn, noBtn);

        Optional<ButtonType> result = alert.showAndWait();

        if (result.isPresent() && result.get().equals(yesBtn)) {
            Stage stage = (Stage) main.getScene().getWindow();
            stage.close();

            Parent root = loadFXML(ConstantPage.LOGIN);
            Stage loginStage = new Stage();
            loginStage.setScene(new Scene(root));
            loginStage.setTitle("Login");
            loginStage.show();
            loginStage.setResizable(false);
            loginStage.sizeToScene();
            tokenManager.removeToken();
        }
    }



}
