package org.rescript.statement;

import org.rescript.expression.Expression;
import org.rescript.run.ScriptContext;
import org.rescript.value.Value;
import org.rescript.value.VoidValue;

public class VardefStatement extends AbstractStatement {

  private String name;

  private Expression initial;

  public VardefStatement(String name, Expression initial) {
    super();
    this.name = name;
    this.initial = initial;
  }

  @Override
  public void execute(ScriptContext ctx) {
    Value v = (initial!=null?initial.eval(ctx):new VoidValue());
    ctx.setLastResult(v);
    ctx.getSymbols().defineVar(name, v);
  }

  @Override
  public String toString() {
    return "VardefStatement [name=" + name + ", initial=" + initial + "]";
  }

}
