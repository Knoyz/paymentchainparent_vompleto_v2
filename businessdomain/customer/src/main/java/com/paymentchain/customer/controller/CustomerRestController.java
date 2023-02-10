/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.paymentchain.customer.controller;

import com.paymentchain.customer.business.transactions.BussinesTransaction;
import com.paymentchain.customer.entities.Customer;
import com.paymentchain.customer.exception.BussinesRuleException;
import com.paymentchain.customer.respository.CustomerRepository;

import java.net.UnknownHostException;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author sotobotero
 */
@RestController
@RequestMapping("/customer")
public class CustomerRestController {

    @Autowired
    CustomerRepository customerRepository;

    @Autowired
    BussinesTransaction bt;

    @Value("${user.role}")
    private String role;


    //obtener toda la informacion de un cliente por el codigo de cliente
    @GetMapping("/full")
    public Customer getByCode(@RequestParam String code) {
        Customer customer = bt.getByCode(code);
        return customer;
    }

    //lista completa de clientes
    @GetMapping()
    public ResponseEntity<List<Customer>> list() {
        List<Customer> findAll = customerRepository.findAll();
        if (findAll == null || findAll.isEmpty()) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.ok(findAll);
        }
    }

    //mensaje introductorio (en desarrollo)
    @GetMapping("/hello")
    public String sayHello() {
        return "Hello your role is: " + role;
    }

    //obtener un cliente por id de cliente en base de datos
    @GetMapping("/{id}")
    public ResponseEntity<Customer> get(@PathVariable long id) throws BussinesRuleException{
        return bt.get(id);
    }


    //modificar informacion de un cliente por el id de cliente
    @PutMapping("/{id}")
    public ResponseEntity<?> put(@PathVariable String id, @RequestBody Customer input) throws BussinesRuleException
    {
        return bt.put(id,input);
    }

    //crear un cliente nuevo
    @PostMapping
    public ResponseEntity<?> post(@RequestBody Customer input) throws BussinesRuleException, UnknownHostException {
        Customer save = bt.save(input);
        return new ResponseEntity<>(save, HttpStatus.CREATED);
    }

    //borrar un cliente mediante su id en base de datos
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable String id) throws BussinesRuleException{
        return bt.deleteCustomerById(id);
    }

}
