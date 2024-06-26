package ac.unindra.roemah_duren_spring.service.impl;

import ac.unindra.roemah_duren_spring.constant.ConstantAPIUrl;
import ac.unindra.roemah_duren_spring.dto.request.AuthRequest;
import ac.unindra.roemah_duren_spring.dto.response.CommonResponse;
import ac.unindra.roemah_duren_spring.dto.response.LoginResponse;
import ac.unindra.roemah_duren_spring.service.AuthService;
import ac.unindra.roemah_duren_spring.util.WebClientUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final WebClientUtil webClientUtil;
    @Override
    public void login(AuthRequest request, WebClientUtil.SuccessCallback<LoginResponse> successCallback, WebClientUtil.ErrorCallback errorCallback) {
        webClientUtil.post(
                ConstantAPIUrl.AuthAPI.LOGIN,
                request,
                new ParameterizedTypeReference<CommonResponse<LoginResponse>>() {
                },
                response -> successCallback.onSuccess(response.getData()),
                errorCallback
        );
    }
}
