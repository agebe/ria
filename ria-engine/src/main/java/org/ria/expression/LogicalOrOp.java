package org.ria.expression;

import org.ria.run.ScriptContext;
import org.ria.value.BooleanValue;
import org.ria.value.Value;

public class LogicalOrOp implements Expression {

  private Expression exp1;

  private Expression exp2;

  public LogicalOrOp(Expression exp1, Expression exp2) {
    super();
    this.exp1 = exp1;
    this.exp2 = exp2;
  }

  @Override
  public Value eval(ScriptContext ctx) {
    if(exp1.eval(ctx).toBoolean() == true) {
      return BooleanValue.TRUE;
    }
    return BooleanValue.valueOf(exp2.eval(ctx).toBoolean());
  }

  @Override
  public String getText() {
    return exp1.getText() + "||" + exp2.getText();
  }

}