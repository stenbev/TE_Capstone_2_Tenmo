package com.techelevator.tenmo.controller;

import com.techelevator.tenmo.dao.AccountDAO;
import com.techelevator.tenmo.dao.TransferDAO;
import com.techelevator.tenmo.dao.UserDao;
import com.techelevator.tenmo.model.Transfer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.List;

@RestController
@PreAuthorize("isAuthenticated()")
public class TransferController {

    @Autowired
    private TransferDAO transferDAO;
    @Autowired
    private UserDao userDao;

    @RequestMapping(value = "/account/transfer", method = RequestMethod.GET)
    public List<Transfer> getAllMyTransfer(Principal principal) {
        String userName = principal.getName();
        List<Transfer> result = transferDAO.getAllTransfers(userDao.findIdByUsername(userName));
        return result;
    }

    @RequestMapping(path = "/transfer/{id}", method = RequestMethod.GET)
    public Transfer getSelectedTransfer(@PathVariable int id) {
        Transfer transfer = transferDAO.getTransferById(id);
        return transfer;
    }

    @ResponseStatus(HttpStatus.CREATED)
    @RequestMapping(path = "/transfer", method = RequestMethod.POST)
    public String sendTransfer(@RequestBody Transfer transfers) {
        String result = transferDAO.sendTransfer(transfers.getAccountFrom(), transfers.getAccountTo(), transfers.getAmount());
        AccountDAO accountDAO = null;
        accountDAO.addToBalance(transfers.getAmount(), transfers.getAccountTo());
        accountDAO.subtractFromBalance(transfers.getAmount(), transfers.getAccountFrom());
        return result;
    }

    @ResponseStatus(HttpStatus.CREATED)
    @RequestMapping(path = "/request", method = RequestMethod.POST)
    public String requestTransfer(@RequestBody Transfer transfers) {
        String result = transferDAO.requestTransfer(transfers.getAccountFrom(), transfers.getAccountTo(), transfers.getAmount());
        return result;
    }

    @RequestMapping(path = "transfer/status/{statusId}", method = RequestMethod.PUT)
    public String updateRequest(@RequestBody Transfer transfers, @PathVariable int statusId) {
        String result = transferDAO.updateTransferRequest(transfers, statusId);
        return result;
    }

    @RequestMapping (path = "/transfer/pending", method = RequestMethod.GET)
    public List<Transfer> getPendingTransfer(Principal principal){
        String username = principal.getName();
        List<Transfer> result = transferDAO.getPendingTransfers(userDao.findIdByUsername(username));
        return result;
    }
}

