package org.ria.expression;

import org.ria.ScriptException;
import org.ria.run.ScriptContext;
import org.ria.value.BooleanValue;
import org.ria.value.Value;

public class UnaryLogicalNotOp implements Expression {

  private Expression expr;

  public UnaryLogicalNotOp(Expression expr) {
    super();
    this.expr = expr;
  }

  @Override
  public Value eval(ScriptContext ctx) {
    Value val = expr.eval(ctx);
    if(val.isBoolean()) {
      return new BooleanValue(!val.toBoolean());
    } else {
      throw new ScriptException("logical not requires boolean value, " + val);
      
    }
  }

  @Override
  public String getText() {
    return "!" + expr.getText();
  }

  @Override
  public String toString() {
    return getText();
  }

}
