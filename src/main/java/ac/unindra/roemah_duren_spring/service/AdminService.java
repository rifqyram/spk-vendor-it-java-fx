package ac.unindra.roemah_duren_spring.service;

import ac.unindra.roemah_duren_spring.dto.request.QueryRequest;
import ac.unindra.roemah_duren_spring.model.Admin;
import ac.unindra.roemah_duren_spring.model.Page;
import ac.unindra.roemah_duren_spring.util.WebClientUtil;

public interface AdminService {
    void createAdmin(Admin admin, WebClientUtil.SuccessCallback<Void> successCallback, WebClientUtil.ErrorCallback errorCallback);
    void getAdmins(QueryRequest request, WebClientUtil.SuccessCallback<Page<Admin>> successCallback, WebClientUtil.ErrorCallback errorCallback);
    void getAllAdmin(WebClientUtil.SuccessCallback<Page<Admin>> successCallback, WebClientUtil.ErrorCallback errorCallback);
    void updateAdmin(Admin admin, WebClientUtil.SuccessCallback<Void> successCallback, WebClientUtil.ErrorCallback errorCallback);
    void deleteAdmin(String id, WebClientUtil.SuccessCallback<Void> successCallback, WebClientUtil.ErrorCallback errorCallback);
}
