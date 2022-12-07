package org.rescript;

public class CheckedExceptionWrapper extends ScriptException {

  private static final long serialVersionUID = -6953872728903451267L;

  public CheckedExceptionWrapper() {
    super();
  }

  public CheckedExceptionWrapper(String message, Throwable cause, boolean enableSuppression,
      boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }

  public CheckedExceptionWrapper(String message, Throwable cause) {
    super(message, cause);
  }

  public CheckedExceptionWrapper(String message) {
    super(message);
  }

  public CheckedExceptionWrapper(Throwable cause) {
    super(cause);
  }

}
