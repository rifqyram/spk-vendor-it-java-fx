package ac.unindra.spk_vendor_it.security;

import org.springframework.stereotype.Component;

import java.util.prefs.Preferences;

@Component
public class TokenManager {
    private static final String TOKEN_KEY = "authToken";
    private final Preferences prefs;

    public TokenManager() {
        prefs = Preferences.userNodeForPackage(TokenManager.class);
    }

    public void saveToken(String token) {
        prefs.put(TOKEN_KEY, token);
    }

    public String getToken() {
        return prefs.get(TOKEN_KEY, null);
    }

    public void removeToken() {
        prefs.remove(TOKEN_KEY);
    }
}