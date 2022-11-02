package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Transfers;

import java.math.BigDecimal;
import java.util.List;

public interface TransferDao {
    public List<Transfers> getAllTransfers(int userId); //important
    public Transfers getTransferById(int transactionId);  //imponrant
    public String sendTransfer(int userFrom, int userTo, BigDecimal amount); //important
    public String requestTransfer(int userFrom, int userTo, BigDecimal amount); //not important
    public List<Transfers> getPendingRequests(int userId); //not important
    public String updateTransfersRequest(Transfers transfer, int statusId);//not important
}
