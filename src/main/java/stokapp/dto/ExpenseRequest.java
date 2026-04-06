package stokapp.dto;

import lombok.Data;

@Data
public class ExpenseRequest {

    private Double amount;
    private String description;
}