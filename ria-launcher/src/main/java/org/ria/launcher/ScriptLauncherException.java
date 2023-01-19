package org.ria.launcher;

import org.ria.ScriptException;

public class ScriptLauncherException extends ScriptException {

  private static final long serialVersionUID = 2185939989116326067L;

  public ScriptLauncherException() {
    super();
  }

  public ScriptLauncherException(String message, Throwable cause, boolean enableSuppression,
      boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }

  public ScriptLauncherException(String message, Throwable cause) {
    super(message, cause);
  }

  public ScriptLauncherException(String message) {
    super(message);
  }

  public ScriptLauncherException(Throwable cause) {
    super(cause);
  }

}
