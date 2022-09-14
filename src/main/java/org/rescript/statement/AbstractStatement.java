package org.rescript.statement;

import org.rescript.ScriptException;

public abstract class AbstractStatement implements Statement {

  @Override
  public void addStatement(Statement statement) {
    throw new ScriptException("can't add statement to " + this);
  }

}
