package ac.unindra.roemah_duren_spring.dto.request;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AdminRequest {
    private String id;
    private String nip;
    private String name;
    private String email;
    private String mobilePhoneNo;
    private String password;
    private boolean status;
}
