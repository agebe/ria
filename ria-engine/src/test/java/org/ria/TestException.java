package org.ria;

public class TestException extends Exception {

  private static final long serialVersionUID = -6202039601863294636L;

  public TestException() {
    super();
  }

  public TestException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }

  public TestException(String message, Throwable cause) {
    super(message, cause);
  }

  public TestException(String message) {
    super(message);
  }

  public TestException(Throwable cause) {
    super(cause);
  }

}
