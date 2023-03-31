package reactive.mongo.product.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductDto {
    private String id;
    private String name;
    private int qty;
    private double price;

    public double getTotalPrice() {
        double tPrice = qty * price;
        return Math.round(tPrice * 100) / 100.0;
    }
}
