package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Account;
import java.math.BigDecimal;

public interface AccountDAO {
    BigDecimal getBalance(int userId);
    BigDecimal getBalanceByAccount(int accountId);
    BigDecimal addToBalance(BigDecimal amountToAdd, int id);
    BigDecimal subtractFromBalance(BigDecimal amountToSubtract, int id);
    Account findUserById(int userId);
    Account findAccountById(int id);
}
