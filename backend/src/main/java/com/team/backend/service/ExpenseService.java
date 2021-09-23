package com.team.backend.service;

import com.team.backend.helpers.ExpenseHolder;
import com.team.backend.model.Expense;
import com.team.backend.model.User;
import com.team.backend.model.Wallet;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface ExpenseService {

    void save(ExpenseHolder expenseHolder, int walletId);
    void save(Expense expense);
    void delete(Expense expense);
    void deleteAllByWallet(Wallet wallet);

    Optional<Expense> findById(int id);
    List<Expense> findAllByWalletOrderByDate(Wallet wallet);
    List<Expense> findAllByWalletAndUser(Wallet wallet, User user);

    void editUserList(Expense updatedExpense, Expense newExpense, List<String> userList);
    void edit(Expense updatedExpense, Expense newExpense);
    void deleteExpense(Expense expense);
    void calculateNewBalance(Wallet wallet, Expense expense, BigDecimal cost);
    BigDecimal calculateExpensesCost(Wallet wallet);
    BigDecimal calculateExpensesCostForUser(Wallet wallet, User user);

    Map<String, Object> getOne(int id);
    Map<String, Object> getAll(int id);
}
