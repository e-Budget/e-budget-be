package com.ebudget.expense.repository;

import com.ebudget.expense.model.Expense;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.UUID;

@ApplicationScoped
public class ExpenseRepository implements PanacheRepositoryBase<Expense, UUID> {
}
