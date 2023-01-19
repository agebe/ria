package org.ria.expression;

import org.ria.ScriptException;
import org.ria.run.ScriptContext;
import org.ria.value.EvaluatedFromValue;
import org.ria.value.Value;

public class UnaryPostDecOp implements Expression {

  private Expression expr;

  public UnaryPostDecOp(Expression expr) {
    super();
    this.expr = expr;
  }

  @Override
  public Value eval(ScriptContext ctx) {
    Value val = expr.eval(ctx);
    if(val instanceof EvaluatedFromValue) {
      Value evalResult = ((EvaluatedFromValue)val).getSymbol().get();
      ((EvaluatedFromValue)val).getSymbol().dec();
      return evalResult;
    } else {
      throw new ScriptException("invalid argument to unary post decrement, "+val);
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
