package org.rescript.expression;

import org.rescript.ScriptException;
import org.rescript.run.ScriptContext;
import org.rescript.value.Value;

public class UnaryPostIncOp implements Expression {

  private Expression expr;

  public UnaryPostIncOp(Expression expr) {
    super();
    this.expr = expr;
  }

  @Override
  public Value eval(ScriptContext ctx) {
    Value val = expr.eval(ctx);
    if(val.isNumber()) {
      // here we need the object the expression was evaluated from
      // could have been a (global or local) script variable or
      // a (static or non-static) member field of a java object
      return val;
    } else {
      throw new ScriptException("unary post incremenet requires number, " + val);
    }
  }

  @Override
  public String getText() {
    return expr.getText() + "++";
  }

  @Override
  public String toString() {
    return getText();
  }

}
