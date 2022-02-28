package com.nttdata.transactionsaving.entity.dto;

import com.nttdata.transactionsaving.entity.enums.ESubType;

import lombok.Data;

@Data
public class SubType {

  private String id;

  private ESubType value;

}
