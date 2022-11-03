package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Account;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class jdbcAccountDAO implements AccountDAO {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    public jdbcAccountDAO() {
    }

    public jdbcAccountDAO(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public BigDecimal getBalance(int userId) {
        String sqlString = "SELECT balance FROM account WHERE user_id = ?";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sqlString, userId);
        BigDecimal balance = null;
        //condition - no symbols or letter only numbers no special symbols
        //can't write out the amount
            results = jdbcTemplate.queryForRowSet(sqlString, userId);
            if (results.next()) {
                balance = results.getBigDecimal("balance");
            }
        return balance;
    }
    @Override
    public BigDecimal getBalanceByAccount(int accountId) {
        String sqlString = "SELECT balance FROM account WHERE account_id = ?";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sqlString, accountId);
        BigDecimal balance = null;
        //condition - no symbols or letter only numbers no special symbols
        //can't write out the amount
        results = jdbcTemplate.queryForRowSet(sqlString, accountId);
        if (results.next()) {
            balance = results.getBigDecimal("balance");
        }
        return balance;
    }



    @Override
    public BigDecimal addToBalance(BigDecimal amountToAdd, int id) {
        Account account = findAccountById(id);
        //greater than 0 but we can accept x.xx so decimals
        //cant use letter
        //cant be a negative number
        BigDecimal newBalance = account.getBalance().add(amountToAdd);
        System.out.println("Your new balance is: " + newBalance);
        String sqlString = "UPDATE account SET balance = ? WHERE user_id = ?";
        jdbcTemplate.update(sqlString, newBalance, id);
        return account.getBalance();
}

    @Override
    public BigDecimal subtractFromBalance(BigDecimal amountToSubtract, int id) {
        Account account = findAccountById(id);
        //can't subtact more than your current balance
        //can't be 0 or negative
        //can't be letters
        if (amountToSubtract.compareTo(account.getBalance()) == -1) {
            System.out.println("Error! Amount subtracted more than balance.");
        } else {
            BigDecimal newBalance = account.getBalance().subtract(amountToSubtract);
            System.out.println("Your new balance is: " + newBalance);
            String sqlString = "UPDATE account SET balance = ? WHERE user_id = ?";
            jdbcTemplate.update(sqlString, newBalance, id);
        }
        return account.getBalance();
    }

    @Override
    public Account findUserById(int userId) {
        String sqlString = "SELECT user_id, balance, account_id FROM account WHERE user_id = ?";
        Account account = null;
        try {
            SqlRowSet results = jdbcTemplate.queryForRowSet(sqlString, userId);
            account = mapRowToAccount(results);
        } catch (DataAccessException e) {
            System.out.println("Error! Invalid Entry.");
        }
        return account;
    }

    @Override
    public Account findAccountById(int id) {
        String sqlString = "SELECT user_id, balance, account_id FROM account WHERE account_id = ?";
        Account account = null;
        SqlRowSet results = jdbcTemplate.queryForRowSet(sqlString, id);
        if (results.next()) {
            account = mapRowToAccount(results);
            return account;
        } else
            System.out.println("Account ID not found");
        return null;
    }

    private Account mapRowToAccount(SqlRowSet result) {
        Account account = new Account();
        account.setBalance(result.getBigDecimal("balance"));
        account.setAccountId(result.getInt("account_id"));
        account.setUserId(result.getInt("user_id"));
        return account;
    }
}
