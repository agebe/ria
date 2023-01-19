package org.rescript;

public class SyntaxException extends ScriptException {

  private static final long serialVersionUID = 1L;

  public SyntaxException() {
    super();
  }

  public SyntaxException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }

  public SyntaxException(String message, Throwable cause) {
    super(message, cause);
  }

  public SyntaxException(String message) {
    super(message);
  }

  public SyntaxException(Throwable cause) {
    super(cause);
  }

}
