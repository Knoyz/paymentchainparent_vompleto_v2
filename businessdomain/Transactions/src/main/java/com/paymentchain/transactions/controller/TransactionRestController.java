/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.paymentchain.transactions.controller;

import com.paymentchain.transactions.entities.Transaction;
import com.paymentchain.transactions.exception.BussinesRuleException;
import com.paymentchain.transactions.respository.TransactionRepository;
import com.paymentchain.transactions.services.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 *
 * @author sotobotero
 */
@RestController
@RequestMapping("/transaction")
public class TransactionRestController {
    
    @Autowired
    TransactionRepository transactionRepository;

    TransactionService ts;
    
      
    @GetMapping()
    public List<Transaction> list() {
        return transactionRepository.findAll();
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Transaction> get(@PathVariable long id) {
         return transactionRepository.findById(id).map(x -> ResponseEntity.ok(x)).orElse(ResponseEntity.notFound().build());      
    }
    
    @GetMapping("/transactions")
    public List<Transaction> get(@RequestParam String ibanAccount) {
      return transactionRepository.findByIbanAccount(ibanAccount);      
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<?> put(@PathVariable String id, @RequestBody Transaction input) {
        return null;
    }
    
    @PostMapping
    public ResponseEntity<?> post(@RequestBody Transaction input) throws BussinesRuleException {
        return ts.saveTransaction(input);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable String id) throws BussinesRuleException {
        return ts.deleteById(id);
    }
    
}
