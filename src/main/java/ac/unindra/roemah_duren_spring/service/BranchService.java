package ac.unindra.roemah_duren_spring.service;

import ac.unindra.roemah_duren_spring.dto.request.QueryRequest;
import ac.unindra.roemah_duren_spring.model.Page;
import ac.unindra.roemah_duren_spring.model.Branch;
import ac.unindra.roemah_duren_spring.util.WebClientUtil;

public interface BranchService {
    void createBranch(Branch branch, WebClientUtil.SuccessCallback<Void> successCallback, WebClientUtil.ErrorCallback errorCallback);
    void getBranches(QueryRequest request, WebClientUtil.SuccessCallback<Page<Branch>> successCallback, WebClientUtil.ErrorCallback errorCallback);
    void getAllBranch(WebClientUtil.SuccessCallback<Page<Branch>> successCallback, WebClientUtil.ErrorCallback errorCallback);
    void updateBranch(Branch branch, WebClientUtil.SuccessCallback<Void> successCallback, WebClientUtil.ErrorCallback errorCallback);
    void deleteBranch(String id, WebClientUtil.SuccessCallback<Void> successCallback, WebClientUtil.ErrorCallback errorCallback);
}
