package ac.unindra.spk_vendor_it.model;

import ac.unindra.spk_vendor_it.entity.Vendor;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode
public class VendorModel {
    private final StringProperty id;
    private final StringProperty name;
    private final StringProperty address;
    private final StringProperty email;
    private final StringProperty mobilePhoneNo;

    public VendorModel() {
        this.id = new SimpleStringProperty();
        this.name = new SimpleStringProperty();
        this.address = new SimpleStringProperty();
        this.email = new SimpleStringProperty();
        this.mobilePhoneNo = new SimpleStringProperty();
    }

    public VendorModel(String id, String name, String address, String email, String mobilePhoneNo) {
        this.id = new SimpleStringProperty(id);
        this.name = new SimpleStringProperty(name);
        this.address = new SimpleStringProperty(address);
        this.email = new SimpleStringProperty(email);
        this.mobilePhoneNo = new SimpleStringProperty(mobilePhoneNo);
    }

    public String getId() {
        return id.get();
    }

    public StringProperty idProperty() {
        return id;
    }

    public void setId(String id) {
        this.id.set(id);
    }

    public String getName() {
        return name.get();
    }

    public StringProperty nameProperty() {
        return name;
    }

    public void setName(String name) {
        this.name.set(name);
    }

    public String getAddress() {
        return address.get();
    }

    public StringProperty addressProperty() {
        return address;
    }

    public void setAddress(String address) {
        this.address.set(address);
    }

    public String getEmail() {
        return email.get();
    }

    public StringProperty emailProperty() {
        return email;
    }

    public void setEmail(String email) {
        this.email.set(email);
    }

    public String getMobilePhoneNo() {
        return mobilePhoneNo.get();
    }

    public StringProperty mobilePhoneNoProperty() {
        return mobilePhoneNo;
    }

    public void setMobilePhoneNo(String mobilePhoneNo) {
        this.mobilePhoneNo.set(mobilePhoneNo);
    }

    public Vendor toEntity() {
        return Vendor.builder()
                .id(getId())
                .name(getName())
                .address(getAddress())
                .email(getEmail())
                .mobilePhoneNo(getMobilePhoneNo())
                .build();
    }

    public static VendorModel fromEntity(Vendor vendor) {
        return new VendorModel(
                vendor.getId(),
                vendor.getName(),
                vendor.getAddress(),
                vendor.getEmail(),
                vendor.getMobilePhoneNo()
        );
    }
}
