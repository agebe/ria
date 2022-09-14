package org.rescript.expression;

import org.rescript.run.ScriptContext;
import org.rescript.value.Value;

public class AssignmentOperator implements Expression {

  private Identifier ident;

  private Expression expression;

  public AssignmentOperator(Identifier ident, Expression expression) {
    super();
    this.ident = ident;
    this.expression = expression;
  }

  @Override
  public Value eval(ScriptContext ctx) {
    Value v = expression.eval(ctx);
    ctx.getSymbols().assignVar(ident.getText(), v);
    return v;
  }

  @Override
  public String getText() {
    return ident.getText() + "=" + expression.getText();
  }

  @Override
  public String toString() {
    return "AssignmentOperator [ident=" + ident + ", expression=" + expression + "]";
  }

}
