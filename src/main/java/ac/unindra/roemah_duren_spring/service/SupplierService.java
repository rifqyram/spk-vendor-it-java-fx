package ac.unindra.roemah_duren_spring.service;

import ac.unindra.roemah_duren_spring.dto.request.QueryRequest;
import ac.unindra.roemah_duren_spring.model.Supplier;
import ac.unindra.roemah_duren_spring.model.Page;
import ac.unindra.roemah_duren_spring.util.WebClientUtil;

public interface SupplierService {
    void createSupplier(Supplier supplier, WebClientUtil.SuccessCallback<Void> successCallback, WebClientUtil.ErrorCallback errorCallback);
    void getSuppliers(QueryRequest request, WebClientUtil.SuccessCallback<Page<Supplier>> successCallback, WebClientUtil.ErrorCallback errorCallback);
    void getSuppliers(WebClientUtil.SuccessCallback<Page<Supplier>> successCallback, WebClientUtil.ErrorCallback errorCallback);
    void updateSupplier(Supplier supplier, WebClientUtil.SuccessCallback<Void> successCallback, WebClientUtil.ErrorCallback errorCallback);
    void deleteSupplier(String id, WebClientUtil.SuccessCallback<Void> successCallback, WebClientUtil.ErrorCallback errorCallback);
}
