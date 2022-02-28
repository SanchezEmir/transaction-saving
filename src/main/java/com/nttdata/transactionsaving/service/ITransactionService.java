package com.nttdata.transactionsaving.service;

import com.nttdata.transactionsaving.entity.Transaction;
import com.nttdata.transactionsaving.entity.dto.SavingAccount;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ITransactionService {

  Mono<Transaction> create(Transaction t);

  Flux<Transaction> findAll();

  Mono<Transaction> findById(String id);

  Mono<Transaction> update(Transaction t);

  Mono<Boolean> delete(String t);

  Mono<Long> countMovements(String t);

  Mono<SavingAccount> findSavingAccountById(String id);

  Mono<SavingAccount> updateSavingAccount(SavingAccount sa);

}
