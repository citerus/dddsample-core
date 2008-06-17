package se.citerus.dddsample.service;

import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionException;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.SimpleTransactionStatus;

/**
 * Simple no-op transaction manager to allow for in-memory "persistence"
 * implementation to be plugged in transparently.
 */
public class InMemTransactionManager implements PlatformTransactionManager {

  public TransactionStatus getTransaction(TransactionDefinition transactionDefinition) throws TransactionException {
    return new SimpleTransactionStatus();
  }

  public void commit(TransactionStatus transactionStatus) throws TransactionException {
  }

  public void rollback(TransactionStatus transactionStatus) throws TransactionException {
  }

}
