package org.rescript.expression;

import org.rescript.run.ScriptContext;
import org.rescript.value.BooleanValue;
import org.rescript.value.Value;

public class LogicalAndOp implements Expression {

  private Expression exp1;

  private Expression exp2;

  public LogicalAndOp(Expression exp1, Expression exp2) {
    super();
    this.exp1 = exp1;
    this.exp2 = exp2;
  }

  @Override
  public Value eval(ScriptContext ctx) {
    if(exp1.eval(ctx).toBoolean() == false) {
      return BooleanValue.FALSE;
    }
    return BooleanValue.valueOf(exp2.eval(ctx).toBoolean());
  }

  @Override
  public String getText() {
    return exp1.getText() + "&&" + exp2.getText();
  }

}
