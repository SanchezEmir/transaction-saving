package com.nttdata.transactionsaving.service.impl;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.circuitbreaker.resilience4j.ReactiveResilience4JCircuitBreakerFactory;
import org.springframework.cloud.client.circuitbreaker.ReactiveCircuitBreaker;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.nttdata.transactionsaving.entity.Transaction;
import com.nttdata.transactionsaving.entity.dto.SavingAccount;
import com.nttdata.transactionsaving.repository.ITransactionRepository;
import com.nttdata.transactionsaving.service.ITransactionService;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@Service
public class TransactionServiceImpl implements ITransactionService {

  private final WebClient webClient, webClientSa;
  private final ReactiveCircuitBreaker reactiveCircuitBreaker;

  @Value("${config.base.apigateway}")
  private String url;

  public TransactionServiceImpl(
      ReactiveResilience4JCircuitBreakerFactory circuitBreakerFactory) {
    this.webClient = WebClient.builder().baseUrl(this.url).build();
    this.webClientSa = WebClient.builder().baseUrl(this.url).build();
    this.reactiveCircuitBreaker = circuitBreakerFactory.create("customer");
  }

  @Autowired
  private ITransactionRepository dao;

  @Override
  public Mono<SavingAccount> findSavingAccountById(String id) {
    log.info("ruta: " + url);
    log.info("buscando saving account: " + id);
    Map<String, Object> params = new HashMap<>();
    params.put("id", id);
    // return
    // webClientSa.get().uri(this.url+"/find/{id}",params).accept(MediaType.APPLICATION_JSON).retrieve().bodyToMono(SavingAccount.class);
    return reactiveCircuitBreaker
        .run(webClient.get().uri(this.url + "/savingAccount/find/{id}", id)
            .accept(MediaType.APPLICATION_JSON).retrieve()
            .bodyToMono(SavingAccount.class), throwable -> {
              return this.getDefaultSavingAccount();
            });
  }

  @Override
  public Mono<SavingAccount> updateSavingAccount(SavingAccount sa) {
    return reactiveCircuitBreaker
        .run(webClient.put().uri(this.url + "/savingAccount/update", sa)
            .accept(MediaType.APPLICATION_JSON).syncBody(sa).retrieve()
            .bodyToMono(SavingAccount.class), throwable -> {
              return this.getDefaultSavingAccount();
            });
  }

  public Mono<SavingAccount> getDefaultSavingAccount() {
    log.info("no encontro peticion");
    Mono<SavingAccount> savingAccount = Mono.just(new SavingAccount("0", null,
        null, null, null, null, null, null, null, null, null));
    return savingAccount;
  }

  @Override
  public Mono<Transaction> create(Transaction t) {
    return dao.save(t);
  }

  @Override
  public Flux<Transaction> findAll() {
    return dao.findAll();
  }

  @Override
  public Mono<Transaction> findById(String id) {
    return dao.findById(id);
  }

  @Override
  public Mono<Transaction> update(Transaction t) {
    return dao.save(t);
  }

  @Override
  public Mono<Boolean> delete(String t) {
    return dao.findById(t)
        .flatMap(tar -> dao.delete(tar).then(Mono.just(Boolean.TRUE)))
        .defaultIfEmpty(Boolean.FALSE);
  }

  @Override
  public Mono<Long> countMovements(String t) {
    return dao.findBySavingAccountId(t).count();
  }
}
