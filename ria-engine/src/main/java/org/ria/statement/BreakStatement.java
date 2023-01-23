package org.ria.statement;

import org.ria.ScriptException;
import org.ria.run.ScriptContext;

public class BreakStatement extends AbstractStatement {

  public BreakStatement(int lineNumber) {
    super(lineNumber);
  }

  @Override
  public void execute(ScriptContext ctx) {
    Breakable b = ctx.getCurrentFrame().peekBreakable();
    if(b != null) {
      b.setBreak();
    } else {
      throw new ScriptException("break can only be used inside a loop or switch/case");
    }
  }

}