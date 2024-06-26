package ac.unindra.roemah_duren_spring.dto.request;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class QueryRequest {
    private Integer page;
    private Integer size;
    private String query;
}
