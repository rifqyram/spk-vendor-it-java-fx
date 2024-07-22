package ac.unindra.spk_vendor_it.model;

import lombok.*;
import org.springframework.data.jpa.domain.Specification;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PageModel {
    private int page;
    private int size;
    private String query;
}
