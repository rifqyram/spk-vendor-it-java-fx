package ac.unindra.roemah_duren_spring.service;

import ac.unindra.roemah_duren_spring.model.Dashboard;
import ac.unindra.roemah_duren_spring.util.WebClientUtil;

public interface DashboardService {
    void getDashboardInfo(WebClientUtil.SuccessCallback<Dashboard> successCallback, WebClientUtil.ErrorCallback errorCallback);
}
