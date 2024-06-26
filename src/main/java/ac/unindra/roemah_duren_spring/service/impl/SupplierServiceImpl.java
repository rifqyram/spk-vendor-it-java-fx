package ac.unindra.roemah_duren_spring.service.impl;

import ac.unindra.roemah_duren_spring.constant.ConstantAPIUrl;
import ac.unindra.roemah_duren_spring.dto.request.SupplierRequest;
import ac.unindra.roemah_duren_spring.dto.request.QueryRequest;
import ac.unindra.roemah_duren_spring.dto.response.CommonResponse;
import ac.unindra.roemah_duren_spring.dto.response.SupplierResponse;
import ac.unindra.roemah_duren_spring.model.Supplier;
import ac.unindra.roemah_duren_spring.model.Page;
import ac.unindra.roemah_duren_spring.service.SupplierService;
import ac.unindra.roemah_duren_spring.util.WebClientUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class SupplierServiceImpl implements SupplierService {
    private final WebClientUtil webClientUtil;

    @Override
    public void createSupplier(Supplier supplier, WebClientUtil.SuccessCallback<Void> successCallback, WebClientUtil.ErrorCallback errorCallback) {
        SupplierRequest request = SupplierRequest.builder()
                .name(supplier.getName())
                .address(supplier.getAddress())
                .email(supplier.getEmail())
                .mobilePhoneNo(supplier.getMobilePhoneNo())
                .build();
        webClientUtil.post(
                ConstantAPIUrl.SupplierAPI.BASE_URL,
                request,
                new ParameterizedTypeReference<CommonResponse<Void>>() {},
                response -> successCallback.onSuccess(response.getData()),
                errorCallback
        );
    }

    @Override
    public void getSuppliers(QueryRequest request, WebClientUtil.SuccessCallback<Page<Supplier>> successCallback, WebClientUtil.ErrorCallback errorCallback) {
        Map<String, String> query = Map.of(
                "page", String.valueOf(request.getPage()),
                "size", String.valueOf(request.getSize()),
                "q", request.getQuery()
        );

        fetchAllSupplier(successCallback, errorCallback, query);
    }

    @Override
    public void getSuppliers(WebClientUtil.SuccessCallback<Page<Supplier>> successCallback, WebClientUtil.ErrorCallback errorCallback) {
        Map<String, String> query = Map.of(
                "all", "true"
        );
        fetchAllSupplier(successCallback, errorCallback, query);
    }

    @Override
    public void updateSupplier(Supplier supplier, WebClientUtil.SuccessCallback<Void> successCallback, WebClientUtil.ErrorCallback errorCallback) {
        SupplierRequest request = SupplierRequest.builder()
                .id(supplier.getId())
                .name(supplier.getName())
                .address(supplier.getAddress())
                .email(supplier.getEmail())
                .mobilePhoneNo(supplier.getMobilePhoneNo())
                .build();
        webClientUtil.put(
                ConstantAPIUrl.SupplierAPI.BASE_URL,
                request,
                new ParameterizedTypeReference<CommonResponse<Void>>() {},
                response -> successCallback.onSuccess(response.getData()),
                errorCallback
        );
    }

    @Override
    public void deleteSupplier(String id, WebClientUtil.SuccessCallback<Void> successCallback, WebClientUtil.ErrorCallback errorCallback) {
        webClientUtil.delete(
                ConstantAPIUrl.SupplierAPI.GET_BASE_URL_WITH_ID(id),
                new ParameterizedTypeReference<CommonResponse<Void>>() {},
                response -> successCallback.onSuccess(response.getData()),
                errorCallback
        );
    }

    private void fetchAllSupplier(WebClientUtil.SuccessCallback<Page<Supplier>> successCallback, WebClientUtil.ErrorCallback errorCallback, Map<String, String> query) {
        webClientUtil.getWithQueryParam(
                ConstantAPIUrl.SupplierAPI.BASE_URL,
                new ParameterizedTypeReference<CommonResponse<List<SupplierResponse>>>() {},
                query,
                response -> {
                    List<Supplier> suppliers = response.getData().stream().map(SupplierResponse::toSupplier).toList();
                    Page<Supplier> page = Page.<Supplier>builder()
                            .data(suppliers)
                            .paging(response.getPaging())
                            .build();
                    successCallback.onSuccess(page);
                },
                errorCallback
        );
    }
}
