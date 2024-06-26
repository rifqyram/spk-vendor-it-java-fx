package ac.unindra.roemah_duren_spring.util;

import ac.unindra.roemah_duren_spring.dto.response.CommonResponse;
import ac.unindra.roemah_duren_spring.repository.TokenManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriBuilder;
import reactor.core.publisher.Mono;

import java.util.Map;

@Component
public class WebClientUtil {
    private final WebClient webClient;

    @Autowired
    public WebClientUtil(TokenManager tokenManager, WebClient.Builder builder) {
        this.webClient = builder
                .filter((request, next) -> next.exchange(
                        ClientRequest.from(request)
                                .headers(headers -> {
                                    String token = tokenManager.getToken();
                                    if (token != null) {
                                        headers.setBearerAuth(token);
                                    }
                                }).build()
                ))
                .build();
    }

    public <T, R> void post(String uri, T requestBody, ParameterizedTypeReference<CommonResponse<R>> responseType,
                            SuccessCommonResponseCallback<R> onSuccess, ErrorCallback onError) {
        callApi(uri, HttpMethod.POST, requestBody, responseType, onSuccess, onError);
    }

    public <T, R> void postWithQueryParam(String uri, T requestBody, ParameterizedTypeReference<CommonResponse<R>> responseType,
                                         Map<String, String> queryParams, SuccessCommonResponseCallback<R> onSuccess, ErrorCallback onError) {
        callApiWithQueryParam(uri, HttpMethod.POST, requestBody, responseType, queryParams, onSuccess, onError);
    }

    public <T, R> void put(String uri, T requestBody, ParameterizedTypeReference<CommonResponse<R>> responseType,
                           SuccessCommonResponseCallback<R> onSuccess, ErrorCallback onError) {
        callApi(uri, HttpMethod.PUT, requestBody, responseType, onSuccess, onError);
    }

    public <T, R> void putWithQueryParam(String uri, T requestBody, ParameterizedTypeReference<CommonResponse<R>> responseType,
                                        Map<String, String> queryParams, SuccessCommonResponseCallback<R> onSuccess, ErrorCallback onError) {
        callApiWithQueryParam(uri, HttpMethod.PUT, requestBody, responseType, queryParams, onSuccess, onError);
    }

    public <R> void get(String uri, ParameterizedTypeReference<CommonResponse<R>> responseType,
                        SuccessCommonResponseCallback<R> onSuccess, ErrorCallback onError) {
        callApi(uri, HttpMethod.GET, null, responseType, onSuccess, onError);
    }

    public <R> void getWithQueryParam(String uri, ParameterizedTypeReference<CommonResponse<R>> responseType,
                                     Map<String, String> queryParams, SuccessCommonResponseCallback<R> onSuccess, ErrorCallback onError) {
        callApiWithQueryParam(uri, HttpMethod.GET, null, responseType, queryParams, onSuccess, onError);
    }

    public <R> void delete(String uri, ParameterizedTypeReference<CommonResponse<R>> responseType,
                           SuccessCommonResponseCallback<R> onSuccess, ErrorCallback onError) {
        callApi(uri, HttpMethod.DELETE, null, responseType, onSuccess, onError);
    }

    public <R> void deleteWithQueryParam(String uri, ParameterizedTypeReference<CommonResponse<R>> responseType,
                                        Map<String, String> queryParams, SuccessCommonResponseCallback<R> onSuccess, ErrorCallback onError) {
        callApiWithQueryParam(uri, HttpMethod.DELETE, null, responseType, queryParams, onSuccess, onError);
    }

    private  <T, R> void callApi(String uri, HttpMethod method, T requestBody,
                               ParameterizedTypeReference<CommonResponse<R>> responseType,
                               SuccessCommonResponseCallback<R> onSuccess, ErrorCallback onError) {

        WebClient.RequestBodySpec request = webClient.method(method).uri(uri).accept(MediaType.APPLICATION_JSON);
        WebClient.RequestHeadersSpec<?> requestHeadersSpec;

        if (requestBody != null && (HttpMethod.POST.equals(method) || HttpMethod.PUT.equals(method))) {
            requestHeadersSpec = request.bodyValue(requestBody);
        } else {
            requestHeadersSpec = request;
        }

        requestHeadersSpec.retrieve()
                .onStatus(HttpStatusCode::isError, response -> response.bodyToMono(new ParameterizedTypeReference<CommonResponse<String>>() {
                }).flatMap(error -> Mono.error(new RuntimeException(error.getMessage()))))
                .bodyToMono(responseType)
                .subscribe(onSuccess::onSuccess, onError::onError);
    }

    private  <T, R> void callApiWithQueryParam(String uri, HttpMethod method, T requestBody,
                                             ParameterizedTypeReference<CommonResponse<R>> responseType,
                                             Map<String, String> queryParams,
                                             SuccessCommonResponseCallback<R> onSuccess, ErrorCallback onError) {

        WebClient.RequestBodySpec requestSpec = webClient.method(method)
                .uri(builder -> {
                    UriBuilder uriBuilder = builder.path(uri);
                    queryParams.forEach(uriBuilder::queryParam);
                    return uriBuilder.build();
                })
                .accept(MediaType.APPLICATION_JSON);

        WebClient.RequestHeadersSpec<?> headersSpec;
        if (requestBody != null && (HttpMethod.POST.equals(method) || HttpMethod.PUT.equals(method))) {
            headersSpec = requestSpec.bodyValue(requestBody);
        } else {
            headersSpec = requestSpec;
        }

        headersSpec.retrieve()
                .onStatus(HttpStatusCode::isError, response ->
                        response.bodyToMono(new ParameterizedTypeReference<CommonResponse<String>>() {})
                                .flatMap(error -> Mono.error(new RuntimeException(error.getMessage())))
                )
                .bodyToMono(responseType)
                .subscribe(onSuccess::onSuccess, onError::onError);
    }

    @FunctionalInterface
    public interface SuccessCommonResponseCallback<T> {
        void onSuccess(CommonResponse<T> response);
    }

    public interface SuccessCallback<T> {
        void onSuccess(T response);
    }

    @FunctionalInterface
    public interface ErrorCallback {
        void onError(Throwable error);
    }
}
