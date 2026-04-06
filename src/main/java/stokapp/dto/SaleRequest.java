package stokapp.dto;

// Bu DTO tekli veya çoklu ürün satış isteğini indirimle birlikte taşır.
import lombok.Data;
import stokapp.entity.PaymentType;

import java.util.List;

@Data
public class SaleRequest {

    private List<SaleItemRequest> items;
    private PaymentType paymentType;
    private Double discountAmount;

    // Zorunlu değil
    private String customerName;

    // Zorunlu değil
    private String customerSurname;

    // Zorunlu değil
    private String customerTc;
}