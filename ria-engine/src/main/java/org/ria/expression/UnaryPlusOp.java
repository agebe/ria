package org.ria.expression;

import org.ria.ScriptException;
import org.ria.run.ScriptContext;
import org.ria.value.IntValue;
import org.ria.value.Value;

public class UnaryPlusOp implements Expression {

  private Expression expr;

  public UnaryPlusOp(Expression expr) {
    super();
    this.expr = expr;
  }

  @Override
  public Value eval(ScriptContext ctx) {
    Value val = expr.eval(ctx);
    if(val.isChar()) {
      val = new IntValue(val.toInt());
    }
    // https://stackoverflow.com/a/2624541
    if(val.isNumber()) {
      return val.unbox();
    } else {
      throw new ScriptException("unary plus requires number, " + val);
    }
  }

  @Override
  public String getText() {
    return "+" + expr.getText();
  }

  @Override
  public String toString() {
    return getText();
  }

}
