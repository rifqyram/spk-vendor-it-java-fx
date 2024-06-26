package ac.unindra.roemah_duren_spring.dto.request;

import lombok.*;

import java.time.LocalDateTime;
import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReportQueryRequest {
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String branchId;
    private String transactionType;
}
