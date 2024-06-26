package ac.unindra.roemah_duren_spring.dto.request;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PagingRequest {
    private Integer size;
    private Integer page;
}
