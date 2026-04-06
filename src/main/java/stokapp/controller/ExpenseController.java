package stokapp.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import stokapp.dto.ExpenseRequest;
import stokapp.dto.ExpenseResponse;
import stokapp.service.ExpenseService;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/expenses")
@RequiredArgsConstructor
public class ExpenseController {

    private final ExpenseService expenseService;

    @PostMapping
    public ExpenseResponse createExpense(@RequestBody ExpenseRequest request) {
        return expenseService.createExpense(request);
    }

    @GetMapping
    public List<ExpenseResponse> getExpensesByDate(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        return expenseService.getExpensesByDate(date);
    }
}