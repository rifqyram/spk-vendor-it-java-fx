package ac.unindra.roemah_duren_spring.model.response;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BranchResponse {
    private String id;
    private String name;
    private String address;
    private String mobilePhoneNo;
}
