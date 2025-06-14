package com.ebudget.income.repository;

import com.ebudget.income.model.Income;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.UUID;

@ApplicationScoped
public class IncomeRepository implements PanacheRepositoryBase<Income, UUID> {
}
