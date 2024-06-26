package ac.unindra.roemah_duren_spring.service;

import ac.unindra.roemah_duren_spring.dto.request.QueryRequest;
import ac.unindra.roemah_duren_spring.dto.response.PagingResponse;
import ac.unindra.roemah_duren_spring.model.Customer;
import ac.unindra.roemah_duren_spring.model.Page;
import ac.unindra.roemah_duren_spring.util.WebClientUtil;

import java.util.List;

public interface CustomerService {
    void createCustomer(Customer customer, WebClientUtil.SuccessCallback<Void> successCallback, WebClientUtil.ErrorCallback errorCallback);
    void getCustomers(QueryRequest request, WebClientUtil.SuccessCallback<Page<Customer>> successCallback, WebClientUtil.ErrorCallback errorCallback);
    void getCustomers(WebClientUtil.SuccessCallback<Page<Customer>> successCallback, WebClientUtil.ErrorCallback errorCallback);
    void updateCustomer(Customer customer, WebClientUtil.SuccessCallback<Void> successCallback, WebClientUtil.ErrorCallback errorCallback);
    void deleteCustomer(String id, WebClientUtil.SuccessCallback<Void> successCallback, WebClientUtil.ErrorCallback errorCallback);
}
