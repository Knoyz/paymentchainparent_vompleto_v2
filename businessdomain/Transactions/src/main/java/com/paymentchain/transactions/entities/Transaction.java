/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.paymentchain.transactions.entities;

import java.time.LocalDateTime;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import lombok.Data;

/**
 *
 * @author sotobotero
 */
@Data
@Entity
public class Transaction {
    
  @Id
  @GeneratedValue(strategy=GenerationType.AUTO)
   private long id;
   private String reference;
   private String ibanAccount;
   private LocalDateTime date;
   private double amount ;
   private double fee;   
   private String description;
   private String status;
   private String channel;   
}
