/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.paymentchain.transactions.services;

import com.paymentchain.transactions.entities.Transaction;
import com.paymentchain.transactions.exception.BussinesRuleException;
import com.paymentchain.transactions.respository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * @author Knoyz
 */
@Service
public class TransactionService {

    @Autowired
    TransactionRepository transactionRepository;

    public ResponseEntity<?> deleteById(String id) throws BussinesRuleException {
        HttpStatus httpStatus = HttpStatus.OK;
        Optional<Transaction> transaction = transactionRepository.findById(Long.valueOf(id));
        if (transaction.isPresent()) {
            httpStatus = HttpStatus.NOT_FOUND;
            throw new BussinesRuleException("1026","id no encontrado, proporcione un id valido", httpStatus);
        }else {
            transactionRepository.deleteById(Long.valueOf(id));
        }
        return new ResponseEntity<>(transaction,httpStatus);
    }

    public ResponseEntity<?> saveTransaction(Transaction input) throws BussinesRuleException{
        HttpStatus httpStatus = HttpStatus.OK;

        if(input.getIbanAccount() != null &&
                input.getStatus() != null &&
                input.getChannel() != null &&
                input.getDate() != null &&
                input.getReference() != null &&
                input.getDescription() != null){
            transactionRepository.save(input);
        }else{
            httpStatus = HttpStatus.PRECONDITION_REQUIRED;
            throw new BussinesRuleException("1026","Se necesitan mas datos",httpStatus);
        }
        return new ResponseEntity<>(input, httpStatus);
    }
}

