package ac.unindra.roemah_duren_spring.service.impl;

import ac.unindra.roemah_duren_spring.constant.ConstantAPIUrl;
import ac.unindra.roemah_duren_spring.dto.request.BranchRequest;
import ac.unindra.roemah_duren_spring.dto.request.QueryRequest;
import ac.unindra.roemah_duren_spring.dto.response.BranchResponse;
import ac.unindra.roemah_duren_spring.dto.response.CommonResponse;
import ac.unindra.roemah_duren_spring.dto.response.StockResponse;
import ac.unindra.roemah_duren_spring.model.Page;
import ac.unindra.roemah_duren_spring.model.Branch;
import ac.unindra.roemah_duren_spring.service.BranchService;
import ac.unindra.roemah_duren_spring.util.WebClientUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class BranchServiceImpl implements BranchService {
    private final WebClientUtil webClientUtil;

    @Override
    public void createBranch(Branch branch, WebClientUtil.SuccessCallback<Void> successCallback, WebClientUtil.ErrorCallback errorCallback) {
        BranchRequest request = BranchRequest.builder()
                .code(branch.getCode())
                .name(branch.getName())
                .address(branch.getAddress())
                .mobilePhoneNo(branch.getMobilePhoneNo())
                .build();
        webClientUtil.post(ConstantAPIUrl.BranchAPI.BASE_URL,
                request,
                new ParameterizedTypeReference<CommonResponse<Void>>() {
                },
                response -> successCallback.onSuccess(response.getData()),
                errorCallback);
    }

    @Override
    public void getBranches(QueryRequest request, WebClientUtil.SuccessCallback<Page<Branch>> successCallback, WebClientUtil.ErrorCallback errorCallback) {
        webClientUtil.getWithQueryParam(
                ConstantAPIUrl.BranchAPI.BASE_URL,
                new ParameterizedTypeReference<CommonResponse<List<BranchResponse>>>() {
                },
                Map.of(
                        "page", String.valueOf(request.getPage()),
                        "size", String.valueOf(request.getSize()),
                        "q", request.getQuery()
                ),
                response -> {
                    List<BranchResponse> data = response.getData();
                    List<Branch> branches = data.stream().map(branchResponse -> {
                        Branch branch = new Branch();
                        branch.setId(branchResponse.getId());
                        branch.setCode(branchResponse.getCode());
                        branch.setName(branchResponse.getName());
                        branch.setAddress(branchResponse.getAddress());
                        branch.setMobilePhoneNo(branchResponse.getMobilePhoneNo());
                        branch.setStocks(branchResponse.getStocks() != null && !branchResponse.getStocks().isEmpty() ? branchResponse.getStocks().stream().map(StockResponse::toStock).toList() : Collections.emptyList());
                        return branch;
                    }).toList();

                    Page<Branch> branchPage = Page.<Branch>builder()
                            .data(branches)
                            .paging(response.getPaging())
                            .build();

                    successCallback.onSuccess(branchPage);
                },
                errorCallback
        );
    }

    @Override
    public void getAllBranch(WebClientUtil.SuccessCallback<Page<Branch>> successCallback, WebClientUtil.ErrorCallback errorCallback) {
        webClientUtil.getWithQueryParam(
                ConstantAPIUrl.BranchAPI.BASE_URL,
                new ParameterizedTypeReference<CommonResponse<List<BranchResponse>>>() {
                },
                Map.of(
                        "all", "true"
                ),
                response -> {
                    List<BranchResponse> data = response.getData();
                    List<Branch> branches = data.stream().map(branchResponse -> {
                        Branch branch = new Branch();
                        branch.setId(branchResponse.getId());
                        branch.setCode(branchResponse.getCode());
                        branch.setName(branchResponse.getName());
                        branch.setAddress(branchResponse.getAddress());
                        branch.setMobilePhoneNo(branchResponse.getMobilePhoneNo());
                        branch.setStocks(branchResponse.getStocks() != null && !branchResponse.getStocks().isEmpty() ? branchResponse.getStocks().stream().map(StockResponse::toStock).toList() : Collections.emptyList());
                        return branch;
                    }).toList();

                    Page<Branch> branchPage = Page.<Branch>builder()
                            .data(branches)
                            .paging(response.getPaging())
                            .build();

                    successCallback.onSuccess(branchPage);
                },
                errorCallback
        );
    }

    @Override
    public void updateBranch(Branch branch, WebClientUtil.SuccessCallback<Void> successCallback, WebClientUtil.ErrorCallback errorCallback) {
        BranchRequest request = BranchRequest.builder()
                .id(branch.getId())
                .name(branch.getName())
                .code(branch.getCode())
                .address(branch.getAddress())
                .mobilePhoneNo(branch.getMobilePhoneNo())
                .build();
        webClientUtil.put(
                ConstantAPIUrl.BranchAPI.BASE_URL,
                request,
                new ParameterizedTypeReference<CommonResponse<Void>>() {
                },
                response -> successCallback.onSuccess(response.getData()),
                errorCallback
        );
    }

    @Override
    public void deleteBranch(String id, WebClientUtil.SuccessCallback<Void> successCallback, WebClientUtil.ErrorCallback errorCallback) {
        webClientUtil.delete(
                ConstantAPIUrl.BranchAPI.GET_BASE_URL_WITH_ID(id),
                new ParameterizedTypeReference<CommonResponse<Void>>() {
                },
                response -> successCallback.onSuccess(response.getData()),
                errorCallback
        );
    }
}
