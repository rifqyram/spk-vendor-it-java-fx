package ac.unindra.roemah_duren_spring.dto.request;

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
    private String targetBranchId;
    private List<TransactionDetailRequest> transactionDetails;
    private String transactionType;
}
