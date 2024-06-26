package ac.unindra.roemah_duren_spring.controller;

import ac.unindra.roemah_duren_spring.JavaFxApplication;
import ac.unindra.roemah_duren_spring.model.Dashboard;
import ac.unindra.roemah_duren_spring.model.Transaction;
import ac.unindra.roemah_duren_spring.service.DashboardService;
import ac.unindra.roemah_duren_spring.util.CurrencyUtil;
import ac.unindra.roemah_duren_spring.util.FXMLUtil;
import ac.unindra.roemah_duren_spring.util.NotificationUtil;
import javafx.fxml.Initializable;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;

import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

public class DashboardController implements Initializable {
    public AnchorPane main;
    public Label labelTotalProduct;
    public Label labelTotalCustomer;
    public Label labelTotalTransaction;
    public Label labelTotalRevenue;
    public Label labelTotalExpend;
    public BarChart<String, Number> chartTransaction;
    private final DashboardService dashboardService;

    public DashboardController() {
        this.dashboardService = JavaFxApplication.getBean(DashboardService.class);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        dashboardService.getDashboardInfo(
                response -> FXMLUtil.updateUI(() -> {
                    setupCard(response);
                    setupBarChart(response.getTransactions());
                }),
                error -> handleResponseError(error.getMessage())
        );
    }

    private void setupCard(Dashboard response) {
        labelTotalCustomer.setText(String.valueOf(response.getTotalCustomer()));
        labelTotalProduct.setText(String.valueOf(response.getTotalProduct()));
        labelTotalTransaction.setText(String.valueOf(response.getTotalTransaction()));
        labelTotalRevenue.setText(CurrencyUtil.formatCurrencyIDR(response.getTotalRevenue()));
        labelTotalExpend.setText(CurrencyUtil.formatCurrencyIDR(response.getTotalExpend()));
    }

    private void setupBarChart(List<Transaction> transactions) {
        Map<String, Long> branchTransactionTotals = new HashMap<>();

        for (Transaction transaction : transactions) {
            String branchName = transaction.getBranch().getName();
            if (transaction.getTransactionType().equals("Penjualan")) {
                branchTransactionTotals.put(branchName, branchTransactionTotals.getOrDefault(branchName, 0L) + transaction.getTotalPrice());
            }
        }

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Total Transaksi");

        for (Map.Entry<String, Long> entry : branchTransactionTotals.entrySet()) {
            series.getData().add(new XYChart.Data<>(entry.getKey(), entry.getValue()));
        }

        chartTransaction.getData().clear();
        chartTransaction.getData().add(series);
    }

    private void handleResponseError(String message) {
        FXMLUtil.updateUI(() -> NotificationUtil.showNotificationError(main, message));
    }
}
