package ac.unindra.roemah_duren_spring.dto.response;

import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DashboardResponse {
    private long totalProducts;
    private long totalCustomers;
    private long totalTransactionsPerDay;
    private long totalRevenue;
    private long totalExpend;
    private List<TransactionResponse> transactions;
}
