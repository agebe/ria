package io.github.agebe.script.symbol;

import io.github.agebe.script.ScriptException;

public class SymbolNotFoundException extends ScriptException {

  private static final long serialVersionUID = -5354168913629174616L;

  public SymbolNotFoundException() {
    super();
  }

  public SymbolNotFoundException(String message, Throwable cause, boolean enableSuppression,
      boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }

  public SymbolNotFoundException(String message, Throwable cause) {
    super(message, cause);
  }

  public SymbolNotFoundException(String message) {
    super(message);
  }

  public SymbolNotFoundException(Throwable cause) {
    super(cause);
  }

}