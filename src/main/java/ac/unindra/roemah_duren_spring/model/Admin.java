package ac.unindra.roemah_duren_spring.model;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
public class Admin {
    private StringProperty id = new SimpleStringProperty();
    private StringProperty nip = new SimpleStringProperty();
    private StringProperty name = new SimpleStringProperty();
    private StringProperty email = new SimpleStringProperty();
    private StringProperty mobilePhoneNo = new SimpleStringProperty();
    private StringProperty password = new SimpleStringProperty();
    private BooleanProperty status = new SimpleBooleanProperty();

    public String getId() {
        return id.get();
    }

    public void setId(String id) {
        this.id.set(id);
    }

    public String getNip() {
        return nip.get();
    }

    public void setNip(String nip) {
        this.nip.set(nip);
    }

    public String getName() {
        return name.get();
    }

    public void setName(String name) {
        this.name.set(name);
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

    public String getPassword() {
        return password.get();
    }
    public void setPassword(String password) {
        this.password.set(password);
    }

    public boolean isStatus() {
        return status.get();
    }

    public void setStatus(boolean status) {
        this.status.set(status);
    }
}
