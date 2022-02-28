package com.nttdata.transactionsaving.entity;

import java.time.LocalDateTime;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.nttdata.transactionsaving.entity.dto.SavingAccount;
import com.nttdata.transactionsaving.entity.enums.ETypeTransaction;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@Document(collection = "SavingAccountTransaction")
@AllArgsConstructor
@NoArgsConstructor
public class Transaction {
  
  @Id
  private String id;
  
  @NotNull
  private SavingAccount savingAccount;
  
  @NotBlank
  private String transactionCode;
  
  @NotNull
  private ETypeTransaction typeTransaction;
  
  @NotNull
  private Double transactionAmount;
  
  private Double commissionAmount;
  
  private LocalDateTime transactionDate;
  
}
