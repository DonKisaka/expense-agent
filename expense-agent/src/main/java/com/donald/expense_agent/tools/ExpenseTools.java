package com.donald.expense_agent.tools;

import com.donald.expense_agent.model.Category;
import com.donald.expense_agent.model.Expense;
import com.donald.expense_agent.repository.ExpenseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Component
@RequiredArgsConstructor
public class ExpenseTools {
    private final ExpenseRepository expenseRepository;

    @Tool(description = "Add a new expense. Requires description, amount, category and date in YYYY-MM-DD format.")
    public Expense addExpense(String description, BigDecimal amount, Category category, String date) {
        Expense expense = new Expense();
        expense.setDescription(description);
        expense.setAmount(amount);
        expense.setCategory(category);
        expense.setDate(LocalDate.parse(date));
        return expenseRepository.save(expense);
    }

    @Tool(description = "Get all expenses stored in the system.")
    public List<Expense> getAllExpenses() {
        return expenseRepository.findAll();
    }

    @Tool(description = "Get expenses by category. Example categories: Food, Transport, Shopping, Bills.")
    public List<Expense> getExpensesByCategory(Category category) {
        return expenseRepository.findByCategory(category);
    }

    @Tool(description = "Get expenses between two dates. Dates must be in YYYY-MM-DD format.")
    public List<Expense> getExpensesByDateRange(String startDate, String endDate) {
        return expenseRepository.findByDateBetween(
                LocalDate.parse(startDate),
                LocalDate.parse(endDate)
        );
    }

    @Tool(description = "Delete an expense by its ID.")
    public String deleteExpense(Long id) {
        if (!expenseRepository.existsById(id)) return "Expense with ID " + id + " not found.";
        expenseRepository.deleteById(id);
        return "Expense " + id + " deleted successfully.";
    }

    @Tool(description = "Get total amount spent in a specific category.")
    public String getTotalByCategory(Category category) {
        var total = expenseRepository.sumByCategory(category);
        if (total == null) return "No expenses found for " + category;
        return "Total spent on " + category + ": sh" + total;
    }

}
