package com.nttdata.transactionsaving.entity.dto;

import com.nttdata.transactionsaving.entity.enums.ETypeCustomer;

import lombok.Data;

@Data
public class TypeCustomer {

  private String id;

  private ETypeCustomer value;

  private SubType subType;

}
