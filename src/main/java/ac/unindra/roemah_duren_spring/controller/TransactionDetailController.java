package ac.unindra.roemah_duren_spring.controller;

import ac.unindra.roemah_duren_spring.JavaFxApplication;
import ac.unindra.roemah_duren_spring.model.Stock;
import ac.unindra.roemah_duren_spring.model.Transaction;
import ac.unindra.roemah_duren_spring.model.TransactionDetail;
import ac.unindra.roemah_duren_spring.service.JasperService;
import ac.unindra.roemah_duren_spring.util.*;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.material2.Material2OutlinedAL;
import org.kordamp.ikonli.material2.Material2OutlinedMZ;

import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class TransactionDetailController implements Initializable {
    public TableView<TransactionDetail> tableDetail;
    public TableColumn<TransactionDetail, Integer> noCol;
    public TableColumn<TransactionDetail, String> transactionIdCol;
    public TableColumn<TransactionDetail, Stock> productCol;
    public TableColumn<TransactionDetail, Integer> qtyCol;
    public TableColumn<TransactionDetail, Long> priceCol;
    public Button closeBtn;
    public AnchorPane main;
    public Button printBtn;

    private Transaction selectedTransaction;

    private final JasperService jasperService;

    public TransactionDetailController() {
        this.jasperService = JavaFxApplication.getBean(JasperService.class);
    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setupButtonIcons();
        initTables();
    }

    public void initTables() {
        TableUtil.setColumnResizePolicy(tableDetail);
        TableUtil.setTableSequence(noCol);

        transactionIdCol.setCellValueFactory(new PropertyValueFactory<>("transactionId"));
        transactionIdCol.setCellFactory(col -> TableUtil.setTableObject(s -> s.split("-")[0]));

        productCol.setCellValueFactory(new PropertyValueFactory<>("stock"));
        productCol.setCellFactory(col -> TableUtil.setTableObject(stock -> stock.getProduct().getName()));

        qtyCol.setCellValueFactory(new PropertyValueFactory<>("qty"));
        priceCol.setCellValueFactory(new PropertyValueFactory<>("price"));
        priceCol.setCellFactory(col -> TableUtil.setTableObject(CurrencyUtil::formatCurrencyIDR));

        if (selectedTransaction != null) {
            tableDetail.getItems().setAll(selectedTransaction.getTransactionDetails());
            tableDetail.refresh();
        }
    }

    public void handleClose() {
        Stage stage = (Stage) main.getScene().getWindow();
        stage.close();
    }

    private void setupButtonIcons() {
        closeBtn.setGraphic(new FontIcon(Material2OutlinedAL.CLOSE));
        printBtn.setGraphic(new FontIcon(Material2OutlinedMZ.PRINT));
    }

    public void setSelectedTransaction(Transaction selectedTransaction) {
        this.selectedTransaction = selectedTransaction;
        printBtn.setVisible(selectedTransaction.getTransactionType().equalsIgnoreCase("Penjualan"));
        initTables();
    }

    public void doPrint() {
        if (selectedTransaction == null) {
            FXMLUtil.updateUI(() -> NotificationUtil.showNotificationError(main, "Transaksi Tidak Ditemukan"));
        }

        List<Map<String, Object>> dataList = new ArrayList<>();

        for (TransactionDetail transactionDetail : selectedTransaction.getTransactionDetails()) {
            Map<String, Object> map = new HashMap<>();
            map.put("DESKRIPSI", transactionDetail.getStock().getProduct().getName());
            map.put("KUANTITAS", String.valueOf(transactionDetail.getQty()));
            map.put("HARGA", CurrencyUtil.formatCurrencyIDR(transactionDetail.getPrice()));
            map.put("TOTAL", CurrencyUtil.formatCurrencyIDR(transactionDetail.getPrice() * transactionDetail.getQty()));
            dataList.add(map);
        }

        Map<String, Object> map = getJasperParams();
        jasperService.createReport(main, "invoice", dataList, map);
    }

    private Map<String, Object> getJasperParams() {
        String to = getInvoiceToInfo();

        Map<String, Object> map = new HashMap<>();
        map.put("KIRIM_KE", to);
        map.put("KODE_TRANSAKSI", selectedTransaction.getId().split("-")[0]);
        map.put("TIPE_TRANSAKSI", selectedTransaction.getTransactionType());

        String date = DateUtil.strDateFromLocalDateTime(selectedTransaction.getTransDate());
        long totalPrice = selectedTransaction.getTransactionDetails().stream().mapToLong(detail -> detail.getPrice() * detail.getQty()).sum();

        map.put("TANGGAL", date);
        map.put("TOTAL_PRICE", CurrencyUtil.formatCurrencyIDR(totalPrice));

        return map;
    }

    private String getInvoiceToInfo() {
        String to;
        if (selectedTransaction.getTransactionType().equals("Penjualan")) {
            to = selectedTransaction.getCustomer() != null ? selectedTransaction.getCustomer().getName() : "Guest";
        } else if (selectedTransaction.getTransactionType().equals("Pembelian")) {
            to = selectedTransaction.getBranch().getName();
        } else if (selectedTransaction.getTransactionType().equals("Transfer")) {
            to = selectedTransaction.getTargetBranch().getName();
        } else if (selectedTransaction.getTransactionType().equals("Pengembalian")) {
            to = selectedTransaction.getBranch().getName();
        } else {
            to = "Tamu";
        }
        return to;
    }
}
