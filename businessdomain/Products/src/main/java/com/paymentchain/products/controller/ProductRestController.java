/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.paymentchain.products.controller;

import com.paymentchain.products.services.ProductService;
import com.paymentchain.products.exeptions.BussinesRuleException;
import com.paymentchain.products.respository.ProductRepository;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import com.paymentchain.products.entities.Product;
import org.springframework.beans.factory.annotation.Value;

@RestController
@RequestMapping("/product")
public class ProductRestController {

    @Autowired
    ProductRepository productRepository;

    @Autowired
    ProductService ps;

    @Value("${user.role}")
    private String role;

    @GetMapping()
    public List<Product> list() {
        System.out.print("el role es : " + role);
        return productRepository.findAll();
    }

    @GetMapping("/{id}")
    public Product get(@PathVariable long id) {
        Product customer = productRepository.findById(id).get();
        return customer;
    }


    @PutMapping("/{id}")
    public ResponseEntity<?> put(@PathVariable String id, @RequestBody Product input) {
        return null;
    }

    @PostMapping
    public ResponseEntity<?> post(@RequestBody Product input) throws BussinesRuleException {
        Product saveProduct = ps.save(input);
        return ResponseEntity.ok(saveProduct);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable String id) {
        return null;
    }

}
