package org.rescript.expression;

import org.rescript.ScriptException;
import org.rescript.run.ScriptContext;
import org.rescript.value.EvaluatedFromValue;
import org.rescript.value.Value;

public class UnaryPreDecOp implements Expression {

  private Expression expr;

  public UnaryPreDecOp(Expression expr) {
    super();
    this.expr = expr;
  }

  @Override
  public Value eval(ScriptContext ctx) {
    Value val = expr.eval(ctx);
    if(val instanceof EvaluatedFromValue) {
      return ((EvaluatedFromValue)val).getSymbol().dec();
    } else {
      throw new ScriptException("invalid argument to unary pre decrement, "+val);
    }
  }

  @Override
  public String getText() {
    return "--" + expr.getText();
  }

  @Override
  public String toString() {
    return getText();
  }

}
