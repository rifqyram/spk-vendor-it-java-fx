package ac.unindra.roemah_duren_spring.controller;

import ac.unindra.roemah_duren_spring.JavaFxApplication;
import ac.unindra.roemah_duren_spring.dto.request.ReportQueryRequest;
import ac.unindra.roemah_duren_spring.model.Branch;
import ac.unindra.roemah_duren_spring.model.Transaction;
import ac.unindra.roemah_duren_spring.model.TransactionDetail;
import ac.unindra.roemah_duren_spring.service.BranchService;
import ac.unindra.roemah_duren_spring.service.JasperService;
import ac.unindra.roemah_duren_spring.service.TransactionService;
import ac.unindra.roemah_duren_spring.util.*;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Pair;
import lombok.Setter;
import net.sf.jasperreports.repo.RepositoryService;
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.material2.Material2OutlinedAL;
import org.kordamp.ikonli.material2.Material2OutlinedMZ;

import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.function.Consumer;

public class ReportController implements Initializable {
    public AnchorPane main;

    public ComboBox<String> reportTypeComboBox;

    public DatePicker startDatePicker;
    public DatePicker endDatePicker;
    public ComboBox<Branch> branchComboBox;
    public Button printReportButton;
    public Button cancelBtn;
    public Label labelErrorType;
    public Label labelErrorStartDate;
    public Label errorLabelEndDate;
    public Label errorLabelBranch;
    public VBox branchPane;

    @Setter
    private AnchorPane ownerPane;

    private final BranchService branchService;
    private final TransactionService transactionService;
    private final JasperService jasperService;

    public ReportController() {
        this.jasperService = JavaFxApplication.getBean(JasperService.class);
        this.branchService = JavaFxApplication.getBean(BranchService.class);
        this.transactionService = JavaFxApplication.getBean(TransactionService.class);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setupButton();
        initComboBoxBranch();
        initTransTypeComboBox();

        branchComboBox.setButtonCell(ComboBoxUtil.getComboBoxListCell(branch -> branch.getCode() + " | " + branch.getName()));
        branchComboBox.setCellFactory(param -> ComboBoxUtil.getComboBoxListCell(branch -> branch.getCode() + " | " + branch.getName()));
    }

    private void initComboBoxBranch() {
        branchService.getAllBranch(
                response -> {
                    branchComboBox.getItems().setAll(response.getData());
                },
                error -> handleResponseError(error.getMessage())
        );
    }

    private void handleResponseError(String message) {
        FXMLUtil.updateUI(() -> NotificationUtil.showNotificationError(ownerPane, message));
    }

    public void doPrint() {
        if (!ValidationUtil.isFormValidComboBox(getValidationComboBox())) return;

        getTransactions(transactions -> {
            if (transactions.isEmpty()) {
                NotificationUtil.showNotificationError(ownerPane, "Tidak ada data transaksi");
                return;
            }

            switch (reportTypeComboBox.getValue()) {
                case "Pembelian":
                    doPrintTransactionPurchase(transactions);
                    break;
                case "Penjualan":
                    // doPrintTransactionProduct(transactions, reportTitle, reportFileName);
                    break;
                case "Penjualan Cabang":
                    // doPrintTransactionProduct(transactions, reportTitle, reportFileName);
                    break;
                case "Transfer":
                    // doPrintTransactionPurchase(transactions, reportTitle, reportFileName);
                    break;
                case "Pengembalian":
                    // doPrintTransactionSale(transactions, reportTitle, reportFileName);
                    break;
            }
        });
    }

    private void doPrintTransactionPurchase(List<Transaction> transactions) {
        List<Map<String, Object>> dataList = new ArrayList<>();
        for (Transaction transaction : transactions) {
            Map<String, Object> data = new HashMap<>();
            data.put("branch", transaction.getBranch().getName());
            String date = DateUtil.strDateFromLocalDateTime(transaction.getTransDate());

            for (TransactionDetail transactionDetail : transaction.getTransactionDetails()) {
                long subTotalPrice = transactionDetail.getQty() * transactionDetail.getPrice();

                data.put("productName", transactionDetail.getStock().getProduct().getName());
                data.put("quantityPurchased", String.valueOf(transactionDetail.getQty()));
                data.put("price", CurrencyUtil.formatCurrencyIDR(transactionDetail.getPrice()));
                data.put("subTotalPrice", CurrencyUtil.formatCurrencyIDR(subTotalPrice));
                data.put("purchaseDate", date);
            }

            dataList.add(data);
        }

        long sum = transactions.stream().mapToLong(transaction -> transaction.getTransactionDetails().stream().mapToLong(detail -> detail.getQty() * detail.getPrice()).sum()).sum();

        Map<String, Object> params = new HashMap<>();
        params.put("tanggal", LocalDateTime.now().toString());
        params.put("grandTotal", CurrencyUtil.formatCurrencyIDR(sum));


        jasperService.createReport(ownerPane, "purchasing", dataList, params);
    }

    private void getTransactions(Consumer<List<Transaction>> consumer) {
        String reportType = reportTypeComboBox.getValue();

        LocalDate startDate = startDatePicker.getValue();
        LocalDate endDate = endDatePicker.getValue();

        LocalDateTime startDateTime = startDate.atTime(LocalTime.MIN);
        LocalDateTime endDateTime = endDate.atTime(LocalTime.MAX);
        Branch branch = branchComboBox.getValue();

        transactionService.getTransactions(
                ReportQueryRequest.builder()
                        .branchId(branch != null ? branch.getId() : null)
                        .startDate(startDateTime)
                        .endDate(endDateTime)
                        .transactionType(reportType)
                        .build(),
                response -> consumer.accept(response.getData()),
                error -> handleResponseError(error.getMessage())
        );
    }

    private void setupButton() {
        printReportButton.setGraphic(new FontIcon(Material2OutlinedMZ.PRINT));
        cancelBtn.setGraphic(new FontIcon(Material2OutlinedAL.CANCEL));
    }

    public void closeModal() {
        Stage stage = (Stage) main.getScene().getWindow();
        stage.close();
    }

    private void initTransTypeComboBox() {
        reportTypeComboBox.getItems().setAll(List.of("Pembelian", "Penjualan", "Transfer", "Pengembalian"));
        System.out.println("added : " + reportTypeComboBox.getItems());
    }

    public Map<ComboBoxBase<?>, Pair<Label, ValidationUtil.ValidationStrategyComboBox>> getValidationComboBox() {
        return Map.of(
                reportTypeComboBox, new Pair<>(labelErrorType, input -> input.getValue() == null ? "Pilih salah satu jenis laporan" : ""),
                startDatePicker, new Pair<>(labelErrorStartDate, input -> input.getValue() == null ? "Pilih tanggal awal" : ""),
                endDatePicker, new Pair<>(errorLabelEndDate, input -> input.getValue() == null ? "Pilih tanggal akhir" : "")
        );
    }
}
