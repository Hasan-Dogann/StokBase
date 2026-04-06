package stokapp.dto;

// Bu DTO gün sonu ekranındaki her ürün satırını indirim bilgisiyle temsil eder.
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class EndOfDaySaleItemResponse {

    private Long saleId;
    private Long saleItemId;
    private Long productId;
    private String productName;
    private Integer quantity;
    private Double unitPrice;
    private Double lineTotal;
    private Double originalTotalPrice;
    private Double discountAmount;
    private Double netTotalPrice;
    private String paymentType;
    private String saleDate;
    private String saleTime;
    private String customerName;
    private String customerSurname;
    private String customerTc;
    private String sellerUsername;
}