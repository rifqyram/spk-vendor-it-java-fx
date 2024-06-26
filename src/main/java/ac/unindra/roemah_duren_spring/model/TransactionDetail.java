package ac.unindra.roemah_duren_spring.model;

import javafx.beans.property.*;
import lombok.*;

@NoArgsConstructor
@EqualsAndHashCode
public class TransactionDetail {
    private StringProperty id = new SimpleStringProperty();
    private StringProperty transactionId = new SimpleStringProperty();

    @Getter
    @Setter
    private Stock stock;

    private IntegerProperty qty = new SimpleIntegerProperty();
    private LongProperty price = new SimpleLongProperty();

    public String getId() {
        return id.get();
    }

    public void setId(String id) {
        this.id.set(id);
    }

    public String getTransactionId() {
        return transactionId.get();
    }

    public void setTransactionId(String transactionId) {
        this.transactionId.set(transactionId);
    }

    public int getQty() {
        return qty.get();
    }

    public void setQty(int qty) {
        this.qty.set(qty);
    }

    public long getPrice() {
        return price.get();
    }

    public void setPrice(long price) {
        this.price.set(price);
    }
}
