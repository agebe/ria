package org.rescript.expression;

import org.rescript.ScriptException;
import org.rescript.run.ScriptContext;
import org.rescript.value.Value;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DotOperator implements Expression {

  private static final Logger log = LoggerFactory.getLogger(DotOperator.class);

  private Expression expr1;

  private TargetExpression expr2;

  public DotOperator(Expression expr1, TargetExpression expr2) {
    super();
    this.expr1 = expr1;
    this.expr2 = expr2;
  }

  @Override
  public Value eval(ScriptContext ctx) {
    Value val = expr1.eval(ctx);
    log.debug("eval expr1 '{}' to '{}', isNull '{}'", expr1, val, val!=null?val.isNull():true);
    if(val != null) {
      return expr2.eval(ctx, val);
    } else {
      throw new ScriptException("left expression '%s' evaluated to null".formatted(expr1));
    }
  }

  @Override
  public String getText() {
    return expr1 + "." + expr2;
  }

  @Override
  public String toString() {
    return getText();
  }

}
