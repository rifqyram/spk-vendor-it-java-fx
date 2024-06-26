package ac.unindra.roemah_duren_spring.service;

import ac.unindra.roemah_duren_spring.dto.request.AuthRequest;
import ac.unindra.roemah_duren_spring.dto.response.LoginResponse;
import ac.unindra.roemah_duren_spring.util.WebClientUtil;

public interface AuthService {
    void login(AuthRequest request, WebClientUtil.SuccessCallback<LoginResponse> successCallback, WebClientUtil.ErrorCallback errorCallback);
}
