package org.rescript;

public class ReservedKeywordException extends SyntaxException {

  private static final long serialVersionUID = 8427794661227830570L;

  public ReservedKeywordException() {
    super();
  }

  public ReservedKeywordException(String message, Throwable cause, boolean enableSuppression,
      boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }

  public ReservedKeywordException(String message, Throwable cause) {
    super(message, cause);
  }

  public ReservedKeywordException(String message) {
    super(message);
  }

  public ReservedKeywordException(Throwable cause) {
    super(cause);
  }

}
