package ac.unindra.roemah_duren_spring.service;

import ac.unindra.roemah_duren_spring.dto.request.QueryRequest;
import ac.unindra.roemah_duren_spring.model.Page;
import ac.unindra.roemah_duren_spring.model.Product;
import ac.unindra.roemah_duren_spring.util.WebClientUtil;

public interface ProductService {
    void createProduct(Product product, WebClientUtil.SuccessCallback<Void> successCallback, WebClientUtil.ErrorCallback errorCallback);
    void getProducts(QueryRequest request, WebClientUtil.SuccessCallback<Page<Product>> successCallback, WebClientUtil.ErrorCallback errorCallback);
    void getAllProducts(WebClientUtil.SuccessCallback<Page<Product>> successCallback, WebClientUtil.ErrorCallback errorCallback);
    void updateProduct(Product product, WebClientUtil.SuccessCallback<Void> successCallback, WebClientUtil.ErrorCallback errorCallback);
    void deleteProduct(String id, WebClientUtil.SuccessCallback<Void> successCallback, WebClientUtil.ErrorCallback errorCallback);
}
