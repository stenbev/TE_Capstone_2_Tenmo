package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.Transfers;
import com.techelevator.tenmo.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;

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
    public List<Transfers> getAllTransfers(int userId) {
        List<Transfers> transfers = new ArrayList<>();
        String sql = "SELECT  amount, transfer_status_id, account_from, account_to, transfer_id, transfer_type_id  " +
                "                FROM transfer  " +
                "                JOIN account  a ON transfer.account_from =  a.account_id   " +
                "                WHERE user_id = ?";
        SqlRowSet result = jdbcTemplate.queryForRowSet(sql, userId);
        if (result.next()) {
            Transfers transfer = mapRowToTransfers(result);
            transfers.add(transfer);

        }
        return transfers;
    }

    private Transfers mapRowToTransfers(SqlRowSet result) {
        Transfers transfer = new Transfers();

        transfer.setTransferId(result.getInt("transfer_id"));
        transfer.setAccountFrom(result.getInt("account_from"));
        transfer.setAmount(result.getBigDecimal("amount"));
        transfer.setAccountTo(result.getInt("account_to"));
        transfer.setTransferStatusId(result.getInt("transfer_status_id"));
        transfer.setTransferTypeId(result.getInt("transfer_type_id"));
        return transfer;
    }


}
