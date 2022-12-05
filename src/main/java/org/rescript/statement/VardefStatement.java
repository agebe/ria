package org.rescript.statement;

import java.util.List;

import org.rescript.parser.Type;
import org.rescript.run.ScriptContext;

public class VardefStatement implements Statement {

  private List<VarDef> vars;

  private Type type;

  public VardefStatement(List<VarDef> vars, Type type) {
    super();
    this.vars = vars;
    this.type = type;
  }

  @Override
  public void execute(ScriptContext ctx) {
    vars.forEach(v -> v.execute(ctx, type));
  }

  @Override
  public String toString() {
    return "VardefStatement [vars=" + vars + ", type=" + type + "]";
  }

}
