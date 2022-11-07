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
    public List<Transfer> getPendingTransfers(int userId) {
        List<Transfer> transfers = new ArrayList<>();
        String sql = "SELECT transfer.transfer_id, transfer.transfer_type_id, transfer.account_from, transfer.account_to, transfer.amount, account.account_id, account.user_id, account.balance, transfer.transfer_status_id, transfer_status.transfer_status_desc " +
                "FROM transfer " +
                "JOIN account ON account.account_id = transfer.account_from " +
                "JOIN transfer_status ON transfer.transfer_status_id = transfer_status.transfer_status_id " +
                "WHERE user_id = ? AND  transfer_status_desc = 'Pending' ;";
        SqlRowSet result = jdbcTemplate.queryForRowSet(sql, userId);
        while (result.next()) {
            Transfer transfer = mapRowToTransfers(result);
            transfers.add(transfer);
        }
        return transfers;
    }

    @Override
    public String updateTransferRequest(Transfer transfer, int statusID) {
        while (statusID == 3 || statusID == 2)  {
            String sql = "UPDATE transfer SET transfer_status_id = ? where transfer_id = ?; ";
            jdbcTemplate.update(sql, statusID, transfer.getTransferId());
            accountDAO.addToBalance(transfer.getAmount(), transfer.getAccountFrom());
            accountDAO.subtractFromBalance(transfer.getAmount(), transfer.getAccountFrom());
            return "OK";
        }
        while (!(accountDAO.getBalance(transfer.getAccountFrom()).compareTo(transfer.getAmount()) == -1)) {
            String sql = "UPDATE transfer SET transfer_status_id = ? where transfer_id = ?; ";
            jdbcTemplate.update(sql, statusID, transfer.getTransferId());
            accountDAO.addToBalance(transfer.getAmount(), transfer.getAccountTo());
            accountDAO.subtractFromBalance(transfer.getAmount(), transfer.getAccountFrom());
            return "Completed";
        }
        return "Count not update Transfer Request";
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
    public String requestTransfer(int userFrom, int userTo, BigDecimal amount) {
        if (userFrom == userTo) {

            return "Sorry, you cannot request money from yourself.";
        }
        if (amount.compareTo(new BigDecimal(0)) == 1) {
            String sql = "INSERT INTO transfer (transfer_type_id, transfer_status_id, account_from, account_to, amount) " +
                    "  VALUES (1, 1, ?, ?, ?)";
            jdbcTemplate.update(sql, userFrom, userTo, amount);

            return "Transfer Request Sent";
        }
        return "Transaction Failed";
    }

    @Override
    public String sendTransfer(int userFrom, int userTo, BigDecimal amount) {
        if (userFrom == userTo) {
            return "Sorry, you cannot transfer money to yourself.";
        }
        if (amount.compareTo(accountDAO.getBalanceByAccount(userFrom)) == -1 && amount.compareTo(new BigDecimal(0)) == 1) {

            String sql = "INSERT INTO transfer (transfer_type_id, transfer_status_id, account_from, account_to, amount) " +
                    "VALUES (2, 2, ?, ?, ?)";
            jdbcTemplate.update(sql, userFrom, userTo, amount);
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
