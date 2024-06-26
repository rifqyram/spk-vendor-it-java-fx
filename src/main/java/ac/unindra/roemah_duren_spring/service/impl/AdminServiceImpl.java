package ac.unindra.roemah_duren_spring.service.impl;

import ac.unindra.roemah_duren_spring.constant.ConstantAPIUrl;
import ac.unindra.roemah_duren_spring.dto.request.AdminRequest;
import ac.unindra.roemah_duren_spring.dto.request.QueryRequest;
import ac.unindra.roemah_duren_spring.dto.response.AdminResponse;
import ac.unindra.roemah_duren_spring.dto.response.CommonResponse;
import ac.unindra.roemah_duren_spring.model.Admin;
import ac.unindra.roemah_duren_spring.model.Page;
import ac.unindra.roemah_duren_spring.service.AdminService;
import ac.unindra.roemah_duren_spring.util.WebClientUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {
    private final WebClientUtil webClientUtil;

    @Override
    public void createAdmin(Admin admin, WebClientUtil.SuccessCallback<Void> successCallback, WebClientUtil.ErrorCallback errorCallback) {
        AdminRequest request = AdminRequest.builder()
                .nip(admin.getNip())
                .name(admin.getName())
                .email(admin.getEmail())
                .mobilePhoneNo(admin.getMobilePhoneNo())
                .password(admin.getPassword())
                .status(admin.isStatus())
                .build();
        webClientUtil.post(
                ConstantAPIUrl.AdminAPI.BASE_URL,
                request,
                new ParameterizedTypeReference<CommonResponse<AdminResponse>>() {
                },
                response -> successCallback.onSuccess(null),
                errorCallback
        );
    }

    @Override
    public void getAdmins(QueryRequest request, WebClientUtil.SuccessCallback<Page<Admin>> successCallback, WebClientUtil.ErrorCallback errorCallback) {
        webClientUtil.getWithQueryParam(
                ConstantAPIUrl.AdminAPI.BASE_URL,
                new ParameterizedTypeReference<CommonResponse<List<AdminResponse>>>() {
                },
                Map.of(
                        "page", String.valueOf(request.getPage()),
                        "size", String.valueOf(request.getSize()),
                        "q", request.getQuery()
                ),
                response -> {
                    List<Admin> admins = response.getData().stream().map(AdminResponse::toAdmin).toList();
                    successCallback.onSuccess(new Page<>(admins, response.getPaging()));
                },
                errorCallback
        );
    }

    @Override
    public void getAllAdmin(WebClientUtil.SuccessCallback<Page<Admin>> successCallback, WebClientUtil.ErrorCallback errorCallback) {
        webClientUtil.getWithQueryParam(
                ConstantAPIUrl.AdminAPI.BASE_URL,
                new ParameterizedTypeReference<CommonResponse<List<AdminResponse>>>() {
                },
                Map.of(
                        "all", "true"
                ),
                response -> {
                    List<Admin> admins = response.getData().stream().map(AdminResponse::toAdmin).toList();
                    successCallback.onSuccess(new Page<>(admins, response.getPaging()));
                },
                errorCallback
        );
    }

    @Override
    public void updateAdmin(Admin admin, WebClientUtil.SuccessCallback<Void> successCallback, WebClientUtil.ErrorCallback errorCallback) {
        AdminRequest request = AdminRequest.builder()
                .id(admin.getId())
                .nip(admin.getNip())
                .name(admin.getName())
                .email(admin.getEmail())
                .mobilePhoneNo(admin.getMobilePhoneNo())
                .password(admin.getPassword())
                .status(admin.isStatus())
                .build();
        webClientUtil.put(
                ConstantAPIUrl.AdminAPI.BASE_URL,
                request,
                new ParameterizedTypeReference<CommonResponse<AdminResponse>>() {
                },
                response -> successCallback.onSuccess(null),
                errorCallback
        );
    }

    @Override
    public void deleteAdmin(String id, WebClientUtil.SuccessCallback<Void> successCallback, WebClientUtil.ErrorCallback errorCallback) {
        webClientUtil.delete(
                ConstantAPIUrl.AdminAPI.GET_BASE_URL_WITH_ID(id),
                new ParameterizedTypeReference<CommonResponse<Void>>() {
                },
                response -> successCallback.onSuccess(null),
                errorCallback
        );
    }
}
