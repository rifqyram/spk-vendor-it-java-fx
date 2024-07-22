package ac.unindra.spk_vendor_it.service;

import ac.unindra.spk_vendor_it.model.PageModel;
import ac.unindra.spk_vendor_it.model.UserInfoModel;
import org.springframework.data.domain.Page;

public interface UserService {
    void create(UserInfoModel userInfoModel);
    Page<UserInfoModel> getAll(PageModel pageModel);
    void update(UserInfoModel userInfoModel);
    void delete(String id);
}
