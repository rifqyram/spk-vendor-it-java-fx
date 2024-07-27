package ac.unindra.spk_vendor_it.service.impl;

import ac.unindra.spk_vendor_it.constant.ResponseMessage;
import ac.unindra.spk_vendor_it.entity.Criteria;
import ac.unindra.spk_vendor_it.entity.Vendor;
import ac.unindra.spk_vendor_it.model.CriteriaModel;
import ac.unindra.spk_vendor_it.model.PageModel;
import ac.unindra.spk_vendor_it.model.VendorModel;
import ac.unindra.spk_vendor_it.repository.CriteriaRepository;
import ac.unindra.spk_vendor_it.repository.VendorRepository;
import ac.unindra.spk_vendor_it.service.VendorService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class VendorServiceImpl implements VendorService {
    private final VendorRepository vendorRepository;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void create(VendorModel vendorModel) {
        Vendor vendor = vendorModel.toEntity();
        vendorRepository.saveAndFlush(vendor);
    }

    @Transactional(readOnly = true)
    @Override
    public Page<VendorModel> getAll(PageModel pageModel) {
        Pageable pageable = PageRequest.of(pageModel.getPage(), pageModel.getSize(), Sort.Direction.ASC, "name");
        Specification<Vendor> specification = Specification.where(VendorRepository.hasSearchQuery(pageModel.getQuery()));
        Page<Vendor> vendorModel = vendorRepository.findAll(specification, pageable);
        return vendorModel.map(VendorModel::fromEntity);
    }

    @Override
    public List<VendorModel> getAll() {
        return vendorRepository.findAll().stream().map(VendorModel::fromEntity).toList();
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void delete(String id) {
        Vendor vendor = getVendor(id);
        vendorRepository.delete(vendor);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void update(VendorModel vendorModel) {
        getVendor(vendorModel.getId());
        Vendor vendor = vendorModel.toEntity();
        vendorRepository.saveAndFlush(vendor);
    }

    @Transactional
    @Override
    public Vendor getOne(String id) {
        return getVendor(id);
    }

    @Transactional(readOnly = true)
    protected Vendor getVendor(String id) {
        return vendorRepository.findById(id).orElseThrow(() -> new RuntimeException(ResponseMessage.DATA_NOT_FOUND));
    }
}
