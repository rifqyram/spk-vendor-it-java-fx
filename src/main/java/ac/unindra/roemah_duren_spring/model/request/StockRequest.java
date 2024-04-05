package ac.unindra.roemah_duren_spring.model.request;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StockRequest {
    private String id;
    private String productId;
    private Integer stock;
    private String branchId;
}
