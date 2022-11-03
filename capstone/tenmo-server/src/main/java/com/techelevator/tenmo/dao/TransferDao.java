package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Transfer;

import java.math.BigDecimal;
import java.util.List;

public interface TransferDAO {
    public List<Transfer> getAllTransfers(int userId); //important Transfers getTransfersById(int transferId);

    public Transfer getTransferById(int transactionId);  //imponrant

    public String sendTransfer(int userFrom, int userTo, BigDecimal amount); //important


    //   public String requestTransfer(int userFrom, int userTo, BigDecimal amount); //not important
//    public List<Transfers> getPendingRequests(int userId); //not important
    //   public String updateTransfersRequest(Transfers transfer, int statusId);//not important
}
