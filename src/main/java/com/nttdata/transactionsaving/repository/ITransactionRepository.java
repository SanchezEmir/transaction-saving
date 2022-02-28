package com.nttdata.transactionsaving.repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

import com.nttdata.transactionsaving.entity.Transaction;

import reactor.core.publisher.Flux;

public interface ITransactionRepository extends ReactiveMongoRepository<Transaction, String> {
  
  Flux<Transaction> findBySavingAccountId(String id);

}
