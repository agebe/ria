package org.ria.expression;

import org.ria.run.ScriptContext;
import org.ria.value.Value;

public class TernaryOp implements Expression {

  private Expression predicate;

  private Expression trueExpression;

  private Expression falseExpression;

  public TernaryOp(Expression predicate, Expression trueExpression, Expression falseExpression) {
    super();
    this.predicate = predicate;
    this.trueExpression = trueExpression;
    this.falseExpression = falseExpression;
  }

  @Override
  public Value eval(ScriptContext ctx) {
    return predicate.eval(ctx).toBoolean()?trueExpression.eval(ctx):falseExpression.eval(ctx);
  }

  @Override
  public String getText() {
    return predicate.getText()+"?"+trueExpression.getText()+":"+falseExpression.getText();
  }

}
