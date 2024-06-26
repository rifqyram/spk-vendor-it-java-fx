package ac.unindra.roemah_duren_spring.service.impl;

import ac.unindra.roemah_duren_spring.constant.ConstantAPIUrl;
import ac.unindra.roemah_duren_spring.dto.response.CommonResponse;
import ac.unindra.roemah_duren_spring.dto.response.DashboardResponse;
import ac.unindra.roemah_duren_spring.dto.response.TransactionResponse;
import ac.unindra.roemah_duren_spring.model.Dashboard;
import ac.unindra.roemah_duren_spring.service.DashboardService;
import ac.unindra.roemah_duren_spring.util.WebClientUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {
    private final WebClientUtil webClientUtil;

    @Override
    public void getDashboardInfo(WebClientUtil.SuccessCallback<Dashboard> successCallback, WebClientUtil.ErrorCallback errorCallback) {
        webClientUtil.get(
                ConstantAPIUrl.DashboardAPI.BASE_URL,
                new ParameterizedTypeReference<CommonResponse<DashboardResponse>>() {
                },
                response -> {
                    Dashboard dashboard = new Dashboard();
                    dashboard.setTotalProduct(response.getData().getTotalProducts());
                    dashboard.setTotalCustomer(response.getData().getTotalCustomers());
                    dashboard.setTotalTransaction(response.getData().getTotalTransactionsPerDay());
                    dashboard.setTotalRevenue(response.getData().getTotalRevenue());
                    dashboard.setTotalExpend(response.getData().getTotalExpend());
                    dashboard.setTransactions(response.getData().getTransactions().stream().map(TransactionResponse::toTransaction).toList());
                    successCallback.onSuccess(dashboard);
                },
                errorCallback
        );
    }
}
