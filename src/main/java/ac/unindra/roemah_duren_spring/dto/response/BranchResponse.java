package ac.unindra.roemah_duren_spring.dto.response;

import ac.unindra.roemah_duren_spring.model.Branch;
import lombok.*;

import java.util.Collections;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BranchResponse {
    private String id;
    private String name;
    private String code;
    private String address;
    private String mobilePhoneNo;
    private List<StockResponse> stocks;

    public Branch toBranch() {
        Branch branch = new Branch();
        branch.setId(id);
        branch.setCode(code);
        branch.setName(name);
        branch.setAddress(address);
        branch.setMobilePhoneNo(mobilePhoneNo);
        branch.setStocks(stocks != null && !stocks.isEmpty() ? stocks.stream().map(StockResponse::toStock).toList() : Collections.emptyList());
        return branch;
    }
}
