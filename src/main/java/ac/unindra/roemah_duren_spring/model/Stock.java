package ac.unindra.roemah_duren_spring.model;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import lombok.*;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class Stock {

    private StringProperty id = new SimpleStringProperty();

    @Setter
    @Getter
    private Product product;

    @Getter
    @Setter
    private Branch branch;

    private IntegerProperty stock = new SimpleIntegerProperty();

    private StringProperty updatedDate = new SimpleStringProperty();

    @Getter
    @Setter
    private List<TransactionDetail> transactionDetails;

    public String getId() {
        return id.get();
    }

    public void setId(String id) {
        this.id.set(id);
    }

    public int getStock() {
        return stock.get();
    }

    public void setStock(int stock) {
        this.stock.set(stock);
    }

    public String getUpdatedDate() {
        return updatedDate.get();
    }

    public void setUpdatedDate(String updatedDate) {
        this.updatedDate.set(updatedDate);
    }


}
