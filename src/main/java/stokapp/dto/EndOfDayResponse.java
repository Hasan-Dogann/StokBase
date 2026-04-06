package stokapp.dto;

// Bu DTO gün sonu ekranının özet, satış detay ve gider verisini birlikte taşır.
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class EndOfDayResponse {

    private EndOfDaySummaryResponse summary;
    private List<EndOfDaySaleItemResponse> sales;
    private List<ExpenseResponse> expenses;
}