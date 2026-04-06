package stokapp.dto;

// Bu DTO gün sonu özet bilgilerini brüt satış, net satış, indirim, gider ve elde kalan tutarla taşır.
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class EndOfDaySummaryResponse {

    private String date;
    private int totalSalesCount;
    private int totalQuantity;
    private double grossSales;
    private double netSales;
    private double totalDiscount;
    private double totalNakit;
    private double totalPos;
    private double totalExpenses;
    private double cashInHand;
}