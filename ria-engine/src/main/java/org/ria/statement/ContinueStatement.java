package org.ria.statement;

import org.ria.ScriptException;
import org.ria.run.ScriptContext;

public class ContinueStatement extends AbstractStatement {

  public ContinueStatement(int lineNumber) {
    super(lineNumber);
  }

  @Override
  public void execute(ScriptContext ctx) {
    Continueable c = ctx.getCurrentFrame().peekContinueable();
    if(c != null) {
      c.setContinue();
    } else {
      throw new ScriptException("continue can only be used inside a loop and inside same function");
    }
  }

}
