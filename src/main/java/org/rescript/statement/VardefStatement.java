package org.rescript.statement;

import java.util.List;

import org.rescript.run.ScriptContext;

public class VardefStatement implements Statement {

  private List<VarDef> vars;

  private String type;

  public VardefStatement(List<VarDef> vars, String type) {
    super();
    this.vars = vars;
    this.type = type;
  }

  @Override
  public void execute(ScriptContext ctx) {
    vars.forEach(v -> v.execute(ctx, type));
  }

}
