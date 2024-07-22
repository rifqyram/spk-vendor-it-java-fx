package ac.unindra.spk_vendor_it.service.impl;

import ac.unindra.spk_vendor_it.constant.ResponseMessage;
import ac.unindra.spk_vendor_it.constant.UserRole;
import ac.unindra.spk_vendor_it.entity.UserCredential;
import ac.unindra.spk_vendor_it.entity.UserInfo;
import ac.unindra.spk_vendor_it.model.PageModel;
import ac.unindra.spk_vendor_it.model.UserInfoModel;
import ac.unindra.spk_vendor_it.repository.UserInfoRepository;
import ac.unindra.spk_vendor_it.security.PasswordManager;
import ac.unindra.spk_vendor_it.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserInfoRepository userInfoRepository;
    private final PasswordManager passwordManager;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void create(UserInfoModel userInfoModel) {
        UserInfo entity = userInfoModel.toEntity();
        entity.setUser(UserCredential.builder()
                .status(userInfoModel.getStatus())
                .role(UserRole.EMPLOYEE)
                .username(userInfoModel.getNip())
                .password(passwordManager.hashPassword(userInfoModel.getPassword()))
                .build());
        userInfoRepository.saveAndFlush(entity);
    }

    @Transactional(readOnly = true)
    @Override
    public Page<UserInfoModel> getAll(PageModel pageModel) {
        Pageable pageable = PageRequest.of(pageModel.getPage(), pageModel.getSize());
        Specification<UserInfo> specification = Specification.where(UserInfoRepository.hasSearchQuery(pageModel.getQuery()));
        Page<UserInfo> userInfoPage = userInfoRepository.findAll(specification, pageable);
        return userInfoPage.map(UserInfoModel::fromEntity);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void update(UserInfoModel userInfoModel) {
        UserInfo currentUser = findById(userInfoModel.getId());

        UserInfo userInfo = userInfoModel.toEntity();
        userInfo.getUser().setId(currentUser.getUser().getId());

        if (userInfoModel.getPassword() != null) {
            userInfo.getUser().setPassword(passwordManager.hashPassword(userInfoModel.getPassword()));
        }

        userInfoRepository.saveAndFlush(userInfo);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void delete(String id) {
        UserInfo userInfo = findById(id);
        userInfoRepository.delete(userInfo);
    }

    @Transactional
    protected UserInfo findById(String id) {
        return userInfoRepository.findById(id).orElseThrow(() -> new RuntimeException(ResponseMessage.DATA_NOT_FOUND));
    }
}