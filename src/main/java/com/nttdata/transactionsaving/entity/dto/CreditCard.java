package com.nttdata.transactionsaving.entity.dto;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreditCard {

  private String id;

  private String cardNumber;

  private Customer customer;

  private Double limitCredit;

  private Date expiration;

  private Date createAt;

}
