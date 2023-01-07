package org.rescript.statement;

import org.rescript.dependency.Dependencies;
import org.rescript.run.ScriptContext;
import org.rescript.value.Value;

public class HeaderEnterStatement extends AbstractStatement {

  public HeaderEnterStatement(int lineNumber) {
    super(lineNumber);
  }

  @Override
  public void execute(ScriptContext ctx) {
    if(!ctx.getSymbols().getScriptSymbols().isDefined("dependencies")) {
      ctx.getSymbols().getScriptSymbols().defineOrAssignVarRoot(
          "dependencies", Value.of(new Dependencies()));
    }
  }

}
