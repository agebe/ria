package org.rescript.statement;

import org.rescript.ScriptException;
import org.rescript.run.ScriptContext;

public class BreakStatement implements Statement {

  @Override
  public void execute(ScriptContext ctx) {
    Breakable b = ctx.getCurrentFrame().peekBreakable();
    if(b != null) {
      b.setBreak();
    } else {
      throw new ScriptException("break can only be used inside a loop and inside same function");
    }
  }

}
