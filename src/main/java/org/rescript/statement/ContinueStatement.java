package org.rescript.statement;

import org.rescript.ScriptException;
import org.rescript.run.ScriptContext;

public class ContinueStatement implements Statement {

  @Override
  public void execute(ScriptContext ctx) {
    Continueable c = ctx.getCurrentFrame().peekContinueable();
    if(c != null) {
      c.executeContinue();
    } else {
      throw new ScriptException("continue can only be used inside a loop and inside same function");
    }
  }

}
