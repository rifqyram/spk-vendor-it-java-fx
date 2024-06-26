package ac.unindra.roemah_duren_spring.dto.response;

import ac.unindra.roemah_duren_spring.model.Admin;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AdminResponse {
    private String id;
    private String nip;
    private String name;
    private String email;
    private String mobilePhoneNo;
    private boolean status;

    public Admin toAdmin() {
        Admin admin = new Admin();
        admin.setId(id);
        admin.setNip(nip);
        admin.setName(name);
        admin.setEmail(email);
        admin.setMobilePhoneNo(mobilePhoneNo);
        admin.setStatus(status);
        return admin;
    }
}
