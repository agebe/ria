package org.rescript;

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
