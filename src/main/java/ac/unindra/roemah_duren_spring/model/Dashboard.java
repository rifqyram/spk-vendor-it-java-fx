package ac.unindra.roemah_duren_spring.model;

import javafx.beans.property.LongProperty;
import javafx.beans.property.SimpleLongProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
public class Dashboard {
    private LongProperty totalProduct = new SimpleLongProperty();
    private LongProperty totalCustomer = new SimpleLongProperty();
    private LongProperty totalTransaction = new SimpleLongProperty();
    private LongProperty totalRevenue = new SimpleLongProperty();
    private LongProperty totalExpend = new SimpleLongProperty();

    @Getter
    @Setter
    private List<Transaction> transactions;

    public long getTotalProduct() {
        return totalProduct.get();
    }


    public void setTotalProduct(long totalProduct) {
        this.totalProduct.set(totalProduct);
    }

    public long getTotalCustomer() {
        return totalCustomer.get();
    }

    public void setTotalCustomer(long totalCustomer) {
        this.totalCustomer.set(totalCustomer);
    }

    public long getTotalTransaction() {
        return totalTransaction.get();
    }

    public void setTotalTransaction(long totalTransaction) {
        this.totalTransaction.set(totalTransaction);
    }

    public long getTotalRevenue() {
        return totalRevenue.get();
    }

    public void setTotalRevenue(long totalRevenue) {
        this.totalRevenue.set(totalRevenue);
    }

    public long getTotalExpend() {
        return totalExpend.get();
    }

    public void setTotalExpend(long totalExpend) {
        this.totalExpend.set(totalExpend);
    }
}
