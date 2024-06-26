package ac.unindra.roemah_duren_spring.dto.response;

import ac.unindra.roemah_duren_spring.model.Product;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductResponse {
    private String id;
    private String code;
    private String name;
    private Long price;
    private String description;
    private SupplierResponse supplier;

    public Product toProduct() {
        Product product = new Product();
        product.setId(id);
        product.setCode(code);
        product.setName(name);
        product.setPrice(price);
        product.setDescription(description);
        product.setSupplier(supplier.toSupplier());
        return product;
    }
}
