package com.nttdata.transactionsaving.controller;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nttdata.transactionsaving.entity.Transaction;
import com.nttdata.transactionsaving.entity.dto.SavingAccount;
import com.nttdata.transactionsaving.service.ITransactionService;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@RestController
@RequestMapping("/transactionSavingAccount")
public class TransactionController {

  @Autowired
  ITransactionService service;

  @GetMapping("list")
  public Flux<Transaction> findAll() {
    return service.findAll();
  }

  @GetMapping("/find/{id}")
  public Mono<Transaction> findById(@PathVariable String id) {
    return service.findById(id);
  }

  @GetMapping("/buscar/{id}")
  public Mono<SavingAccount> findByIdSaving(@PathVariable String id) {
    return service.findSavingAccountById(id);
  }

  @PostMapping("/create")
  public Mono<ResponseEntity<Transaction>> create(
      @RequestBody Transaction savingAccountTransaction) {

    return service
        .countMovements(savingAccountTransaction.getSavingAccount().getId()) // N°
                                                                             // Movimientos
                                                                             // actuales
        .flatMap(cnt -> {
          log.info("id Cuenta Controlador: "
              + savingAccountTransaction.getSavingAccount().getId());
          return service
              .findSavingAccountById(
                  savingAccountTransaction.getSavingAccount().getId()) // Cuenta
                                                                       // Bancaria
              .filter(sa -> sa.getLimitTransactions() > cnt).flatMap(sa -> {
                switch (savingAccountTransaction.getTypeTransaction()) {
                  case DEPOSIT :
                    sa.setBalance(sa.getBalance()
                        + savingAccountTransaction.getTransactionAmount());
                    break;
                  case DRAFT :
                    sa.setBalance(sa.getBalance()
                        - savingAccountTransaction.getTransactionAmount());
                    break;
                }
                if (cnt >= sa.getFreeTransactions()) {
                  sa.setBalance(
                      sa.getBalance() - sa.getCommissionTransactions());
                  savingAccountTransaction
                      .setCommissionAmount(sa.getCommissionTransactions());
                } else {
                  savingAccountTransaction.setCommissionAmount(0.0);
                }

                return service.updateSavingAccount(sa).flatMap(saveAcc -> {
                  savingAccountTransaction.setSavingAccount(saveAcc);
                  savingAccountTransaction
                      .setTransactionDate(LocalDateTime.now());
                  return service.create(savingAccountTransaction);
                });
              }).map(sat -> new ResponseEntity<>(sat, HttpStatus.CREATED));
        }).defaultIfEmpty(new ResponseEntity<>(HttpStatus.BAD_REQUEST));
  }

  @PutMapping("/update")
  public Mono<ResponseEntity<Transaction>> update(
      @RequestBody Transaction transaction) {

    return service.findSavingAccountById(transaction.getSavingAccount().getId())
        .flatMap(sa -> {
          return service.findById(transaction.getId()).flatMap(sat -> {
            switch (transaction.getTypeTransaction()) {
              case DEPOSIT :
                sa.setBalance(sa.getBalance() - sat.getTransactionAmount()
                    + transaction.getTransactionAmount());
                return service.updateSavingAccount(sa).flatMap(saUpdate -> {
                  transaction.setSavingAccount(saUpdate);
                  transaction.setTransactionDate(LocalDateTime.now());
                  return service.update(transaction);
                });
              case DRAFT :
                sa.setBalance(sa.getBalance() + sat.getTransactionAmount()
                    - transaction.getTransactionAmount());
                return service.updateSavingAccount(sa).flatMap(saUpdate -> {
                  transaction.setSavingAccount(saUpdate);
                  transaction.setTransactionDate(LocalDateTime.now());
                  return service.update(transaction);
                });
              default :
                return Mono.empty();
            }
          });
        }).map(sat -> new ResponseEntity<>(sat, HttpStatus.CREATED))
        .defaultIfEmpty(new ResponseEntity<>(HttpStatus.BAD_REQUEST));
  }

  @DeleteMapping("/delete/{id}")
  public Mono<ResponseEntity<String>> delete(@PathVariable String id) {
    return service.delete(id).filter(deleteCustomer -> deleteCustomer)
        .map(deleteCustomer -> new ResponseEntity<>("Transaction Deleted",
            HttpStatus.ACCEPTED))
        .defaultIfEmpty(new ResponseEntity<>(HttpStatus.NOT_FOUND));
  }
}
