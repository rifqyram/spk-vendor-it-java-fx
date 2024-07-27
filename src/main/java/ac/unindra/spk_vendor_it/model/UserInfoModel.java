package ac.unindra.spk_vendor_it.model;

import ac.unindra.spk_vendor_it.constant.UserRole;
import ac.unindra.spk_vendor_it.entity.UserCredential;
import ac.unindra.spk_vendor_it.entity.UserInfo;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import lombok.Builder;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class UserInfoModel {
    private StringProperty id;
    private StringProperty nip;
    private StringProperty name;
    private StringProperty position;
    private StringProperty email;
    private StringProperty mobilePhoneNo;
    private StringProperty password;
    private BooleanProperty status;
    private StringProperty userId;
    private UserRole role;

    @Builder
    public UserInfoModel(String id, String nip, String name, String position, String email, String mobilePhoneNo, String password, boolean status, String userId, UserRole role) {
        this.id = new SimpleStringProperty(id);
        this.nip = new SimpleStringProperty(nip);
        this.name = new SimpleStringProperty(name);
        this.position = new SimpleStringProperty(position);
        this.email = new SimpleStringProperty(email);
        this.mobilePhoneNo = new SimpleStringProperty(mobilePhoneNo);
        this.password = new SimpleStringProperty(password);
        this.status = new SimpleBooleanProperty(status);
        this.userId = new SimpleStringProperty(userId);
        this.role = role;
    }

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

    public String getPosition() {
        return position.get();
    }

    public void setPosition(String position) {
        this.position.set(position);
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

    public boolean getStatus() {
        return status.get();
    }

    public void setStatus(boolean status) {
        this.status.set(status);
    }

    public String getUserId() {
        return userId.get();
    }

    public void setUserId(String userId) {
        this.userId.set(userId);
    }

    public String getPassword() {
        return password.get();
    }

    public void setPassword(String password) {
        this.password.set(password);
    }

    public UserInfo toEntity() {
        return UserInfo.builder()
                .id(id.get())
                .nip(nip.get())
                .name(name.get())
                .position(position.get())
                .email(email.get())
                .mobilePhoneNo(mobilePhoneNo.get())
                .user(UserCredential.builder()
                        .id(userId.get())
                        .status(status.get())
                        .username(nip.get())
                        .role(role)
                        .build())
                .build();
    }

    public static UserInfoModel fromEntity(UserInfo userInfo) {
        return UserInfoModel.builder()
                .id(userInfo.getId())
                .nip(userInfo.getNip())
                .name(userInfo.getName())
                .position(userInfo.getPosition())
                .email(userInfo.getEmail())
                .mobilePhoneNo(userInfo.getMobilePhoneNo())
                .userId(userInfo.getUser().getId())
                .status(userInfo.getUser().isStatus())
                .role(userInfo.getUser().getRole())
                .build();
    }
}
