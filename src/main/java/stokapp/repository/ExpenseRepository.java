package stokapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import stokapp.entity.Expense;

import java.time.LocalDateTime;
import java.util.List;

public interface ExpenseRepository extends JpaRepository<Expense, Long> {

    List<Expense> findByExpenseTimeBetweenOrderByExpenseTimeDesc(LocalDateTime start, LocalDateTime end);
}