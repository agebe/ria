package org.rescript.expression;

import org.rescript.ScriptException;
import org.rescript.run.ScriptContext;
import org.rescript.value.EvaluatedFromValue;
import org.rescript.value.Value;

public class UnaryPostDecOp implements Expression {

  private Expression expr;

  public UnaryPostDecOp(Expression expr) {
    super();
    this.expr = expr;
  }

  @Override
  public Value eval(ScriptContext ctx) {
    Value val = expr.eval(ctx);
    if(val.isNumber()) {
      if(val instanceof EvaluatedFromValue) {
        ((EvaluatedFromValue)val).getSymbol().dec();
      } else {
        throw new ScriptException("invalid argument to unary post decrement, "+val);
      }
      return val;
    } else {
      throw new ScriptException("unary post decrement requires number, " + val);
    }
  }

  @Override
  public String getText() {
    return expr.getText() + "--";
  }

  @Override
  public String toString() {
    return getText();
  }

}
