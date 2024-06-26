package ac.unindra.roemah_duren_spring.service;

import ac.unindra.roemah_duren_spring.dto.request.QueryRequest;
import ac.unindra.roemah_duren_spring.dto.request.ReportQueryRequest;
import ac.unindra.roemah_duren_spring.model.Page;
import ac.unindra.roemah_duren_spring.model.Transaction;
import ac.unindra.roemah_duren_spring.model.TransactionDetail;
import ac.unindra.roemah_duren_spring.util.WebClientUtil;

import java.util.List;

public interface TransactionService {
    void createTransaction(Transaction transaction, WebClientUtil.SuccessCallback<Void> successCallback, WebClientUtil.ErrorCallback errorCallback);

    void getTransactions(QueryRequest request, WebClientUtil.SuccessCallback<Page<Transaction>> successCallback, WebClientUtil.ErrorCallback errorCallback);
    void getTransactions(ReportQueryRequest request, WebClientUtil.SuccessCallback<Page<Transaction>> successCallback, WebClientUtil.ErrorCallback errorCallback);

    void getDetail(String id, WebClientUtil.SuccessCallback<List<TransactionDetail>> successCallback, WebClientUtil.ErrorCallback errorCallback);
}
