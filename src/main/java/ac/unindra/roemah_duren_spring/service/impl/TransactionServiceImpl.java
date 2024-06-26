package ac.unindra.roemah_duren_spring.service.impl;

import ac.unindra.roemah_duren_spring.constant.ConstantAPIUrl;
import ac.unindra.roemah_duren_spring.dto.request.QueryRequest;
import ac.unindra.roemah_duren_spring.dto.request.ReportQueryRequest;
import ac.unindra.roemah_duren_spring.dto.request.TransactionDetailRequest;
import ac.unindra.roemah_duren_spring.dto.request.TransactionRequest;
import ac.unindra.roemah_duren_spring.dto.response.CommonResponse;
import ac.unindra.roemah_duren_spring.dto.response.TransactionDetailResponse;
import ac.unindra.roemah_duren_spring.dto.response.TransactionResponse;
import ac.unindra.roemah_duren_spring.model.Page;
import ac.unindra.roemah_duren_spring.model.Supplier;
import ac.unindra.roemah_duren_spring.model.Transaction;
import ac.unindra.roemah_duren_spring.model.TransactionDetail;
import ac.unindra.roemah_duren_spring.service.TransactionService;
import ac.unindra.roemah_duren_spring.util.WebClientUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {
    private final WebClientUtil webClientUtil;

    @Override
    public void createTransaction(Transaction transaction, WebClientUtil.SuccessCallback<Void> successCallback, WebClientUtil.ErrorCallback errorCallback) {
        List<TransactionDetailRequest> transactionDetailRequests = transaction.getTransactionDetails().stream().map(transactionDetail -> TransactionDetailRequest.builder()
                .stockId(transactionDetail.getStock().getId())
                .qty(transactionDetail.getQty())
                .price(transactionDetail.getPrice())
                .build()).toList();

        TransactionRequest request = TransactionRequest.builder()
                .branchId(transaction.getBranch().getId())
                .customerId(transaction.getCustomer() != null ? transaction.getCustomer().getId() : null)
                .targetBranchId(transaction.getTargetBranch() != null ? transaction.getTargetBranch().getId() : null)
                .transactionDetails(transactionDetailRequests)
                .transactionType(transaction.getTransactionType())
                .build();

        webClientUtil.post(
                ConstantAPIUrl.TransactionAPI.BASE_URL,
                request,
                new ParameterizedTypeReference<>() {
                },
                response -> successCallback.onSuccess(null),
                errorCallback
        );
    }

    @Override
    public void getTransactions(QueryRequest request, WebClientUtil.SuccessCallback<Page<Transaction>> successCallback, WebClientUtil.ErrorCallback errorCallback) {
        Map<String, String> query = new HashMap<>();
        query.put("page", String.valueOf(request.getPage()));
        query.put("size", String.valueOf(request.getSize()));
        query.put("q", request.getQuery());

        webClientUtil.getWithQueryParam(
                ConstantAPIUrl.TransactionAPI.BASE_URL,
                new ParameterizedTypeReference<CommonResponse<List<TransactionResponse>>>() {
                },
                query,
                response -> {
                    List<Transaction> data = response.getData().stream().map(TransactionResponse::toTransaction).toList();
                    Page<Transaction> page = Page.<Transaction>builder()
                            .data(data)
                            .paging(response.getPaging())
                            .build();
                    successCallback.onSuccess(page);
                },
                errorCallback

        );
    }

    @Override
    public void getTransactions(ReportQueryRequest request, WebClientUtil.SuccessCallback<Page<Transaction>> successCallback, WebClientUtil.ErrorCallback errorCallback) {
        Map<String, String> map = new HashMap<>();
        map.put("startDate", request.getStartDate().toString());
        map.put("endDate", request.getEndDate().toString());
        if (request.getBranchId() != null) {
            map.put("branchId", request.getBranchId());
        }
        map.put("type", request.getTransactionType());

        webClientUtil.getWithQueryParam(
                ConstantAPIUrl.TransactionAPI.REPORT_URL,
                new ParameterizedTypeReference<CommonResponse<List<TransactionResponse>>>() {
                },
                map,
                response -> {
                    List<Transaction> data = response.getData().stream().map(TransactionResponse::toTransaction).toList();
                    Page<Transaction> page = Page.<Transaction>builder()
                            .data(data)
                            .paging(response.getPaging())
                            .build();
                    successCallback.onSuccess(page);
                },
                errorCallback
        );
    }

    @Override
    public void getDetail(String id, WebClientUtil.SuccessCallback<List<TransactionDetail>> successCallback, WebClientUtil.ErrorCallback errorCallback) {
        webClientUtil.get(
                ConstantAPIUrl.TransactionAPI.GET_DETAIL_WITH_ID(id),
                new ParameterizedTypeReference<CommonResponse<List<TransactionDetailResponse>>>() {
                },
                response -> {
                    List<TransactionDetail> transaction = response.getData().stream().map(TransactionDetailResponse::toTransactionDetail).toList();
                    successCallback.onSuccess(transaction);
                },
                errorCallback
        );
    }
}
