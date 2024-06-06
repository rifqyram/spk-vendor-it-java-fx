package ac.unindra.roemah_duren_spring.constant;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ConstantAPIUrl {
    public static String BASE_URL;

    @Value("${app.roemah-duren.base-url-api}")
    public void setBaseUrl(String baseUrl) {
        ConstantAPIUrl.BASE_URL = baseUrl;
    }

    public static class BranchAPI {
        public static final String BASE_URL = "/branches";
        private static final String BASE_URL_WITH_ID = "/branches/{id}";

        public static String GET_BASE_URL_WITH_ID(String id) {
            return BASE_URL_WITH_ID.replace("{id}", id);
        }

    }

    public static class SupplierAPI {

    }

    public static class CustomerAPI {

    }

    public static class ProductAPI {

    }

    public static class StockAPI {

    }

    public static class TransactionAPI {

    }
}
