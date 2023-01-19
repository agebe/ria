package org.ria;

public class TestAutoCloseableWithException implements AutoCloseable {

  private boolean closed;

  public boolean isClosed() {
    return closed;
  }

  @Override
  public void close() throws Exception {
    closed = true;
    throw new TestException("exception on close (test)");
  }

}
