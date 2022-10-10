package org.rescript.statement;

import org.rescript.expression.Expression;
import org.rescript.run.ScriptContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExpressionStatement implements Statement {

  private static final Logger log = LoggerFactory.getLogger(ExpressionStatement.class);

  private Expression expression;

  public ExpressionStatement(Expression expression) {
    super();
    this.expression = expression;
  }

  @Override
  public void execute(ScriptContext ctx) {
    log.debug("execute expression '{}'", expression);
    ctx.setLastResult(expression.eval(ctx));
  }

  @Override
  public String toString() {
    return "ExpressionStatement [expression=" + expression + "]";
  }

}
