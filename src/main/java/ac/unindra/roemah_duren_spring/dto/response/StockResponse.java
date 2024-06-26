package ac.unindra.roemah_duren_spring.dto.response;

import ac.unindra.roemah_duren_spring.model.Stock;
import lombok.*;

import java.util.Collections;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StockResponse {
    private String id;
    private ProductResponse product;
    private Integer stock;
    private BranchResponse branch;
    private String updatedDate;
    private List<TransactionDetailResponse> transactionDetails;

    public Stock toStock() {
        Stock newStock = new Stock();
        newStock.setId(id);
        newStock.setProduct(product.toProduct());
        newStock.setStock(stock);
        newStock.setBranch(branch.toBranch());
        newStock.setUpdatedDate(updatedDate);
        newStock.setTransactionDetails(transactionDetails == null ? Collections.emptyList() : transactionDetails.stream().map(TransactionDetailResponse::toTransactionDetail).toList());
        return newStock;
    }
}
