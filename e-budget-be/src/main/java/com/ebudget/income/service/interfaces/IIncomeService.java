package com.ebudget.income.service.interfaces;

import com.ebudget.income.resource.request.NewIncomeDTO;
import com.ebudget.income.resource.request.UpdateIncomeDTO;
import com.ebudget.income.resource.response.IncomeDTO;

import java.util.List;
import java.util.UUID;

public interface IIncomeService {
    IncomeDTO addIncome(NewIncomeDTO newIncomeDTO);
    void updateIncome(UUID incomeId, UpdateIncomeDTO updateIncomeDTO);
    IncomeDTO getIncome(UUID incomeId);
    List<IncomeDTO> getIncomes();
    void deleteIncome(UUID incomeId);
}
