package ac.unindra.roemah_duren_spring.dto.response;

import ac.unindra.roemah_duren_spring.model.Supplier;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SupplierResponse {
    private String id;
    private String name;
    private String address;
    private String email;
    private String mobilePhoneNo;

    public Supplier toSupplier() {
        Supplier supplier = new Supplier();
        supplier.setId(id);
        supplier.setName(name);
        supplier.setAddress(address);
        supplier.setEmail(email);
        supplier.setMobilePhoneNo(mobilePhoneNo);
        return supplier;
    }
}
