package com.techelevator.tenmo.controller;


import com.techelevator.tenmo.dao.TransferDAO;
import com.techelevator.tenmo.dao.UserDao;
import com.techelevator.tenmo.model.Transfer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

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
    @RequestMapping(value = "/account/transfer/{id}", method = RequestMethod.GET)
    public List<Transfer> getAllMyTransfer(@PathVariable int id) {
        return transferDAO.getAllTransfers(id);


    }
    @RequestMapping(path = "/transfer/{id}", method = RequestMethod.GET)
    public Transfer getSelectedTransfer(@PathVariable int id) {
        return transferDAO.getTransferById(id);


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
}

