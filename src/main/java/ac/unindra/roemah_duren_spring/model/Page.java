package ac.unindra.roemah_duren_spring.model;

import ac.unindra.roemah_duren_spring.dto.response.PagingResponse;
import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Page<T> {
    private List<T> data;
    private PagingResponse paging;
}
