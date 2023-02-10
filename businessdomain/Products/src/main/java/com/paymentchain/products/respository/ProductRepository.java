/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.paymentchain.products.respository;


import org.springframework.data.jpa.repository.JpaRepository;
import com.paymentchain.products.entities.Product;

/**
 *
 * @author sotobotero
 */
public interface ProductRepository extends JpaRepository<Product, Long> {

 
   }
