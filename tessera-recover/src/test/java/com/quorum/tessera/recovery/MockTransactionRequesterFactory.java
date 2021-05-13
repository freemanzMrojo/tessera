package com.quorum.tessera.recovery;

import static org.mockito.Mockito.mock;

import com.quorum.tessera.config.Config;
import com.quorum.tessera.recovery.resend.BatchTransactionRequester;
import com.quorum.tessera.recovery.resend.BatchTransactionRequesterFactory;

public class MockTransactionRequesterFactory implements BatchTransactionRequesterFactory {

  private static BatchTransactionRequester transactionRequester;

  static void setTransactionRequester(BatchTransactionRequester tr) {
    transactionRequester = tr;
  }

  @Override
  public BatchTransactionRequester createBatchTransactionRequester(Config config) {
    if (transactionRequester == null) {
      return mock(BatchTransactionRequester.class);
    }
    return transactionRequester;
  }
}
