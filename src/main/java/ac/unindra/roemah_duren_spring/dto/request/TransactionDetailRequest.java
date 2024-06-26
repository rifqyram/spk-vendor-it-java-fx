package ac.unindra.roemah_duren_spring.dto.request;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TransactionDetailRequest {
    private String stockId;
    private Long price;
    private Integer qty;
}
