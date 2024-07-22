package ac.unindra.spk_vendor_it.service;

import ac.unindra.spk_vendor_it.entity.UserCredential;
import ac.unindra.spk_vendor_it.entity.UserSession;

public interface UserSessionService {
    UserSession setSession(UserCredential userCredential);
    UserSession getSession(String token);
    void removeSession(String token);
    void clearSession();
}
