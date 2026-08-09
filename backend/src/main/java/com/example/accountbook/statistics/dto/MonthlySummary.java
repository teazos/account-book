package com.example.accountbook.statistics.dto;

import java.math.BigDecimal;

public class MonthlySummary {
    private BigDecimal income;
    private BigDecimal expense;
    private BigDecimal balance;
    public MonthlySummary(BigDecimal income, BigDecimal expense) { this.income = income; this.expense = expense; this.balance = income.subtract(expense); }
    public BigDecimal getIncome() { return income; }
    public void setIncome(BigDecimal income) { this.income = income; }
    public BigDecimal getExpense() { return expense; }
    public void setExpense(BigDecimal expense) { this.expense = expense; }
    public BigDecimal getBalance() { return balance; }
    public void setBalance(BigDecimal balance) { this.balance = balance; }
}
