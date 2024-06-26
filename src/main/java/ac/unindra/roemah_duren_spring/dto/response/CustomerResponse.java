package ac.unindra.roemah_duren_spring.dto.response;

import ac.unindra.roemah_duren_spring.model.Customer;
import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CustomerResponse {
    private String id;
    private String name;
    private String address;
    private String email;
    private String mobilePhoneNo;

    public Customer toCustomer() {
        Customer customer = new Customer();
        customer.setId(id);
        customer.setName(name);
        customer.setAddress(address);
        customer.setEmail(email);
        customer.setMobilePhoneNo(mobilePhoneNo);
        return customer;
    }
}
