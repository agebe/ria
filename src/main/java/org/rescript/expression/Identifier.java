package org.rescript.expression;

import org.rescript.run.ScriptContext;
import org.rescript.value.Value;

public class Identifier implements TargetExpression {

  private String ident;

  public Identifier(String ident) {
    super();
    this.ident = ident;
  }

  public String getIdent() {
    return ident;
  }

  @Override
  public String toString() {
    return "Identifier [ident=" + ident + "]";
  }

  @Override
  public Value eval(ScriptContext ctx) {
    return ctx.getSymbols().resolve(null, ident);
  }

  @Override
  public Value eval(ScriptContext ctx, Value target) {
    return ctx.getSymbols().resolve(target, ident);
  }

  @Override
  public String getText() {
    return ident;
  }

}
