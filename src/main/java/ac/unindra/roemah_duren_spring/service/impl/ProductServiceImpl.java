package ac.unindra.roemah_duren_spring.service.impl;

import ac.unindra.roemah_duren_spring.constant.ConstantAPIUrl;
import ac.unindra.roemah_duren_spring.dto.request.PagingRequest;
import ac.unindra.roemah_duren_spring.dto.request.ProductRequest;
import ac.unindra.roemah_duren_spring.dto.request.QueryRequest;
import ac.unindra.roemah_duren_spring.dto.response.CommonResponse;
import ac.unindra.roemah_duren_spring.dto.response.ProductResponse;
import ac.unindra.roemah_duren_spring.dto.response.SupplierResponse;
import ac.unindra.roemah_duren_spring.model.Page;
import ac.unindra.roemah_duren_spring.model.Product;
import ac.unindra.roemah_duren_spring.model.Supplier;
import ac.unindra.roemah_duren_spring.service.ProductService;
import ac.unindra.roemah_duren_spring.util.WebClientUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
    private final WebClientUtil webClientUtil;

    @Override
    public void createProduct(Product product, WebClientUtil.SuccessCallback<Void> successCallback, WebClientUtil.ErrorCallback errorCallback) {
        ProductRequest request = ProductRequest.builder()
                .code(product.getCode())
                .name(product.getName())
                .price(product.getPrice())
                .description(product.getDescription())
                .supplierId(product.getSupplier().getId())
                .build();
        webClientUtil.post(
                ConstantAPIUrl.ProductAPI.BASE_URL,
                request,
                new ParameterizedTypeReference<CommonResponse<Void>>() {},
                response -> successCallback.onSuccess(null),
                errorCallback
        );
    }

    @Override
    public void getProducts(QueryRequest request, WebClientUtil.SuccessCallback<Page<Product>> successCallback, WebClientUtil.ErrorCallback errorCallback) {
        Map<String, String> query = Map.of(
                "page", String.valueOf(request.getPage()),
                "size", String.valueOf(request.getSize()),
                "q", request.getQuery()
        );

        getAll(successCallback, errorCallback, query);
    }

    @Override
    public void getAllProducts(WebClientUtil.SuccessCallback<Page<Product>> successCallback, WebClientUtil.ErrorCallback errorCallback) {
        Map<String, String> query = Map.of(
                "all", "true"
        );
        getAll(successCallback, errorCallback, query);
    }

    @Override
    public void updateProduct(Product product, WebClientUtil.SuccessCallback<Void> successCallback, WebClientUtil.ErrorCallback errorCallback) {
        ProductRequest request = ProductRequest.builder()
                .id(product.getId())
                .code(product.getCode())
                .name(product.getName())
                .price(product.getPrice())
                .description(product.getDescription())
                .supplierId(product.getSupplier().getId())
                .build();
        webClientUtil.put(
                ConstantAPIUrl.ProductAPI.BASE_URL,
                request,
                new ParameterizedTypeReference<CommonResponse<Void>>() {},
                response -> successCallback.onSuccess(null),
                errorCallback
        );
    }

    @Override
    public void deleteProduct(String id, WebClientUtil.SuccessCallback<Void> successCallback, WebClientUtil.ErrorCallback errorCallback) {
        webClientUtil.delete(
                ConstantAPIUrl.ProductAPI.GET_BASE_URL_WITH_ID(id),
                new ParameterizedTypeReference<CommonResponse<Void>>() {},
                response -> successCallback.onSuccess(null),
                errorCallback
        );
    }

    private void getAll(WebClientUtil.SuccessCallback<Page<Product>> successCallback, WebClientUtil.ErrorCallback errorCallback, Map<String, String> query) {
        webClientUtil.getWithQueryParam(
                ConstantAPIUrl.ProductAPI.BASE_URL,
                new ParameterizedTypeReference<CommonResponse<List<ProductResponse>>>() {},
                query,
                response -> {
                    List<Product> products = response.getData().stream().map(ProductResponse::toProduct).toList();
                    Page<Product> page = Page.<Product>builder()
                            .data(products)
                            .paging(response.getPaging())
                            .build();
                    successCallback.onSuccess(page);
                },
                errorCallback
        );
    }
}
