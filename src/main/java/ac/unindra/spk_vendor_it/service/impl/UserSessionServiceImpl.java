package ac.unindra.spk_vendor_it.service.impl;

import ac.unindra.spk_vendor_it.entity.UserCredential;
import ac.unindra.spk_vendor_it.entity.UserSession;
import ac.unindra.spk_vendor_it.repository.UserSessionRepository;
import ac.unindra.spk_vendor_it.service.UserSessionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserSessionServiceImpl implements UserSessionService {
    private final UserSessionRepository userSessionRepository;


    @Transactional(rollbackFor = Exception.class)
    @Override
    public UserSession setSession(UserCredential userCredential) {
        if (userSessionRepository.existsByUser_Username(userCredential.getUsername())) {
            removeSession(userCredential.getUsername());
        }
        UserSession session = UserSession.builder()
                .user(userCredential)
                .token(UUID.randomUUID().toString())
                .build();
        return userSessionRepository.saveAndFlush(session);
    }

    @Override
    public UserSession getSession(String token) {
        return userSessionRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Sesi tidak ditemukan"));
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void removeSession(String token) {
        UserSession session = getSession(token);
        userSessionRepository.delete(session);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void clearSession() {
        userSessionRepository.deleteAll();
    }
}
