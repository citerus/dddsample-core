package se.citerus.dddsample.service;

import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionException;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.SimpleTransactionStatus;

/**
 * 
 */
public class InMemTransactionManager implements PlatformTransactionManager {

  @Override
  public TransactionStatus getTransaction(TransactionDefinition transactionDefinition) throws TransactionException {
    return new SimpleTransactionStatus();
  }

  @Override
  public void commit(TransactionStatus transactionStatus) throws TransactionException {
  }

  @Override
  public void rollback(TransactionStatus transactionStatus) throws TransactionException {
  }

}
