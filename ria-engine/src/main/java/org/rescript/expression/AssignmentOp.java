package org.rescript.expression;

import java.util.List;

import org.rescript.run.ScriptContext;
import org.rescript.value.Value;

public class AssignmentOp implements Assignment {

  private Identifier ident;

  private Expression expression;

  public AssignmentOp(Identifier ident, Expression expression) {
    super();
    this.ident = ident;
    this.expression = expression;
  }

  @Override
  public Value eval(ScriptContext ctx) {
    Value v = expression.eval(ctx);
    ctx.getSymbols().getScriptSymbols().assignVar(ident.getIdent(), v);
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

  @Override
  public List<Identifier> identifiers() {
    return List.of(ident);
  }

}
