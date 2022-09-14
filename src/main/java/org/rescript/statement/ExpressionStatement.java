package org.rescript.statement;

import org.rescript.expression.Expression;
import org.rescript.run.ScriptContext;

public class ExpressionStatement extends AbstractStatement {

  private Expression expression;

  public ExpressionStatement(Expression expression) {
    super();
    this.expression = expression;
  }

  @Override
  public void execute(ScriptContext ctx) {
    ctx.setLastResult(expression.eval(ctx));
  }

  @Override
  public String toString() {
    return "ExpressionStatement [expression=" + expression + "]";
  }

}
