package ac.unindra.roemah_duren_spring.service.impl;

import ac.unindra.roemah_duren_spring.constant.ConstantAPIUrl;
import ac.unindra.roemah_duren_spring.dto.request.CustomerRequest;
import ac.unindra.roemah_duren_spring.dto.request.QueryRequest;
import ac.unindra.roemah_duren_spring.dto.response.CommonResponse;
import ac.unindra.roemah_duren_spring.dto.response.CustomerResponse;
import ac.unindra.roemah_duren_spring.dto.response.PagingResponse;
import ac.unindra.roemah_duren_spring.model.Customer;
import ac.unindra.roemah_duren_spring.model.Page;
import ac.unindra.roemah_duren_spring.service.CustomerService;
import ac.unindra.roemah_duren_spring.util.WebClientUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {
    private final WebClientUtil webClientUtil;

    @Override
    public void createCustomer(Customer customer, WebClientUtil.SuccessCallback<Void> successCallback, WebClientUtil.ErrorCallback errorCallback) {
        CustomerRequest request = CustomerRequest.builder()
                .name(customer.getName())
                .address(customer.getAddress())
                .email(customer.getEmail())
                .mobilePhoneNo(customer.getMobilePhoneNo())
                .build();
        webClientUtil.post(
                ConstantAPIUrl.CustomerAPI.BASE_URL,
                request,
                new ParameterizedTypeReference<CommonResponse<Void>>() {},
                response -> successCallback.onSuccess(response.getData()),
                errorCallback
        );
    }

    @Override
    public void getCustomers(QueryRequest request, WebClientUtil.SuccessCallback<Page<Customer>> successCallback, WebClientUtil.ErrorCallback errorCallback) {
        Map<String, String> query = Map.of(
                "page", String.valueOf(request.getPage()),
                "size", String.valueOf(request.getSize()),
                "q", request.getQuery()
        );

        webClientUtil.getWithQueryParam(
                ConstantAPIUrl.CustomerAPI.BASE_URL,
                new ParameterizedTypeReference<CommonResponse<List<CustomerResponse>>>() {},
                query,
                response -> {
                    List<Customer> customers = response.getData().stream().map(CustomerResponse::toCustomer).toList();
                    Page<Customer> page = Page.<Customer>builder()
                            .data(customers)
                            .paging(response.getPaging())
                            .build();
                    successCallback.onSuccess(page);
                },
                errorCallback
        );
    }

    @Override
    public void getCustomers(WebClientUtil.SuccessCallback<Page<Customer>> successCallback, WebClientUtil.ErrorCallback errorCallback) {
        Map<String, String> query = Map.of(
                "all", "true"
        );

        webClientUtil.getWithQueryParam(
                ConstantAPIUrl.CustomerAPI.BASE_URL,
                new ParameterizedTypeReference<CommonResponse<List<CustomerResponse>>>() {},
                query,
                response -> {
                    List<Customer> customers = response.getData().stream().map(CustomerResponse::toCustomer).toList();
                    Page<Customer> page = Page.<Customer>builder()
                            .data(customers)
                            .paging(response.getPaging())
                            .build();
                    successCallback.onSuccess(page);
                },
                errorCallback
        );
    }

    @Override
    public void updateCustomer(Customer customer, WebClientUtil.SuccessCallback<Void> successCallback, WebClientUtil.ErrorCallback errorCallback) {
        CustomerRequest request = CustomerRequest.builder()
                .id(customer.getId())
                .name(customer.getName())
                .address(customer.getAddress())
                .email(customer.getEmail())
                .mobilePhoneNo(customer.getMobilePhoneNo())
                .build();
        webClientUtil.put(
                ConstantAPIUrl.CustomerAPI.BASE_URL,
                request,
                new ParameterizedTypeReference<CommonResponse<Void>>() {},
                response -> successCallback.onSuccess(response.getData()),
                errorCallback
        );
    }

    @Override
    public void deleteCustomer(String id, WebClientUtil.SuccessCallback<Void> successCallback, WebClientUtil.ErrorCallback errorCallback) {
        webClientUtil.delete(
                ConstantAPIUrl.CustomerAPI.GET_BASE_URL_WITH_ID(id),
                new ParameterizedTypeReference<CommonResponse<Void>>() {},
                response -> successCallback.onSuccess(response.getData()),
                errorCallback
        );
    }
}
