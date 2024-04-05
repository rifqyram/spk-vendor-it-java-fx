package ac.unindra.roemah_duren_spring.model.request;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SupplierRequest {
    private String id;
    private String name;
    private String address;
    private String email;
    private String phoneNumberNo;
}
