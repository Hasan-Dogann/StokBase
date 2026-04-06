package stokapp.dto;

// Bu DTO fiş içindeki her ürün satırını temsil eder.
import lombok.Data;

@Data
public class SaleItemRequest {
    private Long productId;
    private Integer quantity;
}