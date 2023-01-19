package org.ria;

public class TestAutoCloseable implements AutoCloseable {

  private boolean closed;

  public boolean isClosed() {
    return closed;
  }

  @Override
  public void close() throws Exception {
    closed = true;
  }

}
