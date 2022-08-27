package io.github.agebe.script;

public class RestrictedScriptException extends RuntimeException {

  private static final long serialVersionUID = 1L;

  public RestrictedScriptException() {
    super();
  }

  public RestrictedScriptException(String message, Throwable cause, boolean enableSuppression,
      boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }

  public RestrictedScriptException(String message, Throwable cause) {
    super(message, cause);
  }

  public RestrictedScriptException(String message) {
    super(message);
  }

  public RestrictedScriptException(Throwable cause) {
    super(cause);
  }

}
