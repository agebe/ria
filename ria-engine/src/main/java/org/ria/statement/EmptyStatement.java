package org.ria.statement;

import org.ria.run.ScriptContext;

public class EmptyStatement extends AbstractStatement implements Statement {

  public EmptyStatement(int lineNumber) {
    super(lineNumber);
  }

  @Override
  public void execute(ScriptContext ctx) {
  }

}