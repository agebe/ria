package org.ria.firewall;

import org.ria.ScriptException;

public class AccessDeniedException extends ScriptException {

  private static final long serialVersionUID = 7225448774273448410L;

  public AccessDeniedException() {
    super();
  }

  public AccessDeniedException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }

  public AccessDeniedException(String message, Throwable cause) {
    super(message, cause);
  }

  public AccessDeniedException(String message) {
    super(message);
  }

  public AccessDeniedException(Throwable cause) {
    super(cause);
  }

}
