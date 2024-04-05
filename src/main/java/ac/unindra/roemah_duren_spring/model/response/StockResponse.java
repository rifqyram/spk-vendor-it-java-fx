package ac.unindra.roemah_duren_spring.model.response;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StockResponse {
    private String id;
    private String productId;
    private Integer stock;
    private BranchResponse branch;
}
