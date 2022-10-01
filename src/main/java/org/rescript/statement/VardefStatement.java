package org.rescript.statement;

import java.util.List;

import org.rescript.run.ScriptContext;

public class VardefStatement extends AbstractStatement {

  private List<VarDef> vars;

  public VardefStatement(List<VarDef> vars) {
    super();
    this.vars = vars;
  }

  @Override
  public void execute(ScriptContext ctx) {
    vars.forEach(v -> v.execute(ctx));
  }

}
