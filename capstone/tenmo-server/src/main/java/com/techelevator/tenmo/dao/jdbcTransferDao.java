package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Transfer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Component

public class jdbcTransferDao implements TransferDAO {

    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private AccountDAO accountDAO;


    public jdbcTransferDao() {
    }

    public jdbcTransferDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Transfer> getAllTransfers(int userId) {
        List<Transfer> transfers = new ArrayList<>();
        String sql = "SELECT * FROM transfer WHERE account_from = (SELECT account_id FROM account WHERE user_id = ?)OR account_to = (SELECT account_id FROM account WHERE user_id = ?)";
        SqlRowSet result = jdbcTemplate.queryForRowSet(sql, userId, userId);
        while (result.next()) {
            Transfer transfer = mapRowToTransfers(result);
            transfers.add(transfer);

        }
        return transfers;
    }

    @Override
    public Transfer getTransferById(int transferId) {
        Transfer transfer = new Transfer();
        String sql = "SELECT  amount, transfer_status_id, account_from, account_to, transfer_id, transfer_type_id   " +
                "FROM transfer   " +
                "WHERE transfer_id = ?";
        SqlRowSet result = jdbcTemplate.queryForRowSet(sql, transferId);
        if (result.next()) {
            transfer = mapRowToTransfers(result);
            return transfer;

        } else System.out.println("Transfer ID not found");
        return null;

    }

    @Override
    public String sendTransfer(int userFrom, int userTo, BigDecimal amount) {
        if (userFrom == userTo) {
            return "Sorry, you cannot transfer money to yourself";

        }

        if (amount.compareTo(accountDAO.getBalanceByAccount(userFrom)) == -1 && amount.compareTo(new BigDecimal(0)) == 1) {

            String sql = "INSERT INTO transfer (transfer_type_id, transfer_status_id, account_from, account_to, amount) " +
                    "VALUES (2, 2, ?, ?, ?)";
            jdbcTemplate.update(sql, userFrom, userTo, amount);
            accountDAO.addToBalance(amount, userTo);
            accountDAO.subtractFromBalance(amount, userFrom);

            return "Transfer Complete";
        }
        return "Transaction Failed";
    }


    private Transfer mapRowToTransfers(SqlRowSet result) {
        Transfer transfer = new Transfer();

        transfer.setTransferId(result.getInt("transfer_id"));
        transfer.setAccountFrom(result.getInt("account_from"));
        transfer.setAmount(result.getBigDecimal("amount"));
        transfer.setAccountTo(result.getInt("account_to"));
        transfer.setTransferStatusId(result.getInt("transfer_status_id"));
        transfer.setTransferTypeId(result.getInt("transfer_type_id"));
        return transfer;
    }


}
