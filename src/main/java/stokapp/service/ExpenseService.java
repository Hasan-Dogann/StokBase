package stokapp.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import stokapp.dto.ExpenseRequest;
import stokapp.dto.ExpenseResponse;
import stokapp.entity.Expense;
import stokapp.repository.ExpenseRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ExpenseService {

    private final ExpenseRepository expenseRepository;

    @Transactional
    public ExpenseResponse createExpense(ExpenseRequest request) {
        if (request.getAmount() == null || request.getAmount() <= 0) {
            throw new RuntimeException("Gider tutarı 0'dan büyük olmalıdır");
        }

        if (request.getDescription() == null || request.getDescription().trim().isEmpty()) {
            throw new RuntimeException("Gider açıklaması boş olamaz");
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        Expense expense = new Expense();
        expense.setAmount(request.getAmount());
        expense.setDescription(request.getDescription().trim());
        expense.setExpenseTime(LocalDateTime.now());
        expense.setCreatedBy(username);

        Expense savedExpense = expenseRepository.save(expense);

        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");

        return new ExpenseResponse(
                savedExpense.getId(),
                savedExpense.getAmount(),
                savedExpense.getDescription(),
                savedExpense.getExpenseTime().format(dateFormatter),
                savedExpense.getExpenseTime().format(timeFormatter),
                savedExpense.getCreatedBy()
        );
    }

    public List<ExpenseResponse> getExpensesByDate(LocalDate date) {
        LocalDateTime start = date.atStartOfDay();
        LocalDateTime end = date.plusDays(1).atStartOfDay();

        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");

        return expenseRepository.findByExpenseTimeBetweenOrderByExpenseTimeDesc(start, end)
                .stream()
                .map(expense -> new ExpenseResponse(
                        expense.getId(),
                        expense.getAmount(),
                        expense.getDescription(),
                        expense.getExpenseTime().format(dateFormatter),
                        expense.getExpenseTime().format(timeFormatter),
                        expense.getCreatedBy()
                ))
                .toList();
    }
}