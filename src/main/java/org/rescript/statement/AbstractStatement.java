package org.rescript.statement;

public abstract class AbstractStatement implements Statement {

  private int lineNumber;

  public AbstractStatement(int lineNumber) {
    this.lineNumber = lineNumber;
  }

//  public AbstractStatement() {
//    super();
//  }

  @Override
  public int getLineNumber() {
    return lineNumber;
  }

  @Override
  public void setLineNumber(int lineNumber) {
    this.lineNumber = lineNumber;
  }

}
