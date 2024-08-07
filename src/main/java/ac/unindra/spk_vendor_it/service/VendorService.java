package ac.unindra.spk_vendor_it.service;

import ac.unindra.spk_vendor_it.entity.Vendor;
import ac.unindra.spk_vendor_it.model.PageModel;
import ac.unindra.spk_vendor_it.model.VendorModel;
import org.springframework.data.domain.Page;

import java.util.List;

public interface VendorService {
    void create(VendorModel vendorModel);
    Page<VendorModel> getAll(PageModel pageModel);
    List<VendorModel> getAll();
    void delete(String id);
    void update(VendorModel vendorModel);

    Vendor getOne(String id);
}
