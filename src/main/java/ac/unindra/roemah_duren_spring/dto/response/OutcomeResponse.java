package ac.unindra.roemah_duren_spring.dto.response;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OutcomeResponse {
    private String id;
    private Long amount;
    private String description;
    private BranchResponse branch;
}
