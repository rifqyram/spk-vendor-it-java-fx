package ac.unindra.roemah_duren_spring.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class Supplier {
    private StringProperty id = new SimpleStringProperty();
    private StringProperty name = new SimpleStringProperty();
    private StringProperty address = new SimpleStringProperty();
    private StringProperty email = new SimpleStringProperty();
    private StringProperty mobilePhoneNo = new SimpleStringProperty();

    public String getId() {
        return id.get();
    }

    public void setId(String id) {
        this.id.set(id);
    }

    public String getName() {
        return name.get();
    }


    public void setName(String name) {
        this.name.set(name);
    }

    public String getAddress() {
        return address.get();
    }

    public void setAddress(String address) {
        this.address.set(address);
    }

    public String getEmail() {
        return email.get();
    }

    public void setEmail(String email) {
        this.email.set(email);
    }

    public String getMobilePhoneNo() {
        return mobilePhoneNo.get();
    }

    public void setMobilePhoneNo(String mobilePhoneNo) {
        this.mobilePhoneNo.set(mobilePhoneNo);
    }

    @Override
    public String toString() {
        return name.get();
    }
}
