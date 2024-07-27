package ac.unindra.spk_vendor_it.service.impl;

import ac.unindra.spk_vendor_it.constant.UserRole;
import ac.unindra.spk_vendor_it.entity.UserCredential;
import ac.unindra.spk_vendor_it.entity.UserSession;
import ac.unindra.spk_vendor_it.model.LoginModel;
import ac.unindra.spk_vendor_it.repository.UserCredentialRepository;
import ac.unindra.spk_vendor_it.security.PasswordManager;
import ac.unindra.spk_vendor_it.security.TokenManager;
import ac.unindra.spk_vendor_it.service.AuthService;
import ac.unindra.spk_vendor_it.service.UserSessionService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final PasswordManager passwordManager;
    private final UserCredentialRepository userCredentialRepository;
    private final UserSessionService userSessionService;
    private final TokenManager tokenManager;

    @Value("${spk.admin.username}")
    private String ADMIN_USERNAME;
    @Value("${spk.admin.password}")
    private String ADMIN_PASSWORD;

    @Transactional(rollbackFor = Exception.class)
    @PostConstruct
    public void initAdmin() {
        if (userCredentialRepository.existsByUsername(ADMIN_USERNAME)) {
            return;
        }
        UserCredential userCredential = UserCredential.builder()
                .username(ADMIN_USERNAME)
                .password(passwordManager.hashPassword(ADMIN_PASSWORD))
                .role(UserRole.ADMIN)
                .status(true)
                .build();
        userCredentialRepository.saveAndFlush(userCredential);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void login(LoginModel loginModel) {
        UserCredential currentUser = userCredentialRepository.findByUsername(loginModel.getUsername())
                .orElseThrow(() -> new RuntimeException("Username atau password salah"));

        if (!currentUser.isStatus()) {
            throw new RuntimeException("Akun tidak aktif, silahkan hubungi admin");
        }

        if (!passwordManager.checkPassword(loginModel.getPassword(), currentUser.getPassword())) {
            throw new RuntimeException("Username atau password salah");
        }

        UserSession session = userSessionService.setSession(currentUser);
        tokenManager.saveToken(session.getToken());
    }

    @Override
    public void checkToken() {
        String token = tokenManager.getToken();
        if (token == null) {
            throw new RuntimeException("Token tidak ditemukan");
        }
        userSessionService.getSession(token);
    }

    @Override
    public void logout() {
        String token = tokenManager.getToken();
        if (token == null) {
            throw new RuntimeException("Token tidak ditemukan");
        }
        userSessionService.removeSession(token);
        tokenManager.removeToken();
    }

    @Transactional(readOnly = true)
    @Override
    public UserCredential getUserInfo() {
        UserCredential user = userSessionService.getSession(tokenManager.getToken()).getUser();
        user.setPassword(null);
        return user;
    }


}
