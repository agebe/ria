package io.github.agebe.script;

public class LexerException extends RestrictedScriptException {

  private static final long serialVersionUID = 1L;

  public LexerException() {
    super();
  }

  public LexerException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }

  public LexerException(String message, Throwable cause) {
    super(message, cause);
  }

  public LexerException(String message) {
    super(message);
  }

  public LexerException(Throwable cause) {
    super(cause);
  }

}
