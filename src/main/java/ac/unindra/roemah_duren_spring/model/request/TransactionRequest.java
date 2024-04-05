package ac.unindra.roemah_duren_spring.model.request;

import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TransactionRequest {
    private String customerId;
    private String branchId;
    private List<TransactionDetailRequest> transactionDetails;
}
