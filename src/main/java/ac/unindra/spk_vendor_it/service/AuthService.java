package ac.unindra.spk_vendor_it.service;

import ac.unindra.spk_vendor_it.entity.UserCredential;
import ac.unindra.spk_vendor_it.model.LoginModel;

public interface AuthService {
    void login(LoginModel loginModel);
    void checkToken();
    void logout();
    UserCredential getUserInfo();
}
