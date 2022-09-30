package org.rescript.expression;

import org.rescript.ScriptException;
import org.rescript.run.ScriptContext;
import org.rescript.value.EvaluatedFromValue;
import org.rescript.value.Value;

public class UnaryPreIncOp implements Expression {

  private Expression expr;

  public UnaryPreIncOp(Expression expr) {
    super();
    this.expr = expr;
  }

  @Override
  public Value eval(ScriptContext ctx) {
    Value val = expr.eval(ctx);
    if(val.isNumber()) {
      if(val instanceof EvaluatedFromValue) {
        return ((EvaluatedFromValue)val).getSymbol().inc();
      } else {
        throw new ScriptException("invalid argument to unary pre increment, "+val);
      }
    } else {
      throw new ScriptException("unary pre incremenet requires number, " + val);
    }
  }

  @Override
  public String getText() {
    return "++" + expr.getText();
  }

  @Override
  public String toString() {
    return getText();
  }

}
