package ac.unindra.roemah_duren_spring.dto.response;

import ac.unindra.roemah_duren_spring.model.TransactionDetail;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TransactionDetailResponse {
    private String id;
    private String transactionId;
    private StockResponse stock;
    private Integer qty;
    private Long price;

    public TransactionDetail toTransactionDetail() {
        TransactionDetail newTransactionDetail = new TransactionDetail();
        newTransactionDetail.setId(id);
        newTransactionDetail.setTransactionId(transactionId);
        newTransactionDetail.setStock(stock.toStock());
        newTransactionDetail.setQty(qty);
        newTransactionDetail.setPrice(price);
        newTransactionDetail.setSubTotal(qty * price);
        return newTransactionDetail;
    }
}
