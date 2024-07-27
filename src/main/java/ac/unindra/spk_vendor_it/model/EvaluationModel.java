package ac.unindra.spk_vendor_it.model;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class EvaluationModel {
    private String id;
    private ProjectModel project;
    private Integer countVendor;
    private LocalDate startDate;
    private LocalDate endDate;
    private LocalDateTime evaluationDate;
    private List<EvaluationDetailModel> evaluationDetails;
}
