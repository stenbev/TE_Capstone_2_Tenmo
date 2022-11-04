package com.techelevator.tenmo.controller;

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

    public TransferController(TransferDAO transferDAO, UserDao userDao) {
        this.transferDAO = transferDAO;
        this.userDao = userDao;
    }

    @RequestMapping(value = "/account/transfer", method = RequestMethod.GET)
    public List<Transfer> getAllMyTransfer(Principal principal) {
        String userName = principal.getName();
        List<Transfer> result = transferDAO.getAllTransfers(userDao.findIdByUsername(userName));
        return result;
    }

    @RequestMapping(path = "/transfer", method = RequestMethod.GET)
    public Transfer getSelectedTransfer(Principal principal) {
        String userName = principal.getName();
        Transfer transfer = transferDAO.getTransferById(userDao.findIdByUsername(userName));
        return transfer;
    }

    @ResponseStatus(HttpStatus.CREATED)
    @RequestMapping(path = "/transfer", method = RequestMethod.POST)
    public String sendTransfer(@RequestBody Transfer transfers) {
        String result = transferDAO.sendTransfer(transfers.getAccountFrom(), transfers.getAccountTo(), transfers.getAmount());
        return result;
    }

    @ResponseStatus(HttpStatus.CREATED)
    @RequestMapping(path = "/request", method = RequestMethod.POST)
    public String requestTransfer(@RequestBody Transfer transfers) {
        String result = transferDAO.sendTransfer(transfers.getAccountFrom(), transfers.getAccountTo(), transfers.getAmount());
        return result;
    }

    @RequestMapping(path = "transfer/status/{statusId}", method = RequestMethod.PUT)
    public String updateRequest(@RequestBody Transfer transfers, @PathVariable int statusID) {
        String result = transferDAO.updateTransferRequest(transfers, statusID);
        return result;
    }
}

