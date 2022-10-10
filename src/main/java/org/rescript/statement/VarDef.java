package org.rescript.statement;

import org.rescript.expression.Expression;
import org.rescript.run.ScriptContext;
import org.rescript.value.Value;
import org.rescript.value.VoidValue;

public class VarDef {

  private String name;

  private Expression initial;

  public VarDef(String name, Expression initial) {
    super();
    this.name = name;
    this.initial = initial;
  }

  public void execute(ScriptContext ctx) {
    Value v = (initial!=null?initial.eval(ctx):new VoidValue());
    ctx.setLastResult(v);
    ctx.getSymbols().getScriptSymbols().defineVar(name, v);
  }

}
