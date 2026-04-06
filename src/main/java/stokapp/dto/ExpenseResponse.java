package stokapp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ExpenseResponse {

    private Long id;
    private Double amount;
    private String description;
    private String expenseDate;
    private String expenseTime;
    private String createdBy;
}